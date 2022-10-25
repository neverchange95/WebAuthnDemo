package de.thi.bachelorthesis.fido2.rpserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum AuthenticatorVendor {
    YUBICO("yubico"),
    GOOGLE("google"),
    MICROSOFT("microsoft"),
    FEITIAN("feitian"),
    APPLE("apple");

    @Getter
    private final String value;

    public static AuthenticatorVendor fromValue(String value){
        return Arrays.stream(AuthenticatorVendor.values())
                .filter(e -> e.value.equals(value.toLowerCase()))
                .findFirst()
                .get();
    }
}
