package com.svm.ecom.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svm.ecom.docs.SwaggerDocs;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomiser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Post-processes the generated OpenAPI spec for the contract-v2 group so that
 * the output matches the client request JSON structure for Contract API endpoints.
 */
public class ContractV2OpenApiCustomiser implements OpenApiCustomiser {

    private static final Map<String, String> SUCCESS_RESPONSE_REFS;
    static {
        SUCCESS_RESPONSE_REFS = new LinkedHashMap<>();
        SUCCESS_RESPONSE_REFS.put("getCustomerContracts", "GetCustomerContractsResponse");
        SUCCESS_RESPONSE_REFS.put("getCustomerContractById", "GetCustomerContractByIdResponse");
    }

    private static final Map<String, String> OPERATION_INSTANCE;
    static {
        OPERATION_INSTANCE = new LinkedHashMap<>();
        OPERATION_INSTANCE.put("getCustomerContracts", "/api/v2/contracts");
        OPERATION_INSTANCE.put("getCustomerContractById", "/api/v2/contracts/{contractId}");
        OPERATION_INSTANCE.put("downloadContract", "/api/v2/contracts/{contractId}/download");
    }

    @Override
    public void customise(OpenAPI openApi) {
        Components components = openApi.getComponents();
        if (components == null) {
            components = new Components();
            openApi.setComponents(components);
        }

        removeV1Components(components);
        registerErrorSchemas(components);
        registerReusableResponses(components);
        transformOperations(openApi);
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

    // ======================== Register Error Schemas ========================

    @SuppressWarnings("rawtypes")
    private void registerErrorSchemas(Components components) {
        Schema nestedErrorSchema = new Schema()
            .type("object")
            .description("Nested details about a parent error")
            .addRequiredItem("detail")
            .addProperties("detail", new Schema().type("string")
                .description("A granular description of the specific error"))
            .addProperties("pointer", new Schema().type("string")
                .description("A pointer to a specific request body property that is the source of the error"))
            .addProperties("parameter", new Schema().type("string")
                .description("The name of the query or path parameter that is the source of the error"))
            .addProperties("header", new Schema().type("string")
                .description("The name of the header that is the source of the error"))
            .addProperties("code", new Schema().type("string")
                .description("A string containing additional provider-specific codes to identify the error context"));
        components.addSchemas("NestedErrorMessage", nestedErrorSchema);

        // TransportService enum
        Schema transportServiceSchema = new Schema()
            .type("string")
            .description("Transport service type");
        transportServiceSchema.setEnum(java.util.Arrays.asList(
            "DOOR / DOOR", "DOOR / CY", "FI / CY", "FI / FO", "CY / DOOR", "CY / CY", "CY / FO"));
        components.addSchemas("TransportService", transportServiceSchema);

        // OfrType enum
        Schema ofrTypeSchema = new Schema()
            .type("string")
            .description("Ocean Freight Rate type: O – Ocean Freight Rate, A – All In");
        ofrTypeSchema.setEnum(java.util.Arrays.asList("O", "A"));
        components.addSchemas("OfrType", ofrTypeSchema);

        Schema errorMessageSchema = new Schema()
            .type("object")
            .description("Details about an error that occurred")
            .addRequiredItem("detail")
            .addProperties("detail", new Schema().type("string")
                .description("A human-readable explanation specific to this occurrence of the problem"))
            .addProperties("title", new Schema().type("string")
                .description("A short, human-readable summary of the problem type"))
            .addProperties("status", new Schema().type("integer")
                .description("The HTTP status code generated by the origin server"))
            .addProperties("instance", new Schema().type("string")
                .description("A URI reference that identifies the specific occurrence of the problem"))
            .addProperties("code", new Schema().type("string")
                .description("An API specific error code"))
            .addProperties("errors", new ArraySchema()
                .description("An array of error details")
                .items(new Schema().$ref("#/components/schemas/NestedErrorMessage")));
        components.addSchemas("ErrorMessage", errorMessageSchema);
    }

    // ======================== Register Reusable Responses ========================

    @SuppressWarnings("rawtypes")
    private void registerReusableResponses(Components components) {
        components.addResponses("BadRequest", createErrorResponse(
            "The server could not understand the request due to invalid syntax or parameters."));

        components.addResponses("Unauthorized", createErrorResponse(
            "Authentication failed (Missing or invalid Bearer token)."));

        components.addResponses("Forbidden", createErrorResponse(
            "The client does not have access rights to the content."));

        components.addResponses("NotFound", createErrorResponse(
            "The contract could not be found or you don't have access to it."));

        components.addResponses("InternalServerError", createErrorResponse(
            "An unexpected server-side error occurred."));

        components.addResponses("ServiceUnavailable", createErrorResponse(
            "The service is currently not available."));

        MediaType contractListMedia = new MediaType()
            .schema(new ArraySchema()
                .items(new Schema().$ref("#/components/schemas/CustomerContractListItem")));
        contractListMedia.setExample(parseJson(SwaggerDocs.Examples.CONTRACT_LIST_200));
        components.addResponses("GetCustomerContractsResponse", new ApiResponse()
            .description("Response object containing a list of customer contracts.")
            .content(new Content().addMediaType("application/json", contractListMedia)));

        MediaType contractDetailMedia = new MediaType()
            .schema(new Schema().$ref("#/components/schemas/CustomerContract"));
        contractDetailMedia.setExample(parseJson(SwaggerDocs.Examples.CONTRACT_DETAIL_200));
        components.addResponses("GetCustomerContractByIdResponse", new ApiResponse()
            .description("Response object containing a single customer contract.")
            .content(new Content().addMediaType("application/json", contractDetailMedia)));
    }

    // ======================== Path Transformation ========================

    private void transformOperations(OpenAPI openApi) {
        if (openApi.getPaths() == null) return;

        openApi.getPaths().forEach((path, pathItem) ->
            pathItem.readOperations().forEach(operation -> {
                String operationId = operation.getOperationId();
                String instance = OPERATION_INSTANCE.getOrDefault(operationId, path);
                replaceAllErrorsInline(operation, operationId, instance);
                replaceSuccessResponseWithRef(operation);
            })
        );
    }

    /**
     * Build all error responses (401/403/404/500/503) inline with the correct
     * instance URL for the specific endpoint.
     */
    private void replaceAllErrorsInline(Operation operation, String operationId, String instance) {
        if (operation.getResponses() == null) return;

        // 400 (for getCustomerContractById and downloadContract)
        if ("getCustomerContractById".equals(operationId) || "downloadContract".equals(operationId)) {
            operation.getResponses().addApiResponse("400", buildBadRequestError(instance));
        }

        // 401
        operation.getResponses().addApiResponse("401", buildInlineError(
            "Authentication failed (Missing or invalid Bearer token).",
            "Authentication failed (Missing or invalid Bearer token).",
            "Unauthorized", 401, instance, "UNAUTHORIZED"));

        // 403
        operation.getResponses().addApiResponse("403", buildInlineError(
            "The client does not have access rights to the content.",
            "The client does not have access rights to the content.",
            "Forbidden", 403, instance, "FORBIDDEN"));

        // 404 (for getCustomerContractById and downloadContract)
        if ("getCustomerContractById".equals(operationId) || "downloadContract".equals(operationId)) {
            operation.getResponses().addApiResponse("404", buildInlineError(
                "The contract could not be found or you don't have access to it.",
                "The contract could not be found or you don't have access to it.",
                "Not Found", 404, instance, "CONTRACT_NOT_FOUND"));
        }

        // 500
        operation.getResponses().addApiResponse("500", buildInlineError(
            "An unexpected server-side error occurred.",
            "An unexpected server-side error occurred.",
            "Internal Server Error", 500, instance, "INTERNAL_ERROR"));

        // 503
        operation.getResponses().addApiResponse("503", buildInlineError(
            "The service is currently not available.",
            "The service is currently not available.",
            "Service Unavailable", 503, instance, "SERVICE_UNAVAILABLE"));
    }

    private void replaceSuccessResponseWithRef(Operation operation) {
        if (operation.getResponses() == null || operation.getOperationId() == null) return;

        String refName = SUCCESS_RESPONSE_REFS.get(operation.getOperationId());
        if (refName != null && operation.getResponses().containsKey("200")) {
            operation.getResponses().addApiResponse("200",
                new ApiResponse().$ref("#/components/responses/" + refName));
        }
    }

    // ======================== Helpers ========================

    @SuppressWarnings("rawtypes")
    private ApiResponse buildInlineError(String description, String detail, String title,
                                          int status, String instance, String code) {
        Map<String, Object> nestedError = new LinkedHashMap<>();
        nestedError.put("detail", detail);
        nestedError.put("code", code);

        List<Map<String, Object>> errors = new ArrayList<>();
        errors.add(nestedError);

        Map<String, Object> example = new LinkedHashMap<>();
        example.put("detail", detail);
        example.put("title", title);
        example.put("status", status);
        example.put("instance", instance);
        example.put("code", code);
        example.put("errors", errors);

        MediaType mediaType = new MediaType()
            .schema(new Schema().$ref("#/components/schemas/ErrorMessage"));
        mediaType.setExample(example);

        return new ApiResponse()
            .description(description)
            .content(new Content().addMediaType("application/json", mediaType));
    }

    @SuppressWarnings("rawtypes")
    private ApiResponse buildBadRequestError(String instance) {
        Map<String, Object> nestedError1 = new LinkedHashMap<>();
        nestedError1.put("detail", "contractId must not be empty.");
        nestedError1.put("parameter", "contractId");
        nestedError1.put("code", "INVALID_CONTRACT_ID");

        Map<String, Object> nestedError2 = new LinkedHashMap<>();
        nestedError2.put("detail", "amendmentNumber must be a valid positive integer.");
        nestedError2.put("parameter", "amendmentNumber");
        nestedError2.put("code", "INVALID_AMENDMENT_NUMBER");

        List<Map<String, Object>> errors = new ArrayList<>();
        errors.add(nestedError1);
        errors.add(nestedError2);

        Map<String, Object> example = new LinkedHashMap<>();
        example.put("detail", "Invalid request parameters. Please review the provided contractId and amendmentNumber.");
        example.put("title", "Bad Request");
        example.put("status", 400);
        example.put("instance", instance);
        example.put("code", "VALIDATION_FAILED");
        example.put("errors", errors);

        MediaType mediaType = new MediaType()
            .schema(new Schema().$ref("#/components/schemas/ErrorMessage"));
        mediaType.setExample(example);

        return new ApiResponse()
            .description("The server could not understand the request due to invalid syntax or parameters.")
            .content(new Content().addMediaType("application/json", mediaType));
    }

    @SuppressWarnings("rawtypes")
    private ApiResponse createErrorResponse(String description) {
        return new ApiResponse()
            .description(description)
            .content(new Content().addMediaType("application/json",
                new MediaType().schema(new Schema().$ref("#/components/schemas/ErrorMessage"))));
    }

    private Object parseJson(String json) {
        try {
            return new ObjectMapper().readValue(json, Object.class);
        } catch (Exception e) {
            return json;
        }
    }
}