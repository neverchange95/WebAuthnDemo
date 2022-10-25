package de.thi.bachelorthesis.fido2.rpserver.general.extension;

import lombok.Data;

import java.util.List;

@Data
public class AuthenticationExtensionsClientOutputs {
    private Boolean appid;
    private String txAuthSimple;
    private String txAuthGeneric;
    private Boolean authnSel;
    private List<String> exts;
    private String uvi;
    private Coordinates loc;
    private Boolean biometricPerfBounds;
    private CredentialPropertiesOutput credProps;
}
