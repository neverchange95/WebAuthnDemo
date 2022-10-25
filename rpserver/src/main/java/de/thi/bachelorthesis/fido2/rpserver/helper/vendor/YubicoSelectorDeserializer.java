package de.thi.bachelorthesis.fido2.rpserver.helper.vendor;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import de.thi.bachelorthesis.fido2.rpserver.model.metadata.yubico.FingerprintParameters;
import de.thi.bachelorthesis.fido2.rpserver.model.metadata.yubico.Selector;
import de.thi.bachelorthesis.fido2.rpserver.model.metadata.yubico.SelectorType;
import de.thi.bachelorthesis.fido2.rpserver.model.metadata.yubico.X509ExtensionParameters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YubicoSelectorDeserializer extends JsonDeserializer<Selector> {
    @Override
    public Selector deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        Selector selector = new Selector();
        String typeString = node.get("type").textValue();
        JsonNode parametersNode = node.get("parameters");
        SelectorType type = SelectorType.fromValue(typeString);
        selector.setType(type);
        if (type == SelectorType.FINGERPRINT) {
            JsonNode fingerprintsNode = parametersNode.get("fingerprints");
            if (fingerprintsNode.isArray()) {
                List<String> parameterList = new ArrayList<>();
                int size = fingerprintsNode.size();
                for (int i = 0; i < size; i++) {
                    parameterList.add(fingerprintsNode.get(i).textValue());
                }
                selector.setParameters(new FingerprintParameters(parameterList));
            }
        } else if (type == SelectorType.X509EXTENSION) {
            String key = parametersNode.get("key").textValue();
            String value = null;
            if (parametersNode.get("value") != null) {
                value = parametersNode.get("value").textValue();
            }
            selector.setParameters(new X509ExtensionParameters(key, value));
        }

        return selector;
    }
}
