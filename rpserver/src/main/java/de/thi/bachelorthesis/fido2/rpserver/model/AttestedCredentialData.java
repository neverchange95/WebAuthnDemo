package de.thi.bachelorthesis.fido2.rpserver.model;

import lombok.Data;

@Data
public class AttestedCredentialData {
    private byte[] aaguid;
    private byte[] credentialId;
    private CredentialPublicKey credentialPublicKey;
}
