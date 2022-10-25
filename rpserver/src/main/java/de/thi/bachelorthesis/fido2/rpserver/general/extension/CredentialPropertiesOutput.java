package de.thi.bachelorthesis.fido2.rpserver.general.extension;

import lombok.Data;

@Data
public class CredentialPropertiesOutput {
    // if rk is true, resident key (able to authenticate without allow list)
    // if rk is false, non-resident key (should have allow list during authentication)
    // if rk is null, we don't know whether it is resident key or non-resident key
    private Boolean rk;
}
