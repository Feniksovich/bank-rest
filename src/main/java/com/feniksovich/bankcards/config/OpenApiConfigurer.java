package com.feniksovich.bankcards.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
public class OpenApiConfigurer {

    private static final String SECURITY_REQUIREMENT_KEY = "Bearer Authentication";

    @Bean
    public ModelResolver modelResolver(ObjectMapper objectMapper) {
        return new ModelResolver(objectMapper);
    }

    @Bean
    public OpenAPI openAPI() {
        final Info info = new Info()
                .title("Bank REST API")
                .description("REST API for managing bank cards and user accounts")
                .version("1.0.0");

        final Server server = new Server()
                .url("http://localhost:8080")
                .description("Local development environment");

        final SecurityRequirement securityRequirement =
                new SecurityRequirement().addList(SECURITY_REQUIREMENT_KEY);

        final SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter JWT token for authentication. Get token via /auth/signin");

        return new OpenAPI()
                .info(info)
                .servers(List.of(server))
                .addSecurityItem(securityRequirement)
                .components(new Components().addSecuritySchemes(SECURITY_REQUIREMENT_KEY, securityScheme));
    }
}
