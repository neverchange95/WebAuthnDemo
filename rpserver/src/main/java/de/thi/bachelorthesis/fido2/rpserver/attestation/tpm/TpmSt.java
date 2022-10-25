package de.thi.bachelorthesis.fido2.rpserver.attestation.tpm;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum TpmSt {
    RSP_COMMAND(0x00C4),
    NULL(0x8000),
    NO_SESSION(0x8001),
    SESSIONS(0x8002),
    ATTEST_NV(0x8014),
    ATTEST_COMMAND_AUDIT(0x8015),
    ATTEST_SESSION_AUDIT(0x8016),
    ATTEST_CERTIFY(0x8017),
    ATTEST_QUOTE(0x8018),
    ATTEST_TIME(0x8019),
    ATTEST_CREATION(0x801A),
    CREATION(0x8021),
    VERIFIED(0x8022),
    AUTH_SECRET(0x8023),
    HASHCHECK(0x8024),
    AUTH_SIGNED(0x8025),
    FU_MANIFEST(0x8029);

    @Getter
    private final int value;

    public static TpmSt fromValue(int value) {
        return Arrays.stream(TpmSt.values())
                .filter(e -> e.value == value)
                .findFirst()
                .get();
    }
}
