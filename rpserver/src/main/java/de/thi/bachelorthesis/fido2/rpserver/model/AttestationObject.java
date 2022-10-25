package de.thi.bachelorthesis.fido2.rpserver.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.thi.bachelorthesis.fido2.rpserver.helper.AttestationObjectDeserializer;
import lombok.Data;

@JsonDeserialize(using = AttestationObjectDeserializer.class)
@Data
public class AttestationObject {
    private AuthenticatorData authData;
    private AttestationStatementFormatIdentifier fmt;
    private AttestationStatement attStmt;
}
