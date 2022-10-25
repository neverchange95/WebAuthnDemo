package de.thi.bachelorthesis.fido2.rpserver.general;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ServerAuthenticatorAttestationResponse extends ServerAuthenticatorResponse {
    @NotBlank
    @Base64Encoded
    private String attestationObject;
    private List<AuthenticatorTransport> transports;    // WebAuthn Level2
}
