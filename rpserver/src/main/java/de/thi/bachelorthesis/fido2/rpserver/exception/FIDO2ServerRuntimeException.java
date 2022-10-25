package de.thi.bachelorthesis.fido2.rpserver.exception;

import de.thi.bachelorthesis.fido2.rpserver.error.InternalErrorCode;
import lombok.Getter;

@Getter
public class FIDO2ServerRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -2575717184560818381L;
    private final InternalErrorCode errorCode;
    private String aaguid;

    public FIDO2ServerRuntimeException(InternalErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public FIDO2ServerRuntimeException(InternalErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public FIDO2ServerRuntimeException(InternalErrorCode errorCode, String message, String aaguid) {
        super(message);
        this.errorCode = errorCode;
        this.aaguid = aaguid;
    }

    public FIDO2ServerRuntimeException(InternalErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public FIDO2ServerRuntimeException(InternalErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public static FIDO2ServerRuntimeException makeCryptoError(Throwable cause) {
        return new FIDO2ServerRuntimeException(InternalErrorCode.CRYPTO_OPERATION_EXCEPTION, cause);
    }

    public static FIDO2ServerRuntimeException makeCredNotFound(String rpId, String credentialId) {
        throw new FIDO2ServerRuntimeException(InternalErrorCode.CREDENTIAL_NOT_FOUND,
                "RpId: " + rpId + "; CredentialId: " + credentialId);
    }

    public static FIDO2ServerRuntimeException makeCredNotFoundUser(String rpId, String userId) {
        throw new FIDO2ServerRuntimeException(InternalErrorCode.CREDENTIAL_NOT_FOUND,
                "RpId: " + rpId + "; UserId: " + userId);
    }
}
