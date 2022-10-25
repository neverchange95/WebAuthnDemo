package de.thi.bachelorthesis.fido2.rpserver.general;

import de.thi.bachelorthesis.fido2.rpserver.general.extension.AuthenticationExtensionsClientOutputs;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class ServerAuthPublicKeyCredential extends Credential {
    @NotNull
    @Valid
    private ServerAuthenticatorAssertionResponse response;
    // extension
    private AuthenticationExtensionsClientOutputs extensions;
}
