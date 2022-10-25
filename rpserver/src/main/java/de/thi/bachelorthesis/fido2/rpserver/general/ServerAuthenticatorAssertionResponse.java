package de.thi.bachelorthesis.fido2.rpserver.general;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ServerAuthenticatorAssertionResponse extends ServerAuthenticatorResponse{
    @NotBlank
    @Base64Encoded
    private String authenticatorData;
    @NotBlank
    @Base64Encoded
    private String signature;
    private String userHandle;  // base64url encoded
}
