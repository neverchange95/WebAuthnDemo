package de.thi.bachelorthesis.fido2.rpserver.cose;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum COSEEllipticCurve {
    P256("P-256", 1, COSEKeyType.EC2, "secp256r1", "NIST P-256 also known as secp256r1", true),
    P384("P-384", 2, COSEKeyType.EC2, "secp384r1", "NIST P-384 also known as secp384r1", true),
    P521("P-521", 3, COSEKeyType.EC2, "secp521r1", "NIST P-521 also known as secp521r1", true),
    P256K("P-256K", 8, COSEKeyType.EC2, "secp256k1", "SECG secp256k1 curve", false),
    ED25519("Ed25519", 6, COSEKeyType.OKP, "ed25519", "Ed25519 for use w/ EdDSA only", true);

    private final String name;
    private final int value;
    private final COSEKeyType keyType;
    private final String namedCurve;
    private final String description;
    private final boolean recommended;

    public static COSEEllipticCurve fromValue(int value) {
        return Arrays.stream(COSEEllipticCurve.values())
                .filter(e -> e.value == value)
                .findFirst()
                .get();
    }
}
