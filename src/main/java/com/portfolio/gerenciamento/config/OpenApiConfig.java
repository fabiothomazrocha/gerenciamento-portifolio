package com.portfolio.gerenciamento.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "basicAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("API de Gerenciamento de Portfólio de Projetos")
                        .version("1.0.0")
                        .description("""
                            Sistema para gerenciar o portfólio de projetos de uma empresa.
                            
                            **Usuários disponíveis:**
                            - `user` / `user123` → leitura (GET)
                            - `manager` / `manager123` → leitura e escrita (GET, POST, PUT, PATCH)
                            - `admin` / `admin123` → acesso total (incluindo DELETE)
                            """)
                        .contact(new Contact()
                                .name("Desafio Técnico")
                                .email("fabiothomazrocha@gmail.com")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")));
    }
}
