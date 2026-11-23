package com.svm.ecom.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import com.svm.ecom.security.decommissioned.BookingV2DecommissionedEndpointSupport;
import org.springdoc.core.customizers.OpenApiCustomiser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Post-processes the generated OpenAPI spec for the booking-v2 group
 * so that the output matches the client's reference-based structure:
 *
 *   - Reusable parameters  (components/parameters)
 *   - Reusable responses   (components/responses)
 *   - Named schemas        (DocumentList, DocumentUploadRequest)
 *   - Inline definitions replaced with $ref
 *   - 201 / 204 bodies stripped
 *   - Inline error responses (like customer-v2) for booking CRUD:
 *     getBookings, createBooking, getBookingById, updateBookingById
 *   - Drops V1-only {@code DangerousGoodsDTO} from components (V2 uses {@code DangerousGoodsInfo} only)
 */
public class BookingV2OpenApiCustomiser implements OpenApiCustomiser {

    private static final Map<String, String> ERROR_RESPONSE_REFS;
    static {
        ERROR_RESPONSE_REFS = new LinkedHashMap<>();
        ERROR_RESPONSE_REFS.put("400", "BadRequest");
        ERROR_RESPONSE_REFS.put("401", "Unauthorized");
        ERROR_RESPONSE_REFS.put("403", "Forbidden");
        ERROR_RESPONSE_REFS.put("404", "NotFound");
        ERROR_RESPONSE_REFS.put("409", "Conflict");
        ERROR_RESPONSE_REFS.put("500", "InternalServerError");
        ERROR_RESPONSE_REFS.put("503", "ServiceUnavailable");
    }

    /** Operation IDs that get inline error responses with endpoint-specific instance (V2 booking, client error architecture). */
    private static final Set<String> BOOKING_CRUD_OPERATION_IDS = new HashSet<>(Arrays.asList(
        "getBookings",
        "createBooking",
        "getBookingById",
        "updateBookingById"
    ));

    private static final Set<String> VGM_OPERATION_IDS = new HashSet<>(Arrays.asList(
        "getVgm",
        "upsertVgm"
    ));

    @Override
    public void customise(OpenAPI openApi) {
        removeDecommissionedBookingV2CrudPaths(openApi);

        addTagDescription(openApi, "Booking Summary",
            "Paged list of bookings for the authenticated customer with sorting, filtering and offset-based pagination.");

        Components components = openApi.getComponents();
        if (components == null) {
            components = new Components();
            openApi.setComponents(components);
        }

        registerParameters(components);
        registerExtraSchemas(components);
        registerVgmSchemas(components);
        enrichExistingSchemas(components);
        removeUnusedComponents(components);
        registerResponses(components);
        transformOperations(openApi);
    }

    private void removeDecommissionedBookingV2CrudPaths(OpenAPI openApi) {
        if (openApi.getPaths() == null) {
            return;
        }
        BookingV2DecommissionedEndpointSupport.OPENAPI_PATHS_TO_REMOVE.forEach(openApi.getPaths()::remove);
    }

    // ======================== Tag Descriptions ========================

    private void addTagDescription(OpenAPI openApi, String tagName, String description) {
        List<Tag> tags = openApi.getTags();
        if (tags == null) {
            tags = new ArrayList<>();
            openApi.setTags(tags);
        }
        boolean found = tags.stream().anyMatch(t -> tagName.equals(t.getName()));
        if (!found) {
            tags.add(new Tag().name(tagName).description(description));
        } else {
            tags.stream().filter(t -> tagName.equals(t.getName())).findFirst().ifPresent(t -> t.setDescription(description));
        }
    }

    // ======================== Component Registration ========================

    private void registerParameters(Components components) {
        components.addParameters("bookingId", new Parameter()
            .name("bookingId")
            .in("path")
            .required(true)
            .description("Booking id, might be a booking request id (e.g. BKON123) "
                + "or a confirmed booking id (e.g. TSHG1234)")
            .schema(new StringSchema()));

        components.addParameters("fileName", new Parameter()
            .name("fileName")
            .in("path")
            .required(true)
            .description("Name of a file")
            .schema(new StringSchema()));
    }

