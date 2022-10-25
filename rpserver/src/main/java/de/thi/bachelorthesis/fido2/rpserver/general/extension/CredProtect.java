package de.thi.bachelorthesis.fido2.rpserver.general.extension;

import lombok.Data;

@Data
public class CredProtect {
    private CredentialProtectionPolicy credentialProtectionPolicy;
    private Boolean enforceCredentialProtectionPolicy;
}
