package de.thi.bachelorthesis.fido2.rpserver.general;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ServerAuthenticatorResponse {
    @NotBlank
    @Base64Encoded
    private String clientDataJSON;  // base64url encoded
}
