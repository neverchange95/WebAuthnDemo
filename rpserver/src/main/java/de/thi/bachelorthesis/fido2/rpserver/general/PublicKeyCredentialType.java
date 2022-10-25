package de.thi.bachelorthesis.fido2.rpserver.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum PublicKeyCredentialType {
    PUBLIC_KEY("public-key");

    @JsonValue
    @Getter
    private final String value;

    @JsonCreator(mode=JsonCreator.Mode.DELEGATING)
    public static PublicKeyCredentialType fromValue(String value) {
        return Arrays.stream(PublicKeyCredentialType.values())
                .filter(e -> e.value.equals(value))
                .findFirst()
                .get();
    }
}
