package org.netcracker.eventteammatessearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EventTeammatesSearchApplication {

    public static void init() {
        System.setProperty("javax.net.ssl.keyStore", "sample_keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "1234567");
    }

    public static void main(String[] args) {
        init();
        SpringApplication.run(EventTeammatesSearchApplication.class, args);
    }


}
