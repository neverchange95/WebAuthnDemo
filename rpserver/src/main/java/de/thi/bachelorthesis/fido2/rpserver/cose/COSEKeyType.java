package de.thi.bachelorthesis.fido2.rpserver.cose;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum COSEKeyType {
    OKP("OKP", 1, "Octet Key Pair"),
    EC2("EC2", 2, "Elliptic Curve Keys w/ x- and y-coordinate pair"),
    RSA("RSA", 3, "RSA Key"),
    SYMMETRIC("Symmetric", 4, "Symmetric Keys");

    private final String name;
    private final int value;
    private final String description;

    public static COSEKeyType fromValue(int value) {
        return Arrays.stream(COSEKeyType.values())
                .filter(e -> e.value == value)
                .findFirst()
                .get();
    }
}
