package com.svm.ecom.docs;

import com.svm.ecom.error.ErrorCodes;

/**
 * OpenAPI example payloads for {@code GET /api/v1/ecomCustomerGate} (documentation only).
 *
 * <p>Every example follows the shared {@code ErrorResponse} contract
 * ({@code statusCode, statusCodeText, errorDateTime, errors[]{errorCode, field, value, reason, message}}):</p>
 * <ul>
 *   <li>{@code errorCode} is built from the {@link ErrorCodes} constants (never a hard-coded number),
 *       and is the code the controller actually emits for that status.</li>
 *   <li>{@code reason} is the serialized value of an {@code ErrorReason} enum constant. Annotation
 *       values must be compile-time constants, so the literal is spelled out here and guarded by
 *       {@code CustomerGateSwaggerDocsTest}, which fails if a literal is not a real
 *       {@code ErrorReason} value.</li>
 *   <li>{@code message} mirrors the text the controller returns.</li>
 * </ul>
 */
public final class CustomerGateSwaggerDocs {

    private CustomerGateSwaggerDocs() {}

    private static final String TS = SwaggerDocs.ExampleValues.EX_DATETIME_TZ;

    /** 400: gateStatus=A is not one of N, E, I, P, M, R, Y (GateStatus.fromValue message). */
    public static final String ERROR_VALIDATION =
        "{\n" +
        "  \"statusCode\": 400,\n" +
        "  \"statusCodeText\": \"Bad Request\",\n" +
        "  \"errorDateTime\": \"" + TS + "\",\n" +
        "  \"errors\": [\n" +
        "    {\"errorCode\":" + ErrorCodes.VALIDATION_INVALID_FORMAT + ",\"field\":\"gateStatus\",\"value\":\"A\","
            + "\"reason\":\"VALIDATION_ERROR\","
            + "\"message\":\"Invalid gateStatus value: A. Valid values are: N, E, I, P, M, R, Y\"}\n" +
        "  ]\n" +
        "}";

    /** 401: missing/invalid JWT (controller emits AUTH_INVALID_JWT for both cases). */
    public static final String ERROR_UNAUTHORIZED =
        "{\n" +
        "  \"statusCode\": 401,\n" +
        "  \"statusCodeText\": \"Unauthorized\",\n" +
        "  \"errorDateTime\": \"" + TS + "\",\n" +
        "  \"errors\": [\n" +
        "    {\"errorCode\":" + ErrorCodes.AUTH_INVALID_JWT + ",\"reason\":\"UNAUTHORIZED\","
            + "\"message\":\"Missing or invalid JWT token\"}\n" +
        "  ]\n" +
        "}";

    /** 403: insufficient permissions. */
    public static final String ERROR_FORBIDDEN =
        "{\n" +
        "  \"statusCode\": 403,\n" +
        "  \"statusCodeText\": \"Forbidden\",\n" +
        "  \"errorDateTime\": \"" + TS + "\",\n" +
        "  \"errors\": [\n" +
        "    {\"errorCode\":" + ErrorCodes.FORBIDDEN + ",\"reason\":\"FORBIDDEN\","
            + "\"message\":\"You do not have permission to access this resource\"}\n" +
        "  ]\n" +
        "}";

    /** 500: unexpected error. */
    public static final String ERROR_INTERNAL =
        "{\n" +
        "  \"statusCode\": 500,\n" +
        "  \"statusCodeText\": \"Internal Server Error\",\n" +
        "  \"errorDateTime\": \"" + TS + "\",\n" +
        "  \"errors\": [\n" +
        "    {\"errorCode\":" + ErrorCodes.INTERNAL_ERROR + ",\"reason\":\"INTERNAL_ERROR\","
            + "\"message\":\"An unexpected error occurred while processing the request\"}\n" +
        "  ]\n" +
        "}";

    /** 503: ECOM database not available. */
    public static final String ERROR_SERVICE_UNAVAILABLE =
        "{\n" +
        "  \"statusCode\": 503,\n" +
        "  \"statusCodeText\": \"Service Unavailable\",\n" +
        "  \"errorDateTime\": \"" + TS + "\",\n" +
        "  \"errors\": [\n" +
        "    {\"errorCode\":" + ErrorCodes.SERVICE_UNAVAILABLE + ",\"reason\":\"SERVICE_UNAVAILABLE\","
            + "\"message\":\"ECOM database is currently unavailable\"}\n" +
        "  ]\n" +
        "}";
}
