package de.thi.bachelorthesis.fido2.rpserver.general;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticatorSelectionCriteria {
    private AuthenticatorAttachment authenticatorAttachment;
    private boolean requireResidentKey;
    private UserVerificationRequirement userVerification;
}