    @SuppressWarnings("rawtypes")
    private void registerExtraSchemas(Components components) {
        // DocumentList — array of DocumentMetadata with inline example
        ArraySchema docListSchema = new ArraySchema();
        docListSchema.setDescription("List of documents.");
        docListSchema.setItems(new Schema().$ref("#/components/schemas/DocumentMetadata"));

        List<Map<String, Object>> docListExample = new ArrayList<>();
        Map<String, Object> doc1 = new LinkedHashMap<>();
        doc1.put("fileName", "customs_manifest.pdf");
        doc1.put("fileSize", 102400);
        doc1.put("lastModified", "2026-01-05T09:00:00Z");
        docListExample.add(doc1);
        Map<String, Object> doc2 = new LinkedHashMap<>();
        doc2.put("fileName", "invoice.pdf");
        doc2.put("fileSize", 51200);
        doc2.put("lastModified", "2026-01-04T17:00:00Z");
        docListExample.add(doc2);
        docListSchema.setExample(docListExample);

        components.addSchemas("DocumentList", docListSchema);

        // DocumentUploadRequest
        ObjectSchema uploadSchema = new ObjectSchema();
        uploadSchema.setDescription("Request body for uploading a document.");
        uploadSchema.setRequired(Arrays.asList("files"));
        uploadSchema.addProperty("files", new ArraySchema()
            .items(new StringSchema().format("binary")));

        components.addSchemas("DocumentUploadRequest", uploadSchema);
    }

    @SuppressWarnings("rawtypes")
    private void registerVgmSchemas(Components components) {
        Schema countryCode = new StringSchema()
            .description("The ISO 3166-1 alpha-2 country code.")
            .minLength(2)
            .maxLength(2)
            .pattern("^[A-Z]{2}$")
            .example("US");
        components.addSchemas("CountryCode", countryCode);
    }

    @SuppressWarnings("rawtypes")
    private void enrichExistingSchemas(Components components) {
        if (components.getSchemas() == null) return;

        // DocumentMetadata — top-level example matching client spec
        Schema docMetaSchema = components.getSchemas().get("DocumentMetadata");
        if (docMetaSchema != null) {
            Map<String, Object> metaExample = new LinkedHashMap<>();
            metaExample.put("fileName", "customs_manifest.pdf");
            metaExample.put("fileSize", 102400);
            metaExample.put("lastModified", "2026-01-05T09:00:00Z");
            docMetaSchema.setExample(metaExample);
        }

        // ErrorMessage — top-level example matching client spec (without "type")
        Schema errorSchema = components.getSchemas().get("ErrorMessage");
        if (errorSchema != null) {
            Map<String, Object> errorExample = new LinkedHashMap<>();
            errorExample.put("detail", "The requested document was not found for the given booking.");
            errorExample.put("title", "Not Found");
            errorExample.put("status", 404);
            errorExample.put("instance", "/api/v2/bookings/BKON123/documents/invoice.pdf");
            errorExample.put("code", "NOT_FOUND");

            List<Map<String, Object>> nested = new ArrayList<>();
            Map<String, Object> nestedItem = new LinkedHashMap<>();
            nestedItem.put("detail", "File type not allowed: exe");
            nestedItem.put("pointer", "#/files");
            nestedItem.put("code", "FORBIDDEN_FILE_EXTENSION");
            nested.add(nestedItem);
            errorExample.put("errors", nested);

            errorSchema.setExample(errorExample);
        }

        // NestedErrorMessage — top-level example
        Schema nestedSchema = components.getSchemas().get("NestedErrorMessage");
        if (nestedSchema != null) {
            Map<String, Object> nestedExample = new LinkedHashMap<>();
            nestedExample.put("detail", "File type not allowed: exe");
            nestedExample.put("pointer", "#/files");
            nestedExample.put("code", "FORBIDDEN_FILE_EXTENSION");
            nestedSchema.setExample(nestedExample);
        }

        enrichVgmComponentSchemas(components);
    }

