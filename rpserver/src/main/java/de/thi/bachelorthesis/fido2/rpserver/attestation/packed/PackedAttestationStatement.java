package de.thi.bachelorthesis.fido2.rpserver.attestation.packed;

import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatement;
import lombok.Data;

import java.util.List;

@Data
public class PackedAttestationStatement extends AttestationStatement {
    private int alg;
    private byte[] sig;
    List<byte[]> x5c;
    private byte[] ecdaaKeyId;
}
