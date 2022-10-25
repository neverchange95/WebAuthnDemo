package de.thi.bachelorthesis.fido2.rpserver.general;

import lombok.Data;

@Data
public class TokenBinding {
    private TokenBindingStatus status;
    private String id;
}