    @SuppressWarnings("rawtypes")
    private void enrichVgmComponentSchemas(Components components) {
        if (components.getSchemas() == null) {
            return;
        }

        Schema weightUnit = components.getSchemas().get("WeightUnit");
        if (weightUnit != null) {
            weightUnit.setDescription("Weight Unit: K – Kilos, T – Metric Tonnes");
            weightUnit.setEnum(Arrays.asList("K", "T"));
            weightUnit.setExample("K");
        }

        Schema vgmMethod = components.getSchemas().get("VgmMethod");
        if (vgmMethod != null) {
            vgmMethod.setDescription(
                "VGM Method: SM1 , SM2");
            vgmMethod.setEnum(Arrays.asList("SM1", "SM2"));
            vgmMethod.setExample("SM1");
        }

        Schema vgmStatus = components.getSchemas().get("VgmStatus");
        if (vgmStatus != null) {
            vgmStatus.setDescription("VGM Status: A – Active");
            vgmStatus.setEnum(Arrays.asList("A"));
            vgmStatus.setExample("A");
        }

        Schema containerVgm = components.getSchemas().get("ContainerVGM");
        if (containerVgm != null) {
            containerVgm.setDescription("VGM weight and equipment for one container");
            if (containerVgm.getProperties() != null) {
                Object weightProp = containerVgm.getProperties().get("weight");
                if (weightProp instanceof Schema) {
                    ((Schema) weightProp).setDescription("VGM weight in the provided unit");
                }
            }
            containerVgm.setRequired(Arrays.asList("equipmentId", "equipmentType", "weight", "unit"));
        }

        Schema vgmInfo = components.getSchemas().get("VGMInfo");
        if (vgmInfo != null) {
            vgmInfo.setDescription(
                "Verified Gross Mass (VGM) information. "
                    + "GET returns booking-level header fields with a containers array (one entry per equipment). "
                    + "PUT accepts the same shape via the VGMInfo request body.");
            if (vgmInfo.getProperties() != null) {
                vgmInfo.getProperties().remove("equipmentId");
                vgmInfo.getProperties().remove("equipmentType");
                vgmInfo.getProperties().remove("weight");
                Object versionProp = vgmInfo.getProperties().get("version");
                if (versionProp instanceof Schema) {
                    Schema versionSchema = (Schema) versionProp;
                    versionSchema.setType("integer");
                    versionSchema.setFormat("int32");
                }
            }
            vgmInfo.setRequired(Arrays.asList("authorizedPerson", "obtainedAt", "method", "containers"));
        }

        Schema address = components.getSchemas().get("Address");
        if (address != null && address.getProperties() != null) {
            Object countryProp = address.getProperties().get("countryCode");
            if (countryProp instanceof Schema) {
                ((Schema) countryProp).$ref("#/components/schemas/CountryCode");
            }
        }

        components.getSchemas().remove("VGMCreateRequest");
        components.getSchemas().remove("ContainerVGMRequest");
        components.getSchemas().remove("AddressRequest");
    }

