package com.api.tca.config.shared;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .specVersion(SpecVersion.V31)
                .info(new Info()
                        .title("TCA API")
                        .description("TCA Api Rest, onde todo o processo da aplicação acontece.")
                        .license(new License()
                                .name("MIT License")
                                .url("https://github.com/Scacchetti-Lab/tca-api/blob/main/LICENSE")));
    }
}

