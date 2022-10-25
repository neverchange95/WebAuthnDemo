package de.thi.bachelorthesis.fido2.rpserver.general;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RegisterCredenial {
    @NotNull
    @Valid
    private ServerRegPublicKeyCredential serverPublicKeyCredential;
    @NotBlank
    private String sessionId;
    @NotBlank
    private String origin;
    @NotBlank
    private String rpId;
    private TokenBinding tokenBinding;
}
