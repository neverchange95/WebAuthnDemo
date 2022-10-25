package de.thi.bachelorthesis.fido2.rpserver.attestation.tpm;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PubArea {
    private TpmKeyAlgorithm type;
    private TpmHashAlgorithm nameAlg;
    private ObjectAttributes objectAttributes;
    private Parameters parameters;
    private byte[] unique;
}
