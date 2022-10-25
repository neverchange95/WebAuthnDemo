package de.thi.bachelorthesis.fido2.rpserver.general;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class ServerPublicKeyCredentialDescriptor {
    private PublicKeyCredentialType type;
    private String id;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<AuthenticatorTransport> transports;
}
