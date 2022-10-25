package de.thi.bachelorthesis.fido2.rpserver.config;


import de.thi.bachelorthesis.fido2.rpserver.attestation.android.keyattestation.RevokeCheckerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NetworkClientConfig {
    @Bean
    public RevokeCheckerClient RevokeCheckerClient(){
        return new RevokeCheckerClient();
    }
}
