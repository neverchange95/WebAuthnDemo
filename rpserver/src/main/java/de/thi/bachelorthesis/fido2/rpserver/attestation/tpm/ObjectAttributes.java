package de.thi.bachelorthesis.fido2.rpserver.attestation.tpm;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ObjectAttributes {
    private boolean fixedTpm;
    private boolean stClear;
    private boolean fixedParent;
    private boolean sensitiveDataOrigin;
    private boolean userWithAuth;
    private boolean adminWithPolicy;
    private boolean noDA;
    private boolean encryptedDuplication;
    private boolean restricted;
    private boolean decrypt;
    private boolean signEncrypt;
}
