package de.thi.bachelorthesis.fido2.rpserver.helper;

import de.thi.bachelorthesis.fido2.rpserver.error.InternalErrorCode;
import de.thi.bachelorthesis.fido2.rpserver.exception.FIDO2ServerRuntimeException;
import de.thi.bachelorthesis.fido2.rpserver.general.COSEAlgorithm;
import de.thi.bachelorthesis.fido2.rpserver.model.CredentialPublicKey;
import de.thi.bachelorthesis.fido2.rpserver.model.ECCKey;
import de.thi.bachelorthesis.fido2.rpserver.model.OctetKey;
import de.thi.bachelorthesis.fido2.rpserver.model.RSAKey;
import de.thi.bachelorthesis.fido2.rpserver.util.PublicKeyUtil;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

public class CredentialPublicKeyHelper {
    public static PublicKey convert(CredentialPublicKey credentialPublicKey) {
        PublicKey publicKey;
        if (credentialPublicKey instanceof RSAKey) {
            RSAKey rsaKey = (RSAKey) credentialPublicKey;
            // convert
            try {
                publicKey = PublicKeyUtil.getRSAPublicKey(rsaKey.getN(), rsaKey.getE());
            } catch (GeneralSecurityException e) {
                throw new FIDO2ServerRuntimeException(InternalErrorCode.USER_PUBLIC_KEY_INVALID_KEY_SPEC, e);
            }
        } else if (credentialPublicKey instanceof ECCKey) {
            ECCKey eccKey = (ECCKey) credentialPublicKey;
            // convert
            try {
                publicKey = PublicKeyUtil.getECDSAPublicKey(eccKey.getX(), eccKey.getY(), eccKey.getCurve().getNamedCurve());
            } catch (GeneralSecurityException e) {
                throw new FIDO2ServerRuntimeException(
                        InternalErrorCode.USER_PUBLIC_KEY_INVALID_KEY_SPEC, e);
            }

        } else if (credentialPublicKey instanceof OctetKey) {
            OctetKey octetKey = (OctetKey) credentialPublicKey;
            // convert
            try {
                publicKey = PublicKeyUtil.getEdDSAPublicKey(octetKey.getX(), octetKey.getCurve().getNamedCurve());
            } catch (GeneralSecurityException e) {
                throw new FIDO2ServerRuntimeException(
                        InternalErrorCode.USER_PUBLIC_KEY_INVALID_KEY_SPEC, e);
            }
        } else {
            throw new FIDO2ServerRuntimeException(InternalErrorCode.INVALID_CREDENTIAL_INSTANCE);
        }
        return publicKey;
    }

    public static COSEAlgorithm getCOSEAlgorithm(CredentialPublicKey credentialPublicKey) {
        COSEAlgorithm algorithm;
        if (credentialPublicKey instanceof RSAKey) {
            algorithm = ((RSAKey) credentialPublicKey).getAlgorithm();
        } else if (credentialPublicKey instanceof OctetKey) {
            algorithm = ((OctetKey) credentialPublicKey).getAlgorithm();
        } else {
            algorithm = ((ECCKey) credentialPublicKey).getAlgorithm();
        }
        return algorithm;
    }
}
