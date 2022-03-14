package org.netcracker.eventteammatessearch.configs;


import org.n52.jackson.datatype.jts.JtsModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {


    @Bean
    public JtsModule jtsModule() {
        return new JtsModule();
    }
}
