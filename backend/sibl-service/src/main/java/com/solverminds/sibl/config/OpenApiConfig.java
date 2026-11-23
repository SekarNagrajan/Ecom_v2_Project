package com.solverminds.sibl.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration("siblOpenApiConfig")
@OpenAPIDefinition(
    info = @Info(
        title = "SIBL Service API",
        version = "1.0.0",
        description = "RESTful API for managing Shipping Instruction (SI) and Bill of Lading (BL) summary data.\n\n" +
                      "**Key endpoints:**\n" +
                      "- **Create SI**: POST /api/v1/bill-of-lading/sicreate - create a new shipping instruction with containers, commodities, customers. When commodities are sent, itemWeight and itemWeightUnit are required per commodity (same as container cargoWeight/weightUnit).\n" +
                      "- **View bill-of-lading**: GET /api/v1/bill-of-lading/view - fetch by bookingNo, onlineSINo, or blNumber (priority: bookingNo ? onlineSINo ? blNumber); response same shape as SI create (SIBLRequestDTO)\n\n" +
                      "This service also provides:\n" +
                      "- **Shipping Instructions**: Container shipping instruction summaries via stored procedure usp_ecomSISummaryNew\n" +
                      "- **Bill of Lading**: Bill of lading summaries with filtering options via stored procedure usp_EcomBLSummary\n\n" +
                      "## Authentication\n" +
                      "All endpoints require JWT Bearer token authentication. The customer code is automatically extracted from the JWT token claims.\n\n" +
                      "## Database\n" +
                      "- **Database**: ReportDB\n" +
                      "- **Connection**: Native SQL queries via stored procedures\n" +
                      "- **Security**: Customer-specific data filtering based on JWT claims",
        termsOfService = "https://solverminds.com/terms",
        contact = @Contact(
            name = "SolverMinds Support Team",
            email = "support@solverminds.com",
            url = "https://solverminds.com/contact"
        ),
        license = @License(
            name = "Proprietary License",
            url = "https://solverminds.com/license"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local Development Server"),
        @Server(url = "https://api-dev.solverminds.com", description = "Development Environment"),
        @Server(url = "https://api-uat.solverminds.com", description = "User Acceptance Testing Environment"),
        @Server(url = "https://api.solverminds.com", description = "Production Environment")
    },
    externalDocs = @io.swagger.v3.oas.annotations.ExternalDocumentation(
        description = "SIBL Service Documentation",
        url = "https://docs.solverminds.com/sibl-service"
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "JWT authentication token is required for all API endpoints.\n\n" +
                  "**How to use:**\n" +
                  "1. Obtain JWT token from the authentication service\n" +
                  "2. Include the token in the Authorization header\n" +
                  "3. Format: Authorization: Bearer <your-jwt-token>\n\n" +
                  "**Token Claims:**\n" +
                  "- customerCode: Used to filter data specific to the authenticated customer\n" +
                  "- exp: Token expiration timestamp\n" +
                  "- iat: Token issued at timestamp\n\n" +
                  "**Example:**\n" +
                  "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjdXN0b21lckNvZGUiOiIyMDAwMTM3NCIsImV4cCI6MTcwNTMyMDAwMH0.signature"
)
public class OpenApiConfig {
}
