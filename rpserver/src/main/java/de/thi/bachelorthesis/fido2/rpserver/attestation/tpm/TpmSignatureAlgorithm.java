package de.thi.bachelorthesis.fido2.rpserver.attestation.tpm;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum TpmSignatureAlgorithm {
    NULL(0x0010),
    RSASSA(0x0014),
    RSAPSS(0x0016),
    ECDSA(0x0018),
    SM2(0x001B),
    ECSCHNORR(0x001C);

    @Getter
    private final int value;

    public static TpmSignatureAlgorithm fromValue(int value) {
        return Arrays.stream(TpmSignatureAlgorithm.values())
                .filter(e -> e.value == value)
                .findFirst()
                .get();
    }
}
