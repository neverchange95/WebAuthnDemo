package de.thi.bachelorthesis.fido2.rpserver.attestation.android.safetynet;

import de.thi.bachelorthesis.fido2.rpserver.model.AttestationStatement;
import lombok.Data;

@Data
public class AndroidSafetyNetAttestationStatement extends AttestationStatement {
    private String ver;
    private byte[] response;
}
