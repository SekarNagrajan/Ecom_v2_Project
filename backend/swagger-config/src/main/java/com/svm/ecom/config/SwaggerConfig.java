package com.svm.ecom.config;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

// Import the new DCSA-style ErrorResponse
import com.svm.ecom.dto.ErrorResponse;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "ECOM API Platform",
        version = "1.0.0",
        description = "Comprehensive API documentation for ECOM platform services including Authentication, Booking, Summary, Schedule, Master Data, Tracking, and Registration services. All authenticated endpoints require JWT Bearer token."
    )
)
public class SwaggerConfig {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    
    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    // NOTE: GroupedOpenApi beans moved to OpenApiGroupsConfig.java
    // This avoids conflicts and provides cleaner separation of concerns

    /**
     * OpenAPI customizer to remove internal schemas from documentation
     * while keeping them functional for API responses
     */
    @Bean
    public OpenApiCustomiser internalSchemaCustomiser() {
        return openApi -> {
            Components components = openApi.getComponents();
            if (components != null && components.getSchemas() != null) {
                // Remove permission-related schemas
                components.getSchemas().entrySet().removeIf(entry -> 
                    entry.getKey().contains("Permission") || 
                    entry.getKey().contains("UserPermission") ||
                    entry.getKey().contains("UserPermissionsRequest") ||
                    entry.getKey().toLowerCase().contains("permission")
                );
                
                // Note: Error response schemas are now kept in documentation
                // They are manually defined in customOpenAPI() and should remain visible
            }
            
            // Force registration of DCSA-style error response class
            forceSchemaRegistration();
        };
    }
    
    /**
     * Additional customizer to ensure error response schemas work in API responses
     * without appearing in the schemas documentation section
     */
    @Bean
    public OpenApiCustomiser errorSchemaRegistrar() {
        return openApi -> {
            // Minimal registration to ensure error schemas work in API responses
            // The schemas will be hidden by the internalSchemaCustomiser
            forceSchemaRegistration();
        };
    }
    
    /**
     * Method to force registration of error response schemas
     * This ensures the DCSA-style ErrorResponse class is available for API responses
     */
    private void forceSchemaRegistration() {
        // Reference the class to trigger schema discovery for API responses
        @SuppressWarnings("unused")
        Class<?> errorResponseClass = ErrorResponse.class;
    }

