package de.thi.bachelorthesis.fido2.rpserver.attestation.tpm;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum TpmHashAlgorithm {
    SHA1(0x0004),
    SHA256(0x000B),
    SHA384(0x000C),
    SHA512(0x000D),
    SM3_256(0x0012);

    @Getter
    private final int value;

    public static TpmHashAlgorithm fromValue(int value) {
        return Arrays.stream(TpmHashAlgorithm.values())
                .filter(e -> e.value == value)
                .findFirst()
                .get();
    }

    public String getAlgorithmName() {
        if (value == SHA1.value) {
            return "SHA-1";
        } else if (value == SHA256.value) {
            return "SHA-256";
        } else if (value == SHA384.value) {
            return "SHA-384";
        } else if (value == SHA512.value) {
            return "SHA-512";
        } else {
            return "SM3";
        }
    }
}
