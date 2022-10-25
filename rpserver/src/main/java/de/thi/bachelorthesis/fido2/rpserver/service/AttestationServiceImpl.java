package de.thi.bachelorthesis.fido2.rpserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import de.thi.bachelorthesis.fido2.rpserver.attestation.AttestationVerificationResult;
import de.thi.bachelorthesis.fido2.rpserver.attestation.AttestationVerifierFactory;
import de.thi.bachelorthesis.fido2.rpserver.attestation.android.keyattestation.AdditionalRevokeChecker;
import de.thi.bachelorthesis.fido2.rpserver.attestation.android.keyattestation.RevokeCheckerClient;
import de.thi.bachelorthesis.fido2.rpserver.error.InternalErrorCode;
import de.thi.bachelorthesis.fido2.rpserver.exception.FIDO2ServerRuntimeException;
import de.thi.bachelorthesis.fido2.rpserver.general.AttestationType;
import de.thi.bachelorthesis.fido2.rpserver.general.AuthenticatorSelectionCriteria;
import de.thi.bachelorthesis.fido2.rpserver.general.ServerAuthenticatorAttestationResponse;
import de.thi.bachelorthesis.fido2.rpserver.general.UserVerificationRequirement;
import de.thi.bachelorthesis.fido2.rpserver.general.crypto.Digests;
import de.thi.bachelorthesis.fido2.rpserver.general.uaf.MetadataStatement;
import de.thi.bachelorthesis.fido2.rpserver.model.AttestationObject;
import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatementFormatIdentifier;
import de.thi.bachelorthesis.fido2.rpserver.util.AaguidUtil;
import de.thi.bachelorthesis.fido2.rpserver.util.CertPathUtil;
import de.thi.bachelorthesis.fido2.rpserver.util.CertificateUtil;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Service
public class AttestationServiceImpl implements AttestationService {
    private final MetadataService metadataService;
    private final VendorSpecificMetadataService vendorSpecificMetadataService;
    private final AttestationVerifierFactory attestationVerifierFactory;
    private final RevokeCheckerClient revokeCheckerClient;

    private boolean acceptUnregisteredAuthenticators = true;

    @Autowired
    public AttestationServiceImpl(MetadataService metadataService, VendorSpecificMetadataService vendorSpecificMetadataService, AttestationVerifierFactory attestationVerifierFactory, RevokeCheckerClient revokeCheckerClient) {
        this.metadataService = metadataService;
        this.vendorSpecificMetadataService = vendorSpecificMetadataService;
        this.attestationVerifierFactory = attestationVerifierFactory;
        this.revokeCheckerClient = revokeCheckerClient;
    }

    @Override
    public AttestationVerificationResult verifyAttestation(byte[] clientDataHsh, AttestationObject attestationObject) {
        AttestationVerificationResult attestationVerificationResult =
                attestationVerifierFactory
                        .getVerifier(attestationObject.getFmt())
                        .verify(attestationObject.getAttStmt(), attestationObject.getAuthData(), clientDataHsh);
        return attestationVerificationResult;
    }

