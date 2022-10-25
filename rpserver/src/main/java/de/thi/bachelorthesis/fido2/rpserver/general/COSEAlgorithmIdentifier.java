package de.thi.bachelorthesis.fido2.rpserver.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum COSEAlgorithmIdentifier {
    RS1(-65535),
    RS256(-257),
    RS384(-258),
    RS512(-259),
    PS256(-37),
    PS384(-38),
    PS512(-39),
    ES256(-7),
    ES384(-35),
    ES512(-36),
    EdDSA(-8),
    ES256K(-43);

    @JsonValue
    @Getter
    private final long value;

    @JsonCreator(mode=JsonCreator.Mode.DELEGATING)
    public static COSEAlgorithmIdentifier fromValue(long value) {
        return Arrays.stream(COSEAlgorithmIdentifier.values()).filter(e -> e.value == value)
                .findFirst()
                .get();
    }
}
