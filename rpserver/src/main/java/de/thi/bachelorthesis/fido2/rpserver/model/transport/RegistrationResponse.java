package de.thi.bachelorthesis.fido2.rpserver.model.transport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.thi.bachelorthesis.fido2.rpserver.general.ServerPublicKeyCredentialDescriptor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationResponse extends AdapterServerResponse {
    private List<ServerPublicKeyCredentialDescriptor> allowCredentials;
}
