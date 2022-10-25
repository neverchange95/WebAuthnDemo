package de.thi.bachelorthesis.fido2.rpserver.attestation.tpm;

import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatement;
import lombok.Data;

import java.util.List;

@Data
public class TpmAttestationStatement extends AttestationStatement {
    private String ver;
    private int alg;
    private List<byte[]> x5c;
    private byte[] ecdaaKeyId;
    private byte[] sig;
    private byte[] certInfo;
    private byte[] pubArea;
}
