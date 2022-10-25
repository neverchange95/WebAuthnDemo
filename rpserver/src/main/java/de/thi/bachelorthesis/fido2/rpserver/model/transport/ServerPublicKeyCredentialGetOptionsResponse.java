package de.thi.bachelorthesis.fido2.rpserver.model.transport;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.thi.bachelorthesis.fido2.rpserver.general.ServerPublicKeyCredentialDescriptor;
import de.thi.bachelorthesis.fido2.rpserver.general.UserVerificationRequirement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerPublicKeyCredentialGetOptionsResponse extends AdapterServerResponse {
    private String challenge;
    private long timeout;
    private String rpId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ServerPublicKeyCredentialDescriptor> allowCredentials;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserVerificationRequirement userVerification;
}
