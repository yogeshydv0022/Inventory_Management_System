package com.files.securityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Inventory Management System")
                .version("1.0")
                .description("This is a sample Spring Boot RESTful service using springdoc-openapi and OpenAPI 3."));
    }
}