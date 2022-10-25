package de.thi.bachelorthesis.fido2.rpserver.general;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class ServerRegPublicKeyCredential extends Credential {
    @NotNull
    @Valid
    private ServerAuthenticatorAttestationResponse response;
    // extension
    //private AuthenticationExtensionsClientOutputs extensions;
}
