package de.thi.bachelorthesis.fido2.rpserver.model;

import de.thi.bachelorthesis.fido2.rpserver.model.transport.ServerPublicKeyCredentialCreationOptionsResponse;
import de.thi.bachelorthesis.fido2.rpserver.model.transport.ServerPublicKeyCredentialGetOptionsResponse;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Session {
    @Id
    private String id;
    private String hmacKey;
    private ServerPublicKeyCredentialCreationOptionsResponse regOptionResponse;
    private ServerPublicKeyCredentialGetOptionsResponse authOptionResponse;
    private boolean served;
}
