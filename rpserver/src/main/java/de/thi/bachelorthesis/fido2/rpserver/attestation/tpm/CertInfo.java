package de.thi.bachelorthesis.fido2.rpserver.attestation.tpm;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CertInfo {
    private byte[] magic;
    private TpmSt type;
    private byte[] extraData;
    private ClockInfo clockInfo;
    private byte[] firmwareVersion;
    private AttestedName attestedName;
}
