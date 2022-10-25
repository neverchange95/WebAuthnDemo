package de.thi.bachelorthesis.fido2.rpserver.model.metadata.yubico;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum Transports {
    BT(0x01),
    BLE(0x02),
    USB(0x04),
    NFC(0x08);

    @JsonValue
    @Getter
    private final int value;

    @JsonCreator(mode=JsonCreator.Mode.DELEGATING)
    public static Transports fromValue(int value){
        return Arrays.stream(Transports.values())
                .filter(e -> e.value == value)
                .findFirst()
                .get();
    }
}
