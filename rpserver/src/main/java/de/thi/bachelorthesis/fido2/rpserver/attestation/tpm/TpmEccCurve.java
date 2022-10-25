package de.thi.bachelorthesis.fido2.rpserver.attestation.tpm;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum TpmEccCurve {
    NONE(0x0000),
    NIST_P192(0x0001),
    NIST_P224(0x0002),
    NIST_P256(0x0003),
    NIST_P348(0x0004),
    NIST_P521(0x0005),
    BN_P256(0x0010),
    BN_P638(0x0011),
    SM2_P256(0x0020);

    @Getter
    private final int value;

    public static TpmEccCurve fromValue(int value) {
        return Arrays.stream(TpmEccCurve.values())
                .filter(e -> e.value == value)
                .findFirst()
                .get();
    }
}
