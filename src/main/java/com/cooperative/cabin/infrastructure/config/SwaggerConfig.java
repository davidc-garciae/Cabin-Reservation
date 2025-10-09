package com.cooperative.cabin.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cabin Reservation API")
                        .version("v2.0")
                        .description("API completa para el sistema de reservas de cabañas de la cooperativa. " +
                                "Incluye autenticación JWT, gestión de reservas, horarios de check-in/check-out, " +
                                "y funcionalidades administrativas completas.")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Equipo de Desarrollo")
                                .email("desarrollo@cooperativa.com")))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtenido del endpoint de login o registro")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