    @Override
    public AttestationObject getAttestationObject(ServerAuthenticatorAttestationResponse attestationResponse) {
        byte[] attestationObjectBytes = Base64
                .getUrlDecoder()
                .decode(attestationResponse.getAttestationObject());

        // perform CBOR decoding
        CBORFactory cborFactory = new CBORFactory();
        ObjectMapper objectMapper = new ObjectMapper(cborFactory);
        AttestationObject attestationObject;
        try {
            attestationObject = objectMapper.readValue(attestationObjectBytes, AttestationObject.class);
        } catch (IOException e) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.INVALID_FORMAT_ATTESTATION_OBJECT, e);
        }
        return attestationObject;
    }

    @Override
    public void attestationObjectValidationCheck(String rpId, AuthenticatorSelectionCriteria authenticatorSelection, AttestationObject attestationObject) {
        // verify attestationObject.authData.attestedCredentialData
        if (attestationObject.getAuthData().getAttestedCredentialData() == null) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.CREDENTIAL_NOT_INCLUDED);
        }

        // verify RP ID (compare with SHA256 hash or RP ID)
        byte[] rpIdHash = Digests.sha256(rpId.getBytes(StandardCharsets.UTF_8));
        if (!Arrays.equals(attestationObject.getAuthData().getRpIdHash(), rpIdHash)) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.RPID_HASH_NOT_MATCHED, "RP ID hash is not matched", AaguidUtil.convert(attestationObject.getAuthData().getAttestedCredentialData().getAaguid()));
        }

        // verify user present flag
        if (!attestationObject.getAuthData().isUserPresent()) {
            // Temporary comment out for Android chrome testings
//            throw new FIDO2ServerRuntimeException(InternalErrorCode.USER_PRESENCE_FLAG_NOT_SET);
        }

        // verify user verification
        if (authenticatorSelection != null &&
                authenticatorSelection.getUserVerification() != null &&
                authenticatorSelection.getUserVerification() == UserVerificationRequirement.REQUIRED &&
                !attestationObject.getAuthData().isUserVerified()) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.USER_VERIFICATION_FLAG_NOT_SET, "User verification flag not set", AaguidUtil.convert(attestationObject.getAuthData().getAttestedCredentialData().getAaguid()));
        }
    }

    @Override
    public void verifyAttestationCertificate(AttestationObject attestationObject, AttestationVerificationResult attestationVerificationResult) {
        // verify trustworthiness
        // check the attestation certificate chained up to root certificates
        // if fails, SHOULD reject the registration

        byte[] aaguid = attestationObject.getAuthData().getAttestedCredentialData().getAaguid();
        MetadataStatement metadataStatement = null;
        if (aaguid != null && !Arrays.equals(aaguid, new byte[16])) {   // aaguid value for u2f is zeroed
            String aaguidString = AaguidUtil.convert(aaguid);
            metadataStatement = metadataService.getMetadataStatementWithAaguid(aaguidString);
        } else {
            System.out.println("empty AAGUID");
        }
        List<String> attestationRootCertificates;
        if (metadataStatement != null) {
            attestationRootCertificates = metadataStatement.getAttestationRootCertificates();
        } else {
            attestationRootCertificates = CertificateUtil.getAttestationRootCertificates(vendorSpecificMetadataService, attestationVerificationResult, metadataService.getAllU2FMetadataStatements());
        }
        // format specific handling

        // set attestation root certificate with metadata or vendor specific data
        // or skip getting metadata
        if (!acceptUnregisteredAuthenticators) {    // throw an error if there is no metadata
            if (attestationRootCertificates == null) {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.METADATA_NOT_FOUND);
            }
        }

        if (attestationRootCertificates != null) {
            verifyCertificateChainOfTrust(attestationObject, attestationVerificationResult, attestationRootCertificates);
        }
    }

    private void verifyCertificateChainOfTrust(AttestationObject attestationObject, AttestationVerificationResult attestationVerificationResult, List<String> attestationRootCertificates) {
        try {
            Set<TrustAnchor> trustAnchors = CertificateUtil.getTrustAnchors(attestationRootCertificates);

            boolean matched = isTopIntermediateCertificateSameWithRootCertificates(attestationVerificationResult, trustAnchors, attestationVerificationResult.getTrustPath().size());

            if (isSelfSignedAttestation(matched, attestationVerificationResult.getType()
                    == AttestationType.BASIC, attestationVerificationResult.getTrustPath().size() == 1)) {
                //Doesn't need to verify cert chain for Self Signed Attestation.
                return;
            }

            if (matched) {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.CERTIFICATE_PATH_VALIDATION_FAIL,
                        "Top intermediate certificate includes one of attestation root certificates");
            }

            boolean enableRevocation = hasCRLDistPointForRevokeCheck(attestationVerificationResult);

            if (attestationObject.getFmt() == AttestationStatementFormatIdentifier.ANDROID_KEY) {
                if (AdditionalRevokeChecker.hasAndroidKeyAttestationRevokedCert(revokeCheckerClient,attestationVerificationResult.getTrustPath())) {
                    throw new FIDO2ServerRuntimeException(InternalErrorCode.CERTIFICATE_PATH_VALIDATION_FAIL);
                }
            }

            boolean result = CertPathUtil.validate(attestationVerificationResult.getTrustPath(),
                    trustAnchors, enableRevocation);

            if (!result) {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.CERTIFICATE_PATH_VALIDATION_FAIL);
            }

        } catch (GeneralSecurityException | IOException e) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.CRYPTO_OPERATION_EXCEPTION,
                    "Cert path validation algorithm parameter error", e);
        }
    }

    private boolean hasCRLDistPointForRevokeCheck(AttestationVerificationResult attestationVerificationResult) throws IOException {
        X509Certificate leafCert = (X509Certificate) attestationVerificationResult.getTrustPath().get(0);

        byte[] cdpExt = leafCert.getExtensionValue(Extension.cRLDistributionPoints.getId());
        CRLDistPoint cdp = null;
        if (cdpExt != null) {
            cdp = CRLDistPoint.getInstance(X509ExtensionUtil.fromExtensionValue(cdpExt));
        }
        boolean enableRevocation = cdpExt != null;
        return enableRevocation;
    }

    private boolean isTopIntermediateCertificateSameWithRootCertificates(AttestationVerificationResult attestationVerificationResult, Set<TrustAnchor> trustAnchors, int size) {
        // check top intermediate certificate is included in attestation root certificates
        boolean matched = trustAnchors
                .stream()
                .anyMatch(e -> {
                    try {
                        return Arrays.equals(e.getTrustedCert().getEncoded(),
                                attestationVerificationResult.getTrustPath().get(size - 1)
                                        .getEncoded());
                    } catch (CertificateEncodingException e1) {
                        return false;
                    }
                });
        return matched;
    }

    private boolean isSelfSignedAttestation(boolean matched, boolean isBasicAttestationType, boolean hasOnlyOneTrustPath) {
        return matched && isBasicAttestationType && hasOnlyOneTrustPath;
    }
}
