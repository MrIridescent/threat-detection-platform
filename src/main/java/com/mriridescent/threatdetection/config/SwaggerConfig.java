package com.mriridescent.threatdetection.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Swagger documentation.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configure OpenAPI documentation
     *
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI threatDetectionAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Threat Detection Platform API")
                        .description("API for a comprehensive platform integrating Project Iris and PhishNet Analyst " + 
                                     "for advanced phishing threat detection and campaign analysis.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("MR Iridescent")
                                .url("https://github.com/mriridescent")
                                .email("contact@mriridescent.com"))
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
