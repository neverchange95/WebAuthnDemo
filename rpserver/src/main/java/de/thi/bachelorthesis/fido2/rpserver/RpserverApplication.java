package de.thi.bachelorthesis.fido2.rpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class RpserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpserverApplication.class, args);
    }

}
