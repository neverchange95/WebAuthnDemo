package de.thi.bachelorthesis.fido2.rpserver.attestation.android.keyattestation;

import java.util.Arrays;

public enum VerifiedBootState {
    VERIFIED(0), SELF_SIGNED(1), UNVERIFIED(2), FAILED(3);

    private final int value;

    VerifiedBootState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static VerifiedBootState fromValue(int value) {
        return Arrays.stream(VerifiedBootState.values())
                .filter(e -> e.value == value)
                .findFirst()
                .get();
    }
}
