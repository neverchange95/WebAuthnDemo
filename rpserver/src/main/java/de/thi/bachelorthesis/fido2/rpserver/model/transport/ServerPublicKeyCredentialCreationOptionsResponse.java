package de.thi.bachelorthesis.fido2.rpserver.model.transport;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.thi.bachelorthesis.fido2.rpserver.entity.PublicKeyCredentialRpEntity;
import de.thi.bachelorthesis.fido2.rpserver.entity.ServerPublicKeyCredentialUserEntity;
import de.thi.bachelorthesis.fido2.rpserver.general.AttestationConveyancePreference;
import de.thi.bachelorthesis.fido2.rpserver.general.AuthenticatorSelectionCriteria;
import de.thi.bachelorthesis.fido2.rpserver.general.PublicKeyCredentialParameters;
import de.thi.bachelorthesis.fido2.rpserver.general.ServerPublicKeyCredentialDescriptor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerPublicKeyCredentialCreationOptionsResponse extends AdapterServerResponse {
    private PublicKeyCredentialRpEntity rp;
    private ServerPublicKeyCredentialUserEntity user;
    private String challenge;
    private List<PublicKeyCredentialParameters> pubKeyCredParams;
    private long timeout;
    private List<ServerPublicKeyCredentialDescriptor> excludeCredentials;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AuthenticatorSelectionCriteria authenticatorSelection;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AttestationConveyancePreference attestation;
}
