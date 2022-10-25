package de.thi.bachelorthesis.fido2.rpserver.model.transport;

import de.thi.bachelorthesis.fido2.rpserver.general.UserVerificationRequirement;
import lombok.Data;

@Data
public class ServerPublicKeyCredentialGetOptionsRequest {
    private String username;
    private UserVerificationRequirement userVerification;
}
