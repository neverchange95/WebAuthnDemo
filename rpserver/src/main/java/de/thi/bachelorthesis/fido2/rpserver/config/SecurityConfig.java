package de.thi.bachelorthesis.fido2.rpserver.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class SecurityConfig {
    @PostConstruct
    private void setEnableCRLDP() {
        System.setProperty("com.sun.security.enableCRLDP", "true");
    }
}
