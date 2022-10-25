package de.thi.bachelorthesis.fido2.rpserver.config;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.Security;

@Configuration
public class BouncyCastleConfig {

    @PostConstruct
    private void setSecurityProvider() {
        Security.addProvider(new BouncyCastleProvider());
    }
}
