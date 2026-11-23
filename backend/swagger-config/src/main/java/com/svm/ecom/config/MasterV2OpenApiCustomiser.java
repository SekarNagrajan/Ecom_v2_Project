package com.svm.ecom.config;

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
 * Post-processes the generated OpenAPI spec for the master-v2 group so that
 * the output matches the client request JSON structure:
 *
 *   - Removes DCSA-style ErrorResponse / ErrorDetail / ErrorReason schemas
 *   - Removes V1 reusable responses
 *   - Registers client-standard reusable responses under components/responses
 *   - Builds all error responses (400-503) inline per-operation with correct
 *     endpoint-specific instance URL and status-appropriate examples
 *   - Replaces 200 responses with $ref to reusable success responses
 */
public class MasterV2OpenApiCustomiser implements OpenApiCustomiser {

    private static final Map<String, String> SUCCESS_RESPONSE_REFS;
    static {
        SUCCESS_RESPONSE_REFS = new LinkedHashMap<>();
        SUCCESS_RESPONSE_REFS.put("getAgencies", "GetAgenciesResponse");
        SUCCESS_RESPONSE_REFS.put("getCommodities", "GetCommoditiesResponse");
        SUCCESS_RESPONSE_REFS.put("getDgPackagings", "GetDgPackagingsResponse");
        SUCCESS_RESPONSE_REFS.put("getImdgEntries", "GetImdgEntriesResponse");
    }

    private static final Map<String, String> OPERATION_INSTANCE;
    static {
        OPERATION_INSTANCE = new LinkedHashMap<>();
        OPERATION_INSTANCE.put("getAgencies", "/api/v2/master/agencies");
        OPERATION_INSTANCE.put("getCommodities", "/api/v2/master/commodities");
        OPERATION_INSTANCE.put("getDgPackagings", "/api/v2/master/dg-packagings");
        OPERATION_INSTANCE.put("getImdgEntries", "/api/v2/master/imdg-entries");
    }

