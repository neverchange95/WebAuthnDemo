package de.thi.bachelorthesis.fido2.rpserver.general;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class RegisterCredentialResult {
    private String aaguid;
    private String credentialId;
    private AuthenticatorAttachment authenticatorAttachment;
    private AttestationType attestationType;
    private List<AuthenticatorTransport> authenticatorTransports;   // list of available authenticator transport
    private boolean userVerified;
    private Boolean rk; // RP can decided UX flow by looking at this
    private Integer credProtect;
}
