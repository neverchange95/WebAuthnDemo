package de.thi.bachelorthesis.fido2.rpserver.attestation.tpm;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttestedName {
    private TpmHashAlgorithm name;
    private byte[] hash;
}
