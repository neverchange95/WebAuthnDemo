package de.thi.bachelorthesis.fido2.rpserver.attestation.apple;

import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatement;
import lombok.Data;

import java.util.List;

@Data
public class AppleAnonymousAttestationStatement extends AttestationStatement {
    List<byte[]> x5c;
}
