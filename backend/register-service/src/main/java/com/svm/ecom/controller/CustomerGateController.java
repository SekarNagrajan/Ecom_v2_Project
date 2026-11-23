package com.svm.ecom.controller;

import com.svm.ecom.dto.CustomerGateDto;
import com.svm.ecom.dto.ErrorResponse;
import com.svm.ecom.docs.CustomerGateSwaggerDocs;
import com.svm.ecom.docs.SwaggerDocs;
import com.svm.ecom.enums.GateStatus;
import com.svm.ecom.error.ErrorCodes;
import com.svm.ecom.error.ErrorReason;
import com.svm.ecom.exception.CustomerGateRateLimitException;
import com.svm.ecom.security.JwtUtil;
import com.svm.ecom.service.CustomerGateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1")
@Tag(name = "Customer Gate", description = "ECOM API - Customer Gate: read-only registration/gate status for e-commerce users")
@SecurityRequirement(name = "bearerAuth")
public class CustomerGateController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerGateController.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    private final CustomerGateService customerGateService;
    private final JwtUtil jwtUtil;

    public CustomerGateController(CustomerGateService customerGateService, JwtUtil jwtUtil) {
        this.customerGateService = customerGateService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
        summary = "List Customer Gate records",
        description = "Returns all e-commerce customer gate records as a JSON array (read-only, no pagination). "
        + "Optional filters: gateStatus (N, E, I, P, M, R, Y) and updatedSince (ISO-8601 UTC, e.g. 2026-01-01T00:00:00Z) on the action date; returns 200 [] when nothing matches. "
        + "Ordered by action date ascending, then customer code. Not scoped by agency; covers all customers.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = CustomerGateDto.class)),
                    examples = @ExampleObject(name = "Empty", value = "[]")
                )
            ),
            @ApiResponse(responseCode = "400",
                description = SwaggerDocs.Common.BAD_REQUEST_400
                    + ". Allowed gateStatus values are N, E, I, P, M, R, Y (bad value -> 400) (IAD \u00a73.3).",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "ValidationError", value = CustomerGateSwaggerDocs.ERROR_VALIDATION))),
            @ApiResponse(responseCode = "401", description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Unauthorized", value = CustomerGateSwaggerDocs.ERROR_UNAUTHORIZED))),
            @ApiResponse(responseCode = "403", description = SwaggerDocs.Common.FORBIDDEN_403,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Forbidden", value = CustomerGateSwaggerDocs.ERROR_FORBIDDEN))),
            // NOTE (docs only): this endpoint is rate limited (100/min per Client ID, enforced by
            // CustomerGateService) and still answers 429 / ErrorCodes.RATE_LIMITED / ErrorReason.RATE_LIMITED
            // at runtime. The 429 response is intentionally NOT documented here to keep the spec aligned with
            // the Booking spec (Booking has no 429 contract). Runtime behaviour is unchanged.
            @ApiResponse(responseCode = "500", description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "InternalError", value = CustomerGateSwaggerDocs.ERROR_INTERNAL))),
            @ApiResponse(responseCode = "503", description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "ServiceUnavailable", value = CustomerGateSwaggerDocs.ERROR_SERVICE_UNAVAILABLE)))
        }
    )
    @GetMapping(path = "/ecomCustomerGate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listCustomerGate(
            HttpServletRequest request,
            @Parameter(
                description = "Filter by derived gate status. One of N (New Customer), E (Expired User), "
                    + "I (Pending with Customer), P (Pending with Agency), M (Pending with Line), R (Rejected), "
                    + "Y (Accepted). Any other value returns 400.",
                example = "Y",
                schema = @Schema(implementation = GateStatus.class))
            @RequestParam(name = "gateStatus", required = false) String gateStatus,
            @Parameter(
                description = "Exclusive lower bound on the last-updated timestamp (ISO-8601 date-time, UTC).",
                example = "2026-01-01T00:00:00Z",
                schema = @Schema(type = "string", format = "date-time", example = "2026-01-01T00:00:00Z"))
            @RequestParam(name = "updatedSince", required = false) String updatedSince) {

        String correlationId = resolveCorrelationId(request);

        try {
            String token = extractJwtToken(request.getHeader("Authorization"));
            if (token == null) {
                return unauthorized("Missing or invalid JWT token", correlationId);
            }

            String clientId;
            try {
                clientId = jwtUtil.validateTokenWithSession(token);
            } catch (RuntimeException e) {
                return unauthorized("Invalid JWT token or authentication failed", correlationId);
            }

            // Global (internal-auth) token only: ECOM customer tokens (uid/customerCode claims) are rejected,
            // and the caller must hold the SYSUSER role.
            if (!jwtUtil.isSysUserReadOnlyToken(token)) {
                logger.warn("ecomCustomerGate denied: token is not a SYSUSER global token [clientId={}, correlationId={}]",
                        clientId, correlationId);
                return forbidden("SYSUSER role required ", correlationId);
            }

            List<CustomerGateDto> data = customerGateService.list(clientId, gateStatus, updatedSince);

            return ResponseEntity.ok()
                .header(CORRELATION_ID_HEADER, correlationId)
                .body(data);

        } catch (IllegalArgumentException e) {
            // Invalid gateStatus letter (not one of N,E,I,P,M,R,Y)
            return badRequest("gateStatus", gateStatus, e.getMessage(), correlationId);
        } catch (DateTimeParseException e) {
            return badRequest("updatedSince", updatedSince,
                "updatedSince must be ISO-8601 date-time, e.g. 2026-01-01T00:00:00Z", correlationId);
        } catch (CustomerGateRateLimitException e) {
            return tooManyRequests(e.getMessage(), e.getRetryAfterSeconds(), correlationId);
        } catch (IllegalStateException e) {
            logger.error("Service unavailable during ecomCustomerGate list [correlationId={}]", correlationId, e);
            return serviceUnavailable(correlationId);
        } catch (Exception e) {
            logger.error("Unexpected error during ecomCustomerGate list [correlationId={}]", correlationId, e);
            return internalError(correlationId);
        }
    }

    // ---- Correlation ID (scoped to this endpoint only; no shared filter exists in this codebase) ----
    private String resolveCorrelationId(HttpServletRequest request) {
        String incoming = request.getHeader(CORRELATION_ID_HEADER);
        return (incoming != null && !incoming.trim().isEmpty()) ? incoming.trim() : UUID.randomUUID().toString();
    }

    // ---- JWT helper (same pattern as BookingController#extractJwtToken) ----
    private String extractJwtToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    // ---- Error helpers (reuse existing ErrorResponse/ErrorCodes/ErrorReason standard) ----
    private ResponseEntity<ErrorResponse> unauthorized(String message, String correlationId) {
        ErrorResponse body = new ErrorResponse(401, "Unauthorized", LocalDateTime.now());
        body.addError(ErrorCodes.AUTH_INVALID_JWT, null, null, ErrorReason.UNAUTHORIZED, message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(CORRELATION_ID_HEADER, correlationId).body(body);
    }

    private ResponseEntity<ErrorResponse> forbidden(String message, String correlationId) {
        ErrorResponse body = new ErrorResponse(403, "Forbidden", LocalDateTime.now());
        body.addError(ErrorCodes.FORBIDDEN, null, null, ErrorReason.FORBIDDEN, message);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).header(CORRELATION_ID_HEADER, correlationId).body(body);
    }

    private ResponseEntity<ErrorResponse> badRequest(String field, String value, String message, String correlationId) {
        ErrorResponse body = new ErrorResponse(400, "Bad Request", LocalDateTime.now());
        body.addError(ErrorCodes.VALIDATION_INVALID_FORMAT, field, value, ErrorReason.VALIDATION_ERROR, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(CORRELATION_ID_HEADER, correlationId).body(body);
    }

    private ResponseEntity<ErrorResponse> tooManyRequests(String message, long retryAfterSeconds, String correlationId) {
        ErrorResponse body = new ErrorResponse(429, "Too Many Requests", LocalDateTime.now());
        body.addError(ErrorCodes.RATE_LIMITED, null, null, ErrorReason.RATE_LIMITED, message);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .header(CORRELATION_ID_HEADER, correlationId)
            .header(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds))
            .body(body);
    }

    private ResponseEntity<ErrorResponse> serviceUnavailable(String correlationId) {
        ErrorResponse body = new ErrorResponse(503, "Service Unavailable", LocalDateTime.now());
        body.addError(ErrorCodes.SERVICE_UNAVAILABLE, null, null, ErrorReason.SERVICE_UNAVAILABLE,
            "ECOM database is currently unavailable");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header(CORRELATION_ID_HEADER, correlationId).body(body);
    }

    private ResponseEntity<ErrorResponse> internalError(String correlationId) {
        ErrorResponse body = new ErrorResponse(500, "Internal Server Error", LocalDateTime.now());
        body.addError(ErrorCodes.INTERNAL_ERROR, null, null, ErrorReason.INTERNAL_ERROR,
            "An unexpected error occurred while processing the request");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header(CORRELATION_ID_HEADER, correlationId).body(body);
    }
}
