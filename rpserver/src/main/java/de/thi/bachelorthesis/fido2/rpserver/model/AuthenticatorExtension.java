package de.thi.bachelorthesis.fido2.rpserver.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import de.thi.bachelorthesis.fido2.rpserver.extension.SupportedExtensions;
import de.thi.bachelorthesis.fido2.rpserver.general.extension.CredentialProtectionPolicy;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Data
public class AuthenticatorExtension {
    private CredentialProtectionPolicy credProtect;

    public static AuthenticatorExtension decode(byte[] input) throws IOException {
        AuthenticatorExtension authenticatorExtension = new AuthenticatorExtension();
        CredentialProtectionPolicy credProtect = null;

        ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
        CBORFactory cborFactory = new CBORFactory();
        ObjectMapper objectMapper = new ObjectMapper(cborFactory);

        JsonNode node = objectMapper.readTree(inputStream);

        if (node == null) {
            throw new IOException("No input for Extension");
        }

        JsonNode credProtectNode = node.get(SupportedExtensions.CRED_PROTECT);

        // credProtect
        if (credProtectNode != null) {
            if (credProtectNode.isNumber()) {
                credProtect = CredentialProtectionPolicy.fromValue(credProtectNode.asInt());
            }
        }

        authenticatorExtension.setCredProtect(credProtect);
        return authenticatorExtension;
    }
}
