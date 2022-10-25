package de.thi.bachelorthesis.fido2.rpserver.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum AuthenticatorAttachment {
    PLATFORM("platform"),
    CROSS_PLATFORM("cross-platform");

    @JsonValue
    @Getter
    private final String value;

    @JsonCreator(mode=JsonCreator.Mode.DELEGATING)
    public static AuthenticatorAttachment fromValue(String value) {
        return Arrays.stream(AuthenticatorAttachment.values())
                .filter(e -> e.value.equals(value))
                .findFirst()
                .get();
    }
}
