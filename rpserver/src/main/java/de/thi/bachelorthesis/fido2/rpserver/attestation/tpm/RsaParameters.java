package de.thi.bachelorthesis.fido2.rpserver.attestation.tpm;

import lombok.Data;

@Data
public class RsaParameters extends Parameters {
    byte[] keyBits;
    byte[] exponent;
}
