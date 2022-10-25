package de.thi.bachelorthesis.fido2.rpserver.attestation.u2f;

import de.thi.bachelorthesis.fido2.rpserver.attestation.AttestationVerificationResult;
import de.thi.bachelorthesis.fido2.rpserver.attestation.AttestationVerifier;
import de.thi.bachelorthesis.fido2.rpserver.error.InternalErrorCode;
import de.thi.bachelorthesis.fido2.rpserver.exception.FIDO2ServerRuntimeException;
import de.thi.bachelorthesis.fido2.rpserver.general.AttestationType;
import de.thi.bachelorthesis.fido2.rpserver.general.COSEAlgorithm;
import de.thi.bachelorthesis.fido2.rpserver.helper.SignatureHelper;
import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatement;
import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatementFormatIdentifier;
import de.thi.bachelorthesis.fido2.rpserver.model.AuthenticatorData;
import de.thi.bachelorthesis.fido2.rpserver.model.ECCKey;
import de.thi.bachelorthesis.fido2.rpserver.util.CertificateUtil;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.ECPublicKey;
import java.util.Arrays;
import java.util.List;

@Component
public class FidoU2fAttestationVerifier implements AttestationVerifier {
    final byte[] U2F_STATIC_AAGUID = new byte[16];

    @Override
    public AttestationStatementFormatIdentifier getIdentifier() {
        return AttestationStatementFormatIdentifier.FIDO_U2F;
    }

    @Override
    public AttestationVerificationResult verify(AttestationStatement attestationStatement, AuthenticatorData authenticatorData, byte[] clientDataHash) {
        FidoU2fAttestationStatement fidoU2f = (FidoU2fAttestationStatement) attestationStatement;

        // check validity
        if (fidoU2f.getSig() == null ||
                fidoU2f.getSig().length == 0 ||
                fidoU2f.getX5c() == null || fidoU2f.getX5c().size() != 1) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.INVALID_ATTESTATION_FORMAT);
        }

        List<Certificate> certificates;
        try {
            certificates = CertificateUtil.getCertificates(fidoU2f.getX5c());
        } catch (CertificateException e) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.ATTESTATION_CERTIFICATE_ERROR, e);
        }

        // check aaguid, aaguid shoud be 0x00
        if (!Arrays.equals(U2F_STATIC_AAGUID, authenticatorData.getAttestedCredentialData().getAaguid())) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.U2F_ATTESTATION_AAGUID_INVALID);
        }

        PublicKey publicKey = certificates.get(0).getPublicKey();

        if (publicKey instanceof ECPublicKey) {
            // check ec curve
            final String OID_P256_CURVE = "1.2.840.10045.3.1.7";

            SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
            ASN1Encodable encodable = info.getAlgorithm().getParameters();

            if (!OID_P256_CURVE.equals(((ASN1ObjectIdentifier) encodable).getId())) {
                // not p-256 curve
            }

            ECCKey eccKey = (ECCKey) authenticatorData.getAttestedCredentialData().getCredentialPublicKey();
            if (eccKey.getX() == null ||
                    eccKey.getX().length != 32 ||
                    eccKey.getY() == null ||
                    eccKey.getY().length != 32) {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.U2F_ATTESTATION_USER_KEY_INVALID);
            }

            int u2fKeyLength = 65;    // 0x04 + x.length + y.length
            int verificationDataSize = 1 + authenticatorData.getRpIdHash().length +
                    clientDataHash.length +
                    authenticatorData.getAttestedCredentialData().getCredentialId().length +
                    u2fKeyLength;

            byte[] verificationData = ByteBuffer
                    .allocate(verificationDataSize)
                    .put((byte) 0x00)
                    .put(authenticatorData.getRpIdHash())
                    .put(clientDataHash)
                    .put(authenticatorData.getAttestedCredentialData().getCredentialId())
                    .put((byte) 0x04)
                    .put(eccKey.getX())
                    .put(eccKey.getY())
                    .array();

            // get cose algorithm from public key
            boolean result = SignatureHelper
                    .verifySignature(publicKey, verificationData, fidoU2f.getSig(), COSEAlgorithm.ES256);

            return AttestationVerificationResult
                    .builder()
                    .success(result)
                    .type(AttestationType.BASIC)
                    .trustPath(certificates)
                    .format(AttestationStatementFormatIdentifier.FIDO_U2F)
                    .build();
        } else {
            // should be ecc key type
            throw new FIDO2ServerRuntimeException(InternalErrorCode.U2F_ATTESTATION_KEY_NOT_ECC_TYPE);
        }
    }
}