    /**
     * Define reusable OpenAPI components for all standard DCSA-style error responses
     * Also sets the server URL based on active profile to prevent auto-generation
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // Determine server URL based on active profile
        String serverUrl;
        String description;
        
        if (activeProfile.contains("prod")) {
            serverUrl = "https://erp.tailwind-shipping.com" + contextPath;
            description = "Production Server";
        } else if (activeProfile.contains("uat")) {
            serverUrl = "https://erp-uat.tailwind-shipping.com" + contextPath;
            description = "UAT Server";
        } else {
            serverUrl = "http://localhost:8080" + contextPath;
            description = "Local Development Server";
        }
        
        System.out.println("========================================");
        System.out.println("OpenAPI Bean - Setting Server:");
        System.out.println("URL: " + serverUrl);
        System.out.println("Profile: " + activeProfile);
        System.out.println("========================================");
        
        return new OpenAPI()
            .info(new io.swagger.v3.oas.models.info.Info()
                .title("ECOM API Platform")
                .version("1.0.0")
                .description("Comprehensive API documentation for ECOM platform services including Authentication, Booking, Summary, Schedule, Master Data, Tracking, and Registration services. All authenticated endpoints require JWT Bearer token.")
                )
            .addServersItem(new Server().url(serverUrl).description(description))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                // JWT Bearer Authentication
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .name("bearerAuth")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT Authorization header using the Bearer scheme. Format: 'Authorization: Bearer {token}'"))
                
                // Define DCSA-style ErrorResponse schema for all error types
                .addSchemas("ErrorResponse", new Schema<Object>()
                    .type("object")
                    .addRequiredItem("httpMethod")
                    .addRequiredItem("requestUri")
                    .addRequiredItem("statusCode")
                    .addRequiredItem("statusCodeText")
                    .addRequiredItem("errorMessage")
                    .addRequiredItem("errorDateTime")
                    .addRequiredItem("errors")
                    .addProperty("httpMethod", new Schema<String>()
                        .type("string")
                        .example("POST")
                        .description("HTTP method of the request"))
                    .addProperty("requestUri", new Schema<String>()
                        .type("string")
                        .example("/api/v1/bookings")
                        .description("URI of the request that caused the error"))
                    .addProperty("statusCode", new Schema<Integer>()
                        .type("integer")
                        .format("int32")
                        .example(400)
                        .description("HTTP status code"))
                    .addProperty("statusCodeText", new Schema<String>()
                        .type("string")
                        .example("Bad Request")
                        .description("HTTP status code text"))
                    .addProperty("errorMessage", new Schema<String>()
                        .type("string")
                        .example("Validation failed")
                        .description("Summary error message"))
                    .addProperty("errorDateTime", new Schema<String>()
                        .type("string")
                        .format("date-time")
                        .example("2025-01-22T10:30:00Z")
                        .description("Timestamp when the error occurred (RFC 3339 UTC format with Z suffix)"))
                    .addProperty("errors", new Schema<Object>()
                        .type("array")
                        .items(new Schema<Object>().$ref("#/components/schemas/ErrorDetail"))
                        .description("Array of detailed error information"))
                    .description("DCSA-style standardized error response format"))
                
                // Define ErrorDetail schema
                .addSchemas("ErrorDetail", new Schema<Object>()
                    .type("object")
                    .addRequiredItem("errorCode")
                    .addRequiredItem("reason")
                    .addRequiredItem("message")
                    .addProperty("errorCode", new Schema<Integer>()
                        .type("integer")
                        .format("int32")
                        .example(1001)
                        .description("Implementation-specific error code"))
                    .addProperty("field", new Schema<String>()
                        .type("string")
                        .nullable(true)
                        .example("facilityCode")
                        .description("Name of the field that caused the error"))
                    .addProperty("value", new Schema<String>()
                        .type("string")
                        .nullable(true)
                        .example("SG SIN WHS")
                        .description("Value that caused the error"))
                    .addProperty("reason", new Schema<String>()
                        .type("string")
                        .example("validationError")
                        .description("Type of error"))
                    .addProperty("message", new Schema<String>()
                        .type("string")
                        .example("Spaces not allowed in facility code")
                        .description("Human-readable error message"))
                    .description("Detailed error information"))
                
                // Define reusable error responses
                .addResponses("UnauthorizedError", new ApiResponse()
                    .description("Unauthorized - Authentication required")
                    .content(new Content()
                        .addMediaType("application/json", new MediaType()
                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                            .addExamples("unauthorized", new Example()
                                .summary("Missing or invalid JWT token")
                                .value("{\n" +
                                    "  \"httpMethod\": \"GET\",\n" +
                                    "  \"requestUri\": \"/api/v1/bookings\",\n" +
                                    "  \"statusCode\": 401,\n" +
                                    "  \"statusCodeText\": \"Unauthorized\",\n" +
                                    "  \"errorMessage\": \"Authentication required\",\n" +
                                    "  \"errorDateTime\": \"2025-01-22T10:30:00Z\",\n" +
                                    "  \"errors\": [\n" +
                                    "    {\n" +
                                    "      \"errorCode\": 1401,\n" +
                                    "      \"field\": null,\n" +
                                    "      \"value\": null,\n" +
                                    "      \"reason\": \"unauthorized\",\n" +
                                    "      \"message\": \"Full authentication is required to access this resource\"\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}")))))
                
                .addResponses("BadRequestError", new ApiResponse()
                    .description("Bad Request - Invalid request parameters or validation errors")
                    .content(new Content()
                        .addMediaType("application/json", new MediaType()
                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                            .addExamples("validationError", new Example()
                                .summary("Validation failed")
                                .value("{\n" +
                                    "  \"httpMethod\": \"POST\",\n" +
                                    "  \"requestUri\": \"/api/v1/bookings\",\n" +
                                    "  \"statusCode\": 400,\n" +
                                    "  \"statusCodeText\": \"Bad Request\",\n" +
                                    "  \"errorMessage\": \"Validation failed\",\n" +
                                    "  \"errorDateTime\": \"2025-01-22T10:30:00Z\",\n" +
                                    "  \"errors\": [\n" +
                                    "    {\n" +
                                    "      \"errorCode\": 1001,\n" +
                                    "      \"field\": \"facilityCode\",\n" +
                                    "      \"value\": \"SG SIN WHS\",\n" +
                                    "      \"reason\": \"validationError\",\n" +
                                    "      \"message\": \"Spaces not allowed in facility code\"\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}")))))
                
                .addResponses("ForbiddenError", new ApiResponse()
                    .description("Forbidden - Access denied")
                    .content(new Content()
                        .addMediaType("application/json", new MediaType()
                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                            .addExamples("forbidden", new Example()
                                .summary("Access denied")
                                .value("{\n" +
                                    "  \"httpMethod\": \"DELETE\",\n" +
                                    "  \"requestUri\": \"/api/v1/admin/users/123\",\n" +
                                    "  \"statusCode\": 403,\n" +
                                    "  \"statusCodeText\": \"Forbidden\",\n" +
                                    "  \"errorMessage\": \"Access is denied\",\n" +
                                    "  \"errorDateTime\": \"2025-01-22T10:30:00Z\",\n" +
                                    "  \"errors\": [\n" +
                                    "    {\n" +
                                    "      \"errorCode\": 1403,\n" +
                                    "      \"field\": null,\n" +
                                    "      \"value\": null,\n" +
                                    "      \"reason\": \"forbidden\",\n" +
                                    "      \"message\": \"User does not have permission to access this resource\"\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}")))))
                
                .addResponses("NotFoundError", new ApiResponse()
                    .description("Not Found - Resource not found")
                    .content(new Content()
                        .addMediaType("application/json", new MediaType()
                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                            .addExamples("notFound", new Example()
                                .summary("Resource not found")
                                .value("{\n" +
                                    "  \"httpMethod\": \"GET\",\n" +
                                    "  \"requestUri\": \"/api/v1/bookings/12345\",\n" +
                                    "  \"statusCode\": 404,\n" +
                                    "  \"statusCodeText\": \"Not Found\",\n" +
                                    "  \"errorMessage\": \"Resource not found\",\n" +
                                    "  \"errorDateTime\": \"2025-01-22T10:30:00Z\",\n" +
                                    "  \"errors\": [\n" +
                                    "    {\n" +
                                    "      \"errorCode\": 1404,\n" +
                                    "      \"field\": \"bookingId\",\n" +
                                    "      \"value\": \"12345\",\n" +
                                    "      \"reason\": \"notFound\",\n" +
                                    "      \"message\": \"No booking found with ID: 12345\"\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}")))))
                
                .addResponses("BusinessRuleError", new ApiResponse()
                    .description("Unprocessable Entity - Business rule violation")
                    .content(new Content()
                        .addMediaType("application/json", new MediaType()
                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                            .addExamples("businessRule", new Example()
                                .summary("Business rule violation")
                                .value("{\n" +
                                    "  \"httpMethod\": \"POST\",\n" +
                                    "  \"requestUri\": \"/api/v1/bookings\",\n" +
                                    "  \"statusCode\": 422,\n" +
                                    "  \"statusCodeText\": \"Unprocessable Entity\",\n" +
                                    "  \"errorMessage\": \"Booking validation failed\",\n" +
                                    "  \"errorDateTime\": \"2025-01-22T10:30:00Z\",\n" +
                                    "  \"errors\": [\n" +
                                    "    {\n" +
                                    "      \"errorCode\": 7201,\n" +
                                    "      \"field\": \"pol\",\n" +
                                    "      \"value\": \"XXXX\",\n" +
                                    "      \"reason\": \"businessRule\",\n" +
                                    "      \"message\": \"Origin port not serviceable\"\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}")))))
                
                .addResponses("ServerError", new ApiResponse()
                    .description("Internal Server Error")
                    .content(new Content()
                        .addMediaType("application/json", new MediaType()
                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                            .addExamples("serverError", new Example()
                                .summary("Internal server error")
                                .value("{\n" +
                                    "  \"httpMethod\": \"POST\",\n" +
                                    "  \"requestUri\": \"/api/v1/bookings\",\n" +
                                    "  \"statusCode\": 500,\n" +
                                    "  \"statusCodeText\": \"Internal Server Error\",\n" +
                                    "  \"errorMessage\": \"The supplied data could not be accepted\",\n" +
                                    "  \"errorDateTime\": \"2025-01-22T10:30:00Z\",\n" +
                                    "  \"errors\": [\n" +
                                    "    {\n" +
                                    "      \"errorCode\": 1500,\n" +
                                    "      \"field\": null,\n" +
                                    "      \"value\": null,\n" +
                                    "      \"reason\": \"internalError\",\n" +
                                    "      \"message\": \"An unexpected error occurred. Please try again later.\"\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}")))))
                
                .addResponses("MethodNotAllowedError", new ApiResponse()
                    .description("Method Not Allowed")
                    .content(new Content()
                        .addMediaType("application/json", new MediaType()
                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                            .addExamples("methodNotAllowed", new Example()
                                .summary("HTTP method not allowed")
                                .value("{\n" +
                                    "  \"httpMethod\": \"DELETE\",\n" +
                                    "  \"requestUri\": \"/api/v1/bookings\",\n" +
                                    "  \"statusCode\": 405,\n" +
                                    "  \"statusCodeText\": \"Method Not Allowed\",\n" +
                                    "  \"errorMessage\": \"Method Not Allowed\",\n" +
                                    "  \"errorDateTime\": \"2025-01-22T10:30:00Z\",\n" +
                                    "  \"errors\": [\n" +
                                    "    {\n" +
                                    "      \"errorCode\": 1005,\n" +
                                    "      \"field\": null,\n" +
                                    "      \"value\": null,\n" +
                                    "      \"reason\": \"methodNotAllowed\",\n" +
                                    "      \"message\": \"Request method 'DELETE' not supported\"\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}")))))
            );
    }

}