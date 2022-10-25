package de.thi.bachelorthesis.fido2.rpserver.model;

import de.thi.bachelorthesis.fido2.rpserver.general.Credential;
import de.thi.bachelorthesis.fido2.rpserver.general.ServerAuthenticatorAttestationResponse;
import lombok.Data;

@Data
public class AdapterRegServerPublicKeyCredential extends Credential {
    private String rawId;
    private ServerAuthenticatorAttestationResponse response;
}
