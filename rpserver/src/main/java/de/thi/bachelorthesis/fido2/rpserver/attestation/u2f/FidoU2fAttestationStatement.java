package de.thi.bachelorthesis.fido2.rpserver.attestation.u2f;

import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatement;
import lombok.Data;

import java.util.List;

@Data
public class FidoU2fAttestationStatement extends AttestationStatement {
    private List<byte[]> x5c;
    private byte[] sig;
}
