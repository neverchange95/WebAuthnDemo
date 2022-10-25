package de.thi.bachelorthesis.fido2.rpserver.general.extension;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum CredentialProtectionPolicy {
    USER_VERIFICATION_OPTIONAL(0x01, "userVerificationOptional"),
    USER_VERIFICATION_OPTIONAL_WITH_CREDENTIAL_ID_LIST(0x02, "userVerificationOptionalWithCredentialIDList"),
    USER_VERIFICATION_REQUIRED(0x03, "userVerificationRequired");

    @Getter
    private final int value;
    @Getter @JsonValue
    private final String stringValue;

    @JsonCreator(mode=JsonCreator.Mode.DELEGATING)
    public static CredentialProtectionPolicy fromStringValue(@JsonProperty("stringValue") String stringValue){
        return Arrays.stream(CredentialProtectionPolicy.values())
                .filter(e -> e.stringValue.equals(stringValue))
                .findFirst()
                .get();
    }

    public static CredentialProtectionPolicy fromValue(int value){
        return Arrays.stream(CredentialProtectionPolicy.values())
                .filter(e -> e.value == value)
                .findFirst()
                .get();
    }
}
