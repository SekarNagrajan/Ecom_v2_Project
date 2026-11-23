package com.svm.ecom.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomiser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Post-processes the generated OpenAPI spec for the customer-v2 group:
 *
 *   - Replaces global bearerAuth with customer login authentication
 *   - Removes V1 reusable error responses / schemas
 *   - Registers customer-specific reusable responses under components/responses
 *   - Builds all error responses (400-503) inline per-operation with correct
 *     endpoint-specific instance URL and status-appropriate examples
 */
public class CustomerV2OpenApiCustomiser implements OpenApiCustomiser {

    private static final String CUSTOMER_AUTH_SCHEME = "customerBearerAuth";

    @Override
    public void customise(OpenAPI openApi) {
        Components components = openApi.getComponents();
        if (components == null) {
            components = new Components();
            openApi.setComponents(components);
        }

        replaceWithCustomerAuth(openApi, components);
        removeV1Components(components);
        registerReusableResponses(components);
        transformOperations(openApi);
    }

    // ======================== Customer Authentication ========================

    private void replaceWithCustomerAuth(OpenAPI openApi, Components components) {
        if (components.getSecuritySchemes() != null) {
            components.getSecuritySchemes().remove("bearerAuth");
        }

        components.addSecuritySchemes(CUSTOMER_AUTH_SCHEME, new SecurityScheme()
            .name(CUSTOMER_AUTH_SCHEME)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("Customer JWT Authorization header using the Bearer scheme. "
                + "Obtain the token via the customer login endpoint. "
                + "Format: 'Authorization: Bearer {token}'"));

        openApi.setSecurity(Collections.singletonList(
            new SecurityRequirement().addList(CUSTOMER_AUTH_SCHEME)));
    }

    // ======================== Remove V1 Artifacts ========================

    private void removeV1Components(Components components) {
        if (components.getSchemas() != null) {
            components.getSchemas().remove("ErrorResponse");
            components.getSchemas().remove("ErrorDetail");
            components.getSchemas().remove("ErrorReason");
        }

        if (components.getResponses() != null) {
            components.getResponses().remove("UnauthorizedError");
            components.getResponses().remove("BadRequestError");
            components.getResponses().remove("ForbiddenError");
            components.getResponses().remove("NotFoundError");
            components.getResponses().remove("BusinessRuleError");
            components.getResponses().remove("ServerError");
            components.getResponses().remove("MethodNotAllowedError");
        }
    }

    // ======================== Register Reusable Responses ========================

    @SuppressWarnings("rawtypes")
    private void registerReusableResponses(Components components) {
        components.addResponses("BadRequest", createErrorResponse(
            "The server could not understand the request due to invalid syntax or parameters."));

        components.addResponses("Unauthorized", createErrorResponse(
            "Authentication failed (Missing or invalid customer Bearer token)."));

        components.addResponses("Forbidden", createErrorResponse(
            "The client does not have access rights to the content."));

        components.addResponses("InternalServerError", createErrorResponse(
            "An unexpected server-side error occurred."));

        components.addResponses("ServiceUnavailable", createErrorResponse(
            "The service is currently not available."));

        components.addResponses("GetCustomersResponse", new ApiResponse()
            .description("A list of customers.")
            .content(new Content().addMediaType("application/json",
                new MediaType().schema(new ArraySchema()
                    .items(new Schema().$ref("#/components/schemas/Customer"))))));
    }

    // ======================== Path Transformation ========================

    private void transformOperations(OpenAPI openApi) {
        if (openApi.getPaths() == null) return;

        openApi.getPaths().forEach((path, pathItem) ->
            pathItem.readOperations().forEach(operation -> {
                String operationId = operation.getOperationId();
                String instance = "/api/v2/master/customers";
                replaceAllErrorsInline(operation, operationId, instance);
                replaceSuccessResponseWithRef(operation);
            })
        );
    }

    private void replaceAllErrorsInline(Operation operation, String operationId, String instance) {
        if (operation.getResponses() == null) return;

        if (operation.getResponses().containsKey("400")) {
            operation.getResponses().addApiResponse("400", buildBadRequest(instance));
        }

        if (operation.getResponses().containsKey("401")) {
            operation.getResponses().addApiResponse("401", buildInlineError(
                "Authentication failed (Missing or invalid customer Bearer token).",
                "Missing or invalid customer Bearer token.",
                "Unauthorized", 401, instance, "UNAUTHORIZED"));
        }

        if (operation.getResponses().containsKey("403")) {
            operation.getResponses().addApiResponse("403", buildInlineError(
                "The client does not have access rights to the content.",
                "You do not have permission to access this resource.",
                "Forbidden", 403, instance, "FORBIDDEN"));
        }

        if (operation.getResponses().containsKey("500")) {
            operation.getResponses().addApiResponse("500", buildInlineError(
                "An unexpected server-side error occurred.",
                "An unexpected server-side error occurred.",
                "Internal Server Error", 500, instance, "INTERNAL_ERROR"));
        }

        if (operation.getResponses().containsKey("503")) {
            operation.getResponses().addApiResponse("503", buildInlineError(
                "The service is currently not available.",
                "The service is currently not available. Please try again later.",
                "Service Unavailable", 503, instance, "SERVICE_UNAVAILABLE"));
        }
    }

    @SuppressWarnings("rawtypes")
    private ApiResponse buildBadRequest(String instance) {
        String detail = "Customer data validation failed. Please review the provided data.";
        List<Map<String, Object>> nestedErrors = new ArrayList<>();
        Map<String, Object> nested = new LinkedHashMap<>();
        nested.put("detail", "The email address does not match the required format.");
        nested.put("pointer", "#/email");
        nested.put("code", "INVALID_EMAIL_FORMAT");
        nestedErrors.add(nested);

        Map<String, Object> example = new LinkedHashMap<>();
        example.put("detail", detail);
        example.put("title", "Validation Failed");
        example.put("status", 400);
        example.put("instance", instance);
        example.put("code", "VALIDATION_FAILED");
        example.put("errors", nestedErrors);

        MediaType mediaType = new MediaType()
            .schema(new Schema().$ref("#/components/schemas/ErrorMessage"));
        mediaType.setExample(example);

        return new ApiResponse()
            .description("The server could not understand the request due to invalid syntax or parameters.")
            .content(new Content().addMediaType("application/json", mediaType));
    }

    private void replaceSuccessResponseWithRef(Operation operation) {
        if (operation.getResponses() == null || operation.getOperationId() == null) return;

        if ("getCustomers".equals(operation.getOperationId())
                && operation.getResponses().containsKey("200")) {
            operation.getResponses().addApiResponse("200",
                new ApiResponse().$ref("#/components/responses/GetCustomersResponse"));
        }
    }

    // ======================== Helpers ========================

    @SuppressWarnings("rawtypes")
    private ApiResponse buildInlineError(String description, String detail, String title,
                                          int status, String instance, String code) {
        Map<String, Object> example = new LinkedHashMap<>();
        example.put("detail", detail);
        example.put("title", title);
        example.put("status", status);
        example.put("instance", instance);
        example.put("code", code);

        MediaType mediaType = new MediaType()
            .schema(new Schema().$ref("#/components/schemas/ErrorMessage"));
        mediaType.setExample(example);

        return new ApiResponse()
            .description(description)
            .content(new Content().addMediaType("application/json", mediaType));
    }

    @SuppressWarnings("rawtypes")
    private ApiResponse createErrorResponse(String description) {
        return new ApiResponse()
            .description(description)
            .content(new Content().addMediaType("application/json",
                new MediaType().schema(new Schema().$ref("#/components/schemas/ErrorMessage"))));
    }
}
