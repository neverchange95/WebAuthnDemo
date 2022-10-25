package de.thi.bachelorthesis.fido2.rpserver.model.metadata.yubico;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.thi.bachelorthesis.fido2.rpserver.helper.vendor.YubicoSelectorDeserializer;
import lombok.Data;

@Data
@JsonDeserialize(using = YubicoSelectorDeserializer.class)
public class Selector {
    private SelectorType type;
    private Parameters parameters;
}
