package de.thi.bachelorthesis.fido2.rpserver.helper;

import de.thi.bachelorthesis.fido2.rpserver.error.InternalErrorCode;
import de.thi.bachelorthesis.fido2.rpserver.exception.FIDO2ServerRuntimeException;
import de.thi.bachelorthesis.fido2.rpserver.general.COSEAlgorithm;
import de.thi.bachelorthesis.fido2.rpserver.util.SignatureUtil;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

public class SignatureHelper {
    public static boolean verifySignature(PublicKey publicKey, byte[] messageBytes, byte[] signatureBytes, COSEAlgorithm algorithm) {
        try {
            if (algorithm == COSEAlgorithm.ES256 ||
                    algorithm == COSEAlgorithm.ES256K) {
                return SignatureUtil.verifySHA256withECDSA(publicKey, messageBytes, signatureBytes);
            } else if (algorithm == COSEAlgorithm.ES384) {
                return SignatureUtil.verifySHA384withECDSA(publicKey, messageBytes, signatureBytes);
            } else if (algorithm == COSEAlgorithm.ES512) {
                return SignatureUtil.verifySHA512withECDSA(publicKey, messageBytes, signatureBytes);
            } else if (algorithm == COSEAlgorithm.PS256) {
                return SignatureUtil.verifySHA256withRSAPssSignature(publicKey, messageBytes, signatureBytes);
            } else if (algorithm == COSEAlgorithm.PS384) {
                return SignatureUtil.verifySHA384withRSAPssSignature(publicKey, messageBytes, signatureBytes);
            } else if (algorithm == COSEAlgorithm.PS512) {
                return SignatureUtil.verifySHA512withRSAPssSignature(publicKey, messageBytes, signatureBytes);
            } else if (algorithm == COSEAlgorithm.RS1) {
                return SignatureUtil.verifySHA1withRSASignature(publicKey, messageBytes, signatureBytes);
            } else if (algorithm == COSEAlgorithm.RS256) {
                return SignatureUtil.verifySHA256withRSASignature(publicKey, messageBytes, signatureBytes);
            } else if (algorithm == COSEAlgorithm.RS384) {
                return SignatureUtil.verifySHA384withRSASignature(publicKey, messageBytes, signatureBytes);
            } else if (algorithm == COSEAlgorithm.RS512) {
                return SignatureUtil.verifySHA512withRSASignature(publicKey, messageBytes, signatureBytes);
            } else if (algorithm == COSEAlgorithm.EDDSA) {
                return SignatureUtil.verifyPureEdDSA(publicKey, messageBytes, signatureBytes);
            }
        } catch (GeneralSecurityException e) {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.SIGNATURE_VERIFICATION_ERROR);
        }

        return false;
    }
}
