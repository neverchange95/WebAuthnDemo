package de.thi.bachelorthesis.fido2.rpserver.attestation.android.keyattestation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KeyDescription {
    private int attestationVersion;
    private SecurityLevel attestationSecurityLevel;
    private int keymasterVersion;
    private SecurityLevel keymasterSecurityLevel;
    private byte[] attestationChallenge;
    private AuthorizationList softwareEnforced;
    private AuthorizationList teeEnforced;
}