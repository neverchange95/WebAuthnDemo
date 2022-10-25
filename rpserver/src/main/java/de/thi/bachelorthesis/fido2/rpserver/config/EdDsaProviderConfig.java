package de.thi.bachelorthesis.fido2.rpserver.config;

import net.i2p.crypto.eddsa.EdDSASecurityProvider;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.Security;

@Configuration
public class EdDsaProviderConfig {
    @PostConstruct
    private void setSecurityProvider() {
        Security.addProvider(new EdDSASecurityProvider());
    }
}
