package de.thi.bachelorthesis.fido2.rpserver.attestation.tpm;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum TpmKeyAlgorithm {
    RSA(0x0001),
    ECC(0x0023);

    @Getter
    private final int value;

    public static TpmKeyAlgorithm fromValue(int value) {
        return Arrays.stream(TpmKeyAlgorithm.values())
                .filter(e -> e.value == value)
                .findFirst()
                .get();
    }
}
