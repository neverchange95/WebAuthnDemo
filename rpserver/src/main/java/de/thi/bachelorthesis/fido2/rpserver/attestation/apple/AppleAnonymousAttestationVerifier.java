package de.thi.bachelorthesis.fido2.rpserver.attestation.apple;

import de.thi.bachelorthesis.fido2.rpserver.attestation.AttestationVerificationResult;
import de.thi.bachelorthesis.fido2.rpserver.attestation.AttestationVerifier;
import de.thi.bachelorthesis.fido2.rpserver.error.InternalErrorCode;
import de.thi.bachelorthesis.fido2.rpserver.exception.FIDO2ServerRuntimeException;
import de.thi.bachelorthesis.fido2.rpserver.general.AttestationType;
import de.thi.bachelorthesis.fido2.rpserver.general.crypto.Digests;
import de.thi.bachelorthesis.fido2.rpserver.helper.CredentialPublicKeyHelper;
import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatement;
import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatementFormatIdentifier;
import de.thi.bachelorthesis.fido2.rpserver.model.AuthenticatorData;
import de.thi.bachelorthesis.fido2.rpserver.util.CertificateUtil;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Component
public class AppleAnonymousAttestationVerifier implements AttestationVerifier {
    private static final String APPLE_ANONYMOUS_ATTESTATION_OID = "1.2.840.113635.100.8.2";

    @Override
    public AttestationStatementFormatIdentifier getIdentifier() {
        return AttestationStatementFormatIdentifier.APPLE_ANONYMOUS;
    }

    @Override
    public AttestationVerificationResult verify(AttestationStatement attestationStatement, AuthenticatorData authenticatorData, byte[] clientDataHash) {
        AppleAnonymousAttestationStatement appleAnonymous = (AppleAnonymousAttestationStatement) attestationStatement;

        byte[] expectedNonceToHash = ByteBuffer
                .allocate(authenticatorData.getBytes().length + clientDataHash.length)
                .put(authenticatorData.getBytes())
                .put(clientDataHash)
                .array();

        if (appleAnonymous.getX5c() != null &&
                !appleAnonymous.getX5c().isEmpty()) {
            List<Certificate> certificateList;
            try {
                certificateList = CertificateUtil.getCertificates(appleAnonymous.getX5c());
            } catch (CertificateException e) {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.ATTESTATION_CERTIFICATE_ERROR, e);
            }

            X509Certificate certificate = (X509Certificate) certificateList.get(0);
            byte[] expectedNonce = Digests.sha256(expectedNonceToHash);
            ASN1Sequence credCertASN1Sequence = extractASN1Sequence(certificate);
            byte[] certNonce = getNonceFromCredCert(credCertASN1Sequence);

            if (!Arrays.equals(expectedNonce, certNonce)) {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.SIGNATURE_VERIFICATION_ERROR);
            }

            byte[] attestedCredPubKey = CredentialPublicKeyHelper.convert(authenticatorData.getAttestedCredentialData().getCredentialPublicKey()).getEncoded();
            byte[] certPubKey = certificate.getPublicKey().getEncoded();

            if (!Arrays.equals(attestedCredPubKey, certPubKey)) {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.APPLE_ANONYMOUS_ATTESTATION_PUBLIC_KEY_NOT_MATCHED);
            }

            return AttestationVerificationResult
                    .builder()
                    .success(true)
                    .type(AttestationType.ANON_CA)
                    .trustPath(certificateList)
                    .format(AttestationStatementFormatIdentifier.APPLE_ANONYMOUS)
                    .build();
        }

        throw new FIDO2ServerRuntimeException(InternalErrorCode.INVALID_ATTESTATION_FORMAT);
    }

    private ASN1Sequence extractASN1Sequence(X509Certificate certificate) {
        byte[] attestationExtensionBytes = certificate.getExtensionValue(APPLE_ANONYMOUS_ATTESTATION_OID);
        if (attestationExtensionBytes == null
                || attestationExtensionBytes.length == 0) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.APPLE_ANONYMOUS_ATTESTATION_DATA_NOT_FOUND, "Couldn't find the apple anonymous attestation extension data.");
        }

        ASN1Sequence decodedSequence;
        try {
            try (ASN1InputStream asn1InputStream = new ASN1InputStream(attestationExtensionBytes)) {
                // The extension contains one object, a sequence, in the
                // Distinguished Encoding Rules (DER)-encoded form. Get the DER
                // bytes.
                byte[] derSequenceBytes = ((ASN1OctetString) asn1InputStream
                        .readObject()).getOctets();
                // Decode the bytes as an ASN1 sequence object.
                try (ASN1InputStream seqInputStream = new ASN1InputStream(derSequenceBytes)) {
                    decodedSequence = (ASN1Sequence) seqInputStream.readObject();
                }
            }
        } catch (IOException e) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.APPLE_ANONYMOUS_ATTESTATION_DATA_DECODING_FAIL, e);
        }
        return decodedSequence;
    }

    private byte[] getNonceFromCredCert(ASN1Sequence credCertASN1Sequence) {
        DERTaggedObject sequenceObject = (DERTaggedObject) credCertASN1Sequence.getObjectAt(0);
        return ASN1OctetString.getInstance(sequenceObject.getObject()).getOctets();
    }
}
