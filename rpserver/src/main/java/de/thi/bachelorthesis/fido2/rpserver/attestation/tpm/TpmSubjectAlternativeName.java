package de.thi.bachelorthesis.fido2.rpserver.attestation.tpm;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TpmSubjectAlternativeName {
    private TpmCapVendorId manufacturer;
    private String partNumber;
    private String firmwareVersion;
}
