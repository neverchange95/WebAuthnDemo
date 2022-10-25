package de.thi.bachelorthesis.fido2.rpserver.general;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum COSEAlgorithm {
    RS1("RS1", -65535, "RSASSA-PKCS1-v1_5 w/ SHA-1", false),
    RS256("RS256", -257, "RSASSA-PKCS1-v1_5 w/ SHA-256", false),
    RS384("RS384", -258, "RSASSA-PKCS1-v1_5 w/ SHA-384", false),
    RS512("RS512", -259, "RSASSA-PKCS1-v1_5 w/ SHA-512", false),
    PS256("PS256", -37, "RSASSA-PSS w/ SHA-256", true),
    PS384("PS384", -38, "RSASSA-PSS w/ SHA-384", true),
    PS512("PS512", -39, "RSASSA-PSS w/ SHA-512", true),
    EDDSA("EdDSA", -8, "EdDSA", true),
    ES256("ES256", -7, "ECDSA w/ SHA-256", true),
    ES384("ES384", -35, "ECDSA w/ SHA-384", true),
    ES512("ES512", -36, "ECDSA w/ SHA-512", true),
    ES256K("ES256K", -43, "ECDSA using P-256K and SHA-256", false);

    private final String name;
    private final int value;
    private final String description;
    private final boolean recommended;

    public static COSEAlgorithm fromValue(int value) {
        return Arrays.stream(COSEAlgorithm.values())
                .filter(e -> e.value == value)
                .findFirst()
                .get();
    }

    public boolean isECCAlgorithm() {
        return value == ES256.value ||
                value == ES384.value ||
                value == ES512.value ||
                value == ES256K.value;
    }

    public boolean isRSAAlgorithm() {
        return value == RS1.value ||
                value == RS256.value ||
                value == RS384.value ||
                value == RS512.value ||
                value == PS256.value ||
                value == PS384.value ||
                value == PS512.value;
    }

    public boolean isEdDSAAlgorithm() {
        return value == EDDSA.value;
    }

    public String getHashAlgorithm() {
        if (value == RS1.value) {
            return "SHA-1";
        } else if (value == RS256.value ||
                value == PS256.value ||
                value == ES256.value ||
                value == ES256K.value) {
            return "SHA-256";
        } else if (value == RS384.value ||
                value == PS384.value ||
                value == ES384.value) {
            return "SHA-384";
        } else {
            return "SHA-512";
        }

    }
}
