package de.thi.bachelorthesis.fido2.rpserver.attestation.tpm;

import lombok.Data;

@Data
public class Parameters {
    private byte[] symmetric;
    private TpmSignatureAlgorithm scheme;
}