    private void removeUnusedComponents(Components components) {
        // Remove V1 schemas not used by V2 document APIs
        if (components.getSchemas() != null) {
            components.getSchemas().remove("ErrorResponse");
            components.getSchemas().remove("ErrorDetail");
            // V1-only: list item for commodities[].dangerousGoods on POST/PUT /api/v1/booking/*.
            // Booking V2 uses components/schemas/DangerousGoodsInfo on BookingContainerCommodity only.
            components.getSchemas().remove("DangerousGoodsDTO");
        }

        // Remove V1 response models registered by SwaggerConfig
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

    @SuppressWarnings("rawtypes")
    private void registerResponses(Components components) {
        components.addResponses("BadRequest", createErrorResponse(
            "The server could not understand the request due to invalid syntax or parameters.",
            buildErrorExample("File type not allowed: exe.", "Bad Request", 400,
                "/api/v2/bookings/BKON123/documents", "BAD_REQUEST")));

        components.addResponses("Unauthorized", createErrorResponse(
            "Authentication failed (Missing or invalid Bearer token).",
            buildErrorExample("Missing or invalid Bearer token.", "Unauthorized", 401,
                "/api/v2/bookings/BKON123/documents", "UNAUTHORIZED")));

        components.addResponses("Forbidden", createErrorResponse(
            "The client does not have access rights to the content.",
            buildErrorExample("You do not have permission to access this resource.", "Forbidden", 403,
                "/api/v2/bookings/BKON123/documents", "FORBIDDEN")));

        components.addResponses("NotFound", createErrorResponse(
            "The resource could not be found.",
            buildErrorExample("The requested document was not found for the given booking.", "Not Found", 404,
                "/api/v2/bookings/BKON123/documents/invoice.pdf", "NOT_FOUND")));

        components.addResponses("Conflict", createErrorResponse(
            "Conflict with the current system state.",
            buildErrorExample("A file named 'invoice.pdf' already exists for this booking.", "Conflict", 409,
                "/api/v2/bookings/BKON123/documents", "CONFLICT")));

        components.addResponses("InternalServerError", createErrorResponse(
            "An unexpected server-side error occurred.",
            buildErrorExample("An unexpected error occurred while processing the request.", "Internal Server Error", 500,
                "/api/v2/bookings/BKON123/documents", "INTERNAL_SERVER_ERROR")));

        components.addResponses("ServiceUnavailable", createErrorResponse(
            "The service is currently not available.",
            buildErrorExample("The service is currently not available. Please try again later.", "Service Unavailable", 503,
                "/api/v2/bookings/BKON123/documents", "SERVICE_UNAVAILABLE")));

        // ListBookingDocumentsResponse
        components.addResponses("ListBookingDocumentsResponse", new ApiResponse()
            .description("A list of document metadata.")
            .content(new Content().addMediaType("application/json",
                new MediaType().schema(new Schema().$ref("#/components/schemas/DocumentList")))));
    }

    private Map<String, Object> buildErrorExample(String detail, String title, int status,
                                                   String instance, String code) {
        Map<String, Object> example = new LinkedHashMap<>();
        example.put("detail", detail);
        example.put("title", title);
        example.put("status", status);
        example.put("instance", instance);
        example.put("code", code);
        return example;
    }

    @SuppressWarnings("rawtypes")
    private ApiResponse createErrorResponse(String description, Map<String, Object> example) {
        MediaType mediaType = new MediaType()
            .schema(new Schema().$ref("#/components/schemas/ErrorMessage"));
        mediaType.setExample(example);
        return new ApiResponse()
            .description(description)
            .content(new Content().addMediaType("application/json", mediaType));
    }

    // ======================== Path Transformation ========================

    private void transformOperations(OpenAPI openApi) {
        if (openApi.getPaths() == null) return;

        openApi.getPaths().forEach((path, pathItem) -> {
            pathItem.readOperations().forEach(operation -> {
                replaceParametersWithRefs(operation);

                if (BOOKING_CRUD_OPERATION_IDS.contains(operation.getOperationId())) {
                    replaceBookingCrudErrorsInline(operation, operation.getOperationId(), path);
                } else if (VGM_OPERATION_IDS.contains(operation.getOperationId())) {
                    transformVgmOperation(operation, path);
                } else {
                    replaceErrorResponsesWithRefs(operation);
                }

                stripNoBodyResponses(operation);

                if ("listBookingDocuments".equals(operation.getOperationId())) {
                    operation.getResponses().addApiResponse("200",
                        new ApiResponse().$ref("#/components/responses/ListBookingDocumentsResponse"));
                }

                if ("uploadBookingDocuments".equals(operation.getOperationId())) {
                    replaceUploadRequestBody(operation);
                }
            });
        });
    }

    /**
     * Inline error responses for booking CRUD operations (V2 booking, client error architecture):
     * endpoint-specific instance and booking-specific detail messages (no type in examples).
     */
    private void replaceBookingCrudErrorsInline(Operation operation, String operationId, String path) {
        if (operation.getResponses() == null) return;

        String instance = instanceForBookingCrudOperation(operationId, path);
        boolean isById = instance.contains("BKON123");

        if (operation.getResponses().containsKey("400")) {
            operation.getResponses().addApiResponse("400", buildBadRequestForBookings(instance));
        }
        if (operation.getResponses().containsKey("401")) {
            operation.getResponses().addApiResponse("401", buildInlineErrorWithType(
                "Authentication failed (Missing or invalid Bearer token).",
                "Missing or invalid Bearer token. Required for booking operations.",
                "Unauthorized", 401, instance, "UNAUTHORIZED",
                "Missing or invalid Bearer token.", "#/headers/Authorization", "UNAUTHORIZED"));
        }
        if (operation.getResponses().containsKey("403")) {
            operation.getResponses().addApiResponse("403", buildInlineErrorWithType(
                "The client does not have access rights to the content.",
                isById ? "You do not have permission to access this booking." : "You do not have permission to access bookings.",
                "Forbidden", 403, instance, "FORBIDDEN",
                isById ? "You do not have permission to access this booking." : "Access denied to bookings.",
                "#/bookingId", "FORBIDDEN"));
        }
        if (operation.getResponses().containsKey("404")) {
            String notFoundDetail = isById
                ? "Booking not found: BKON123"
                : "The requested booking resource could not be found.";
            operation.getResponses().addApiResponse("404", buildInlineErrorWithType(
                "The resource could not be found.",
                notFoundDetail,
                "Not Found", 404, instance, "NOT_FOUND",
                notFoundDetail, "#/bookingId", "NOT_FOUND"));
        }
        if (operation.getResponses().containsKey("409")) {
            operation.getResponses().addApiResponse("409", buildInlineErrorWithType(
                "Conflict with the current system state.",
                "The booking request could not be completed due to a conflict with the current state.",
                "Conflict", 409, instance, "CONFLICT",
                "The booking request could not be completed due to a conflict with the current state.",
                "#/request", "CONFLICT"));
        }
        if (operation.getResponses().containsKey("500")) {
            operation.getResponses().addApiResponse("500", buildInlineErrorWithType(
                "An unexpected server-side error occurred.",
                "An unexpected error occurred while processing the booking request.",
                "Internal Server Error", 500, instance, "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred while processing the booking request.",
                "#/request", "INTERNAL_SERVER_ERROR"));
        }
        if (operation.getResponses().containsKey("503")) {
            operation.getResponses().addApiResponse("503", buildInlineErrorWithType(
                "The service is currently not available.",
                "The booking service is currently not available. Please try again later.",
                "Service Unavailable", 503, instance, "SERVICE_UNAVAILABLE",
                "The booking service is currently not available. Please try again later.",
                "#/request", "SERVICE_UNAVAILABLE"));
        }
    }

    private String instanceForBookingCrudOperation(String operationId, String path) {
        if ("getBookings".equals(operationId) || "createBooking".equals(operationId)) {
            return "/api/v2/bookings";
        }
        if ("getBookingById".equals(operationId) || "updateBookingById".equals(operationId)) {
            return "/api/v2/bookings/BKON123";
        }
        return path;
    }

    @SuppressWarnings("rawtypes")
    private ApiResponse buildInlineErrorWithType(String description, String detail, String title,
                                                 int status, String instance, String code,
                                                 String nestedDetail, String nestedPointer, String nestedCode) {
        Map<String, Object> example = new LinkedHashMap<>();
        example.put("detail", detail);
        example.put("title", title);
        example.put("status", status);
        example.put("instance", instance);
        example.put("code", code);
        List<Map<String, Object>> errorsList = new ArrayList<>();
        Map<String, Object> nested = new LinkedHashMap<>();
        nested.put("detail", nestedDetail);
        nested.put("pointer", nestedPointer);
        nested.put("code", nestedCode);
        errorsList.add(nested);
        example.put("errors", errorsList);
        MediaType mediaType = new MediaType()
            .schema(new Schema().$ref("#/components/schemas/ErrorMessage"));
        mediaType.setExample(example);
        return new ApiResponse()
            .description(description)
            .content(new Content().addMediaType("application/json", mediaType));
    }

    @SuppressWarnings("rawtypes")
    private ApiResponse buildBadRequestForBookings(String instance) {
        List<Map<String, Object>> nestedErrors = new ArrayList<>();
        Map<String, Object> nested = new LinkedHashMap<>();
        nested.put("detail", "The origin port (originId) is required for the booking.");
        nested.put("pointer", "#/originId");
        nested.put("code", "REQUIRED_FIELD");
        nestedErrors.add(nested);

        Map<String, Object> example = new LinkedHashMap<>();
        example.put("detail", "Booking data validation failed. Please review the provided data.");
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

    private void transformVgmOperation(Operation operation, String path) {
        replaceErrorResponsesWithRefs(operation);

        if ("/api/v2/bookings/{bookingId}/vgm".equals(path)) {
            List<Parameter> pathLevel = new ArrayList<>();
            pathLevel.add(new Parameter().$ref("#/components/parameters/bookingId"));
            operation.setParameters(pathLevel);
        }

        if ("getVgm".equals(operation.getOperationId())) {
            Content getContent = new Content();
            getContent.addMediaType("application/json", new MediaType()
                .schema(new Schema().$ref("#/components/schemas/VGMInfo")));
            operation.getResponses().addApiResponse("200", new ApiResponse()
                .description("Response object containing a VGM info.")
                .content(getContent));
        }

        if ("upsertVgm".equals(operation.getOperationId())) {
            if (operation.getRequestBody() != null) {
                Content requestContent = new Content();
                requestContent.addMediaType("application/json", new MediaType()
                    .schema(new Schema().$ref("#/components/schemas/VGMInfo")));
                operation.getRequestBody().setContent(requestContent);
                operation.getRequestBody().setRequired(true);
                operation.getRequestBody().setDescription("Payload to create/replace vgm information");
            }

            operation.getResponses().remove("204");

            Map<String, Object> updateExample = new LinkedHashMap<>();
            updateExample.put("BookNo", "TSHGCGP26008949");
            updateExample.put("message", "VGM updated successfully");

            MediaType updateMediaType = new MediaType()
                .schema(new Schema().$ref("#/components/schemas/VgmUpdateResponse"));
            updateMediaType.setExample(updateExample);

            Content updateContent = new Content();
            updateContent.addMediaType("application/json", updateMediaType);
            operation.getResponses().addApiResponse("200", new ApiResponse()
                .description("VGM saved successfully")
                .content(updateContent));
        }
    }

    private void replaceParametersWithRefs(Operation operation) {
        if (operation.getParameters() == null) return;

        List<Parameter> replaced = new ArrayList<>();
        for (Parameter param : operation.getParameters()) {
            if ("bookingId".equals(param.getName())) {
                replaced.add(new Parameter().$ref("#/components/parameters/bookingId"));
            } else if ("fileName".equals(param.getName())) {
                replaced.add(new Parameter().$ref("#/components/parameters/fileName"));
            } else {
                replaced.add(param);
            }
        }
        operation.setParameters(replaced);
    }

    private void replaceErrorResponsesWithRefs(Operation operation) {
        if (operation.getResponses() == null) return;

        ERROR_RESPONSE_REFS.forEach((statusCode, refName) -> {
            if (operation.getResponses().containsKey(statusCode)) {
                operation.getResponses().addApiResponse(statusCode,
                    new ApiResponse().$ref("#/components/responses/" + refName));
            }
        });
    }

    private void stripNoBodyResponses(Operation operation) {
        if (operation.getResponses() == null) return;

        ApiResponse r201 = operation.getResponses().get("201");
        if (r201 != null) {
            r201.setContent(null);
        }

        ApiResponse r204 = operation.getResponses().get("204");
        if (r204 != null) {
            r204.setContent(null);
        }
    }

    @SuppressWarnings("rawtypes")
    private void replaceUploadRequestBody(Operation operation) {
        if (operation.getRequestBody() == null) return;

        Content content = new Content();
        content.addMediaType("multipart/form-data", new MediaType()
            .schema(new Schema().$ref("#/components/schemas/DocumentUploadRequest")));
        operation.getRequestBody().setContent(content);
        operation.getRequestBody().setRequired(true);
    }
}
