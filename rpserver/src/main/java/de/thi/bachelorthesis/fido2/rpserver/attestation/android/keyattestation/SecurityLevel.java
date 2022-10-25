package de.thi.bachelorthesis.fido2.rpserver.attestation.android.keyattestation;

import java.util.Arrays;

public enum SecurityLevel {
    SOFTWARE(0), TRUSTED_ENVIRONMENT(1);

    private final int value;

    SecurityLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SecurityLevel fromValue(int value) {
        return Arrays.stream(SecurityLevel.values())
                .filter(e -> e.value == value)
                .findFirst()
                .get();
    }
}