package de.thi.bachelorthesis.fido2.rpserver.attestation.android.keyattestation;

import lombok.Data;

@Data
public class RootOfTrust {
    private byte[] verifiedBootKey;
    private boolean deviceLocked;
    private VerifiedBootState verifiedBootState;
}
