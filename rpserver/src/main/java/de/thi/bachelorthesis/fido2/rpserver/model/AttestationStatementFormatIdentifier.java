package de.thi.bachelorthesis.fido2.rpserver.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum AttestationStatementFormatIdentifier {
    PACKED("packed"),
    TPM("tpm"),
    ANDROID_KEY("android-key"),
    ANDROID_SAFETYNET("android-safetynet"),
    FIDO_U2F("fido-u2f"),
    NONE("none"),
    APPLE_ANONYMOUS("apple");

    @JsonValue
    @Getter
    private final String value;

    public static AttestationStatementFormatIdentifier fromValue(String value){
        return Arrays.stream(AttestationStatementFormatIdentifier.values())
                .filter(e -> e.value.equals(value))
                .findFirst()
                .get();
    }
}
