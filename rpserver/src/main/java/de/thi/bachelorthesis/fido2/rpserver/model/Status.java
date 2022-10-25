package de.thi.bachelorthesis.fido2.rpserver.model;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Status {
    OK("ok"),
    FAILED("failed");

    @Getter @JsonValue private final String value;
}
