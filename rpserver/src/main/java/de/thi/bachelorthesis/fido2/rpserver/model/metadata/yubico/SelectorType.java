package de.thi.bachelorthesis.fido2.rpserver.model.metadata.yubico;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum SelectorType {
    FINGERPRINT("fingerprint"),
    X509EXTENSION("x509Extension");

    @JsonValue
    @Getter
    private final String value;

    @JsonCreator(mode=JsonCreator.Mode.DELEGATING)
    public static SelectorType fromValue(String value){
        return Arrays.stream(SelectorType.values())
                .filter(e -> e.value.equals(value))
                .findFirst()
                .get();
    }
}
