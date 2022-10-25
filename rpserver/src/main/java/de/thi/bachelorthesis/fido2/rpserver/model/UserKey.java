package de.thi.bachelorthesis.fido2.rpserver.model;

import de.thi.bachelorthesis.fido2.rpserver.general.AttestationType;
import de.thi.bachelorthesis.fido2.rpserver.general.AuthenticatorTransport;
import de.thi.bachelorthesis.fido2.rpserver.general.COSEAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.PublicKey;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserKey {
    // rp info
    private String rpId;
    // user info
    private String id;
    private String name;
    private String icon;
    private String displayName;
    // credential info
    private String aaguid;
    private String credentialId;
    private PublicKey publicKey;
    private COSEAlgorithm algorithm;
    private Long signCounter;
    private AttestationType attestationType;

    // transports
    private List<AuthenticatorTransport> transports;    // WebAuthn Level2

    // credProps - rk
    private Boolean rk; // WebAuthn Level2

    // credProtect
    private Integer credProtect;

    private Date registeredAt;
    private Date authenticatedAt;
}
