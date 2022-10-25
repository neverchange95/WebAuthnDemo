package de.thi.bachelorthesis.fido2.rpserver.model;

import de.thi.bachelorthesis.fido2.rpserver.general.Credential;
import de.thi.bachelorthesis.fido2.rpserver.general.ServerAuthenticatorAssertionResponse;
import de.thi.bachelorthesis.fido2.rpserver.general.extension.AuthenticationExtensionsClientOutputs;
import lombok.Data;

@Data
public class AdapterAuthServerPublicKeyCredential extends Credential {
    private String rawId;
    private ServerAuthenticatorAssertionResponse response;
    // extension
    private AuthenticationExtensionsClientOutputs extensions;
}