    @Override
    public void customise(OpenAPI openApi) {
        Components components = openApi.getComponents();
        if (components == null) {
            components = new Components();
            openApi.setComponents(components);
        }

        removeV1Components(components);
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

    // ======================== Register Reusable Responses ========================

    @SuppressWarnings("rawtypes")
    private void registerReusableResponses(Components components) {
        components.addResponses("BadRequest", createErrorResponse(
            "The server could not understand the request due to invalid syntax or parameters."));

        components.addResponses("Unauthorized", createErrorResponse(
            "Authentication failed (Missing or invalid Bearer token)."));

        components.addResponses("Forbidden", createErrorResponse(
            "The client does not have access rights to the content."));

        components.addResponses("InternalServerError", createErrorResponse(
            "An unexpected server-side error occurred."));

        components.addResponses("ServiceUnavailable", createErrorResponse(
            "The service is currently not available."));

        components.addResponses("GetAgenciesResponse", new ApiResponse()
            .description("A list of agencies.")
            .content(new Content().addMediaType("application/json",
                new MediaType().schema(new ArraySchema()
                    .items(new Schema().$ref("#/components/schemas/Agency"))))));

        components.addResponses("GetCommoditiesResponse", new ApiResponse()
            .description("A list of commodities.")
            .content(new Content().addMediaType("application/json",
                new MediaType().schema(new ArraySchema()
                    .items(new Schema().$ref("#/components/schemas/Commodity"))))));

        components.addResponses("GetDgPackagingsResponse", new ApiResponse()
            .description("A list of DG packagings.")
            .content(new Content().addMediaType("application/json",
                new MediaType().schema(new ArraySchema()
                    .items(new Schema().$ref("#/components/schemas/DgPackaging"))))));

        components.addResponses("GetImdgEntriesResponse", new ApiResponse()
            .description("A list of IMDG entries.")
            .content(new Content().addMediaType("application/json",
                new MediaType().schema(new ArraySchema()
                    .items(new Schema().$ref("#/components/schemas/ImdgEntry"))))));
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
     * Build all error responses (400/401/403/500/503) inline with the correct
     * instance URL for the specific endpoint.
     */
    private void replaceAllErrorsInline(Operation operation, String operationId, String instance) {
        if (operation.getResponses() == null) return;

        // 400 — per-API validation example (excluded for getAgencies: no input params, so no validation)
        if ("getAgencies".equals(operationId)) {
            operation.getResponses().remove("400");
        } else if (operation.getResponses().containsKey("400")) {
            operation.getResponses().addApiResponse("400", buildBadRequest(operationId, instance));
        }

        // 401
        if (operation.getResponses().containsKey("401")) {
            operation.getResponses().addApiResponse("401", buildInlineError(
                "Authentication failed (Missing or invalid Bearer token).",
                "Missing or invalid Bearer token.",
                "Unauthorized", 401, instance, "UNAUTHORIZED"));
        }

        // 403
        if (operation.getResponses().containsKey("403")) {
            operation.getResponses().addApiResponse("403", buildInlineError(
                "The client does not have access rights to the content.",
                "You do not have permission to access this resource.",
                "Forbidden", 403, instance, "FORBIDDEN"));
        }

        // 500
        if (operation.getResponses().containsKey("500")) {
            operation.getResponses().addApiResponse("500", buildInlineError(
                "An unexpected server-side error occurred.",
                "An unexpected server-side error occurred.",
                "Internal Server Error", 500, instance, "INTERNAL_ERROR"));
        }

        // 503
        if (operation.getResponses().containsKey("503")) {
            operation.getResponses().addApiResponse("503", buildInlineError(
                "The service is currently not available.",
                "The service is currently not available. Please try again later.",
                "Service Unavailable", 503, instance, "SERVICE_UNAVAILABLE"));
        }
    }

    @SuppressWarnings("rawtypes")
    private ApiResponse buildBadRequest(String operationId, String instance) {
        String detail;
        List<Map<String, Object>> nestedErrors = new ArrayList<>();

        if ("getAgencies".equals(operationId)) {
            detail = "Agency data validation failed. Please review the request.";
            Map<String, Object> nested = new LinkedHashMap<>();
            nested.put("detail", "Invalid or missing query parameter.");
            nested.put("parameter", "agencyCode");
            nested.put("code", "INVALID_PARAMETER");
            nestedErrors.add(nested);
        } else if ("getCommodities".equals(operationId)) {
            detail = "Commodity data validation failed. Please review the request.";
            Map<String, Object> nested = new LinkedHashMap<>();
            nested.put("detail", "Invalid or missing query parameter.");
            nested.put("parameter", "commodityCode");
            nested.put("code", "INVALID_PARAMETER");
            nestedErrors.add(nested);
        } else if ("getDgPackagings".equals(operationId)) {
            detail = "DG packaging data validation failed. Please review the request.";
            Map<String, Object> nested = new LinkedHashMap<>();
            nested.put("detail", "Invalid usage value. Valid values are: OUTER, INNER.");
            nested.put("parameter", "usage");
            nested.put("code", "INVALID_PARAMETER");
            nestedErrors.add(nested);
        } else if ("getImdgEntries".equals(operationId)) {
            detail = "IMDG data validation failed. Please review the request.";
            Map<String, Object> nested = new LinkedHashMap<>();
            nested.put("detail", "unNo is required and must be 4 digits.");
            nested.put("parameter", "unNo");
            nested.put("code", "DG_UNNO_INVALID");
            nestedErrors.add(nested);
        } else {
            detail = "Validation failed. Please review the request.";
            Map<String, Object> nested = new LinkedHashMap<>();
            nested.put("detail", "Invalid request parameter.");
            nested.put("code", "BAD_REQUEST");
            nestedErrors.add(nested);
        }

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
