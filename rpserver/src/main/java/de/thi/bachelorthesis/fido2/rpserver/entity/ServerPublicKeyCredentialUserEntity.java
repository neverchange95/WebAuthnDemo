package de.thi.bachelorthesis.fido2.rpserver.entity;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.Data;

@Data
public class ServerPublicKeyCredentialUserEntity {
    @NotNull
    @Length(min = 1, max = 64)
    private String id;  //base64url encoded
    private String name;
    private String displayName;
}
