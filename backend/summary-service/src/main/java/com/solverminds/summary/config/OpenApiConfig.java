package com.solverminds.summary.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration("summaryOpenApiConfig")
@OpenAPIDefinition(
    info = @Info(
        title = "Summary Service API",
        version = "1.0.0",
        description = "API for managing summary data including dashboard, CRO, arrival notices, and delivery orders",
        contact = @Contact(
            name = "SolverMinds Support",
            email = "support@solverminds.com"
        ),
        license = @License(
            name = "Proprietary",
            url = "https://solverminds.com/license"
        )
    ),
    servers = {
        @Server(url = "https://localhost:8080", description = "Local Development Server"),
        @Server(url = "https://api-dev.solverminds.com", description = "Development Server"),
        @Server(url = "https://api-uat.solverminds.com", description = "UAT Server"),
        @Server(url = "https://api.solverminds.com", description = "Production Server")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "JWT authentication token required. Use 'Bearer <token>' format."
)
public class OpenApiConfig {
}
