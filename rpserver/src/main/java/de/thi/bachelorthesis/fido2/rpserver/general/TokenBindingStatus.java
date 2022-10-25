package de.thi.bachelorthesis.fido2.rpserver.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum TokenBindingStatus {
    PRESENT("present"),
    SUPPORTED("supported"),
    NOT_SUPPORTED("not-supported"); // android native FIDO response has following value, so just added for the tests.

    @JsonValue
    @Getter
    private final String value;

    @JsonCreator(mode=JsonCreator.Mode.DELEGATING)
    public static TokenBindingStatus fromValue(String value) {
        return Arrays.stream(TokenBindingStatus.values())
                .filter(e -> e.value.equals(value))
                .findFirst()
                .get();
    }
}
