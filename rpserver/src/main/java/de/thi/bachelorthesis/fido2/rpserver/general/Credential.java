package de.thi.bachelorthesis.fido2.rpserver.general;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Credential {
    @NotBlank
    @Base64Encoded
    private String id;
    @NotNull
    private PublicKeyCredentialType type;
}
