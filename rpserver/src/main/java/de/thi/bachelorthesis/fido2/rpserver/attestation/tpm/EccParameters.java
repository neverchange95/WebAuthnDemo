package de.thi.bachelorthesis.fido2.rpserver.attestation.tpm;

import lombok.Data;

@Data
public class EccParameters extends Parameters {
    private TpmEccCurve curveId;
    private byte[] kdf;
}
