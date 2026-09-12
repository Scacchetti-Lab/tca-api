package com.api.tca.config.shared;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class MapperConfig {

    @Bean
    public ObjectMapper getMapper() {
        return new ObjectMapper();
    }
}
