package de.thi.bachelorthesis.fido2.rpserver.model.transport;

import de.thi.bachelorthesis.fido2.rpserver.general.AttestationConveyancePreference;
import de.thi.bachelorthesis.fido2.rpserver.general.AuthenticatorSelectionCriteria;
import de.thi.bachelorthesis.fido2.rpserver.general.extension.CredProtect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerPublicKeyCredentialCreationOptionsRequest {
    private String username;
    private String displayName;
    private AuthenticatorSelectionCriteria authenticatorSelection;
    private AttestationConveyancePreference attestation = AttestationConveyancePreference.none;
    private CredProtect credProtect;
}
