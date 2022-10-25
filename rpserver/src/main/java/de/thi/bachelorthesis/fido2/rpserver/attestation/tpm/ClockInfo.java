package de.thi.bachelorthesis.fido2.rpserver.attestation.tpm;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ClockInfo {
    private byte[] clock;
    private long resetCount;
    private long restartCount;
    private boolean safe;
}
