package com.svm.ecom.rates.controller;

import com.svm.ecom.dto.ErrorMessage;
import com.svm.ecom.dto.NestedErrorMessage;
import com.svm.ecom.rates.cursor.MrgCursorCodec.InvalidCursorException;
import com.svm.ecom.rates.dto.mrg.MrgListResponse;
import com.svm.ecom.rates.dto.mrg.MrgNoFormat;
import com.svm.ecom.rates.service.MrgService;
import com.svm.ecom.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * MRG (Minimum Rate Guideline) data extraction (IAD TSHG-2935). Read-only, SYSUSER only.
 */
@RestController
@RequestMapping("/api/v1/rates")
@Tag(name = "Rates", description = "Operations related to rate and surcharge estimation.")
@SecurityRequirement(name = "bearerAuth")
public class MrgController {

    private static final Logger logger = LoggerFactory.getLogger(MrgController.class);
    private static final String INSTANCE = "/api/v1/rates/mrg";
    // mrgNo is accepted (not rejected as unknown) but ignored — callers still get the paged list.
    private static final Set<String> ALLOWED_QUERY_PARAMS =
            Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList("limit", "cursor", "mrgNo")));
    private static final String ALLOWED_PARAMS_TEXT = "limit, cursor";

    private final MrgService mrgService;
    private final JwtUtil jwtUtil;

    public MrgController(MrgService mrgService, JwtUtil jwtUtil) {
        this.mrgService = mrgService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(path = "/mrg", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "List MRG items",
            description = "Returns a paged list of MRG (Minimum Rate Guideline) items, each with its header fields and "
                    + "all rate detail lines. Cursor-based pagination only. "
                    + "Only users with the SYSUSER role are authorized.",
            operationId = "getMrgList",
            parameters = {
                    @Parameter(name = "limit", in = ParameterIn.QUERY, required = false,
                            description = " Number of records to return (1–100, default 20).",
                            schema = @Schema(type = "integer", defaultValue = "20", example = "40")),
                    @Parameter(name = "cursor", in = ParameterIn.QUERY, required = false,
                            description = " Cursor from the previous response (metadata.nextCursor) to fetch the next page. Omit for the first page. ",
                            schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Paged list of MRG items.",
                            content = @Content(schema = @Schema(implementation = MrgListResponse.class),
                                    examples = {
                                            @ExampleObject(name = "pagedList",
                                                    summary = "Paged list",
                                                    value = "{\"items\":[{\"id\":\"M25070800001\",\"description\":\"\",\"validFrom\":\"2025-07-08T00:00:00Z\",\"validTo\":\"2025-12-31T00:00:00Z\",\"currency\":\"USD\",\"details\":[]}],\"metadata\":{\"nextCursor\":\"abcdef123\",\"hasNext\":true,\"limit\":20}}")
                                    })),
                    @ApiResponse(responseCode = "400", description = "Invalid or unknown query parameter",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class),
                                    examples = {
                                            @ExampleObject(name = "unknownParameter",
                                                    summary = "Unknown query parameter",
                                                    value = "{\"detail\":\"Unknown parameter: mr4543yteytyurutugNo. Allowed: limit, cursor\",\"title\":\"Validation Failed\",\"status\":400,\"instance\":\"/api/v1/rates/mrg\",\"code\":\"VALIDATION_FAILED\",\"errors\":[{\"detail\":\"Unknown parameter: mr4543yteytyurutugNo. Allowed: limit, cursor\",\"parameter\":\"mr4543yteytyurutugNo\",\"code\":\"UNKNOWN_PARAMETER\"}]}")
                                    })),
                    @ApiResponse(responseCode = "401", description = "Missing or invalid Bearer token",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User does not have the SYSUSER role",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "503", description = "Service temporarily unavailable",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    public ResponseEntity<?> listMrg(HttpServletRequest request,
                                     @RequestParam(name = "limit", required = false) String limitParam,
                                     @RequestParam(name = "cursor", required = false) String cursor) {
        try {
            String token = extractJwtToken(request.getHeader("Authorization"));
            if (token == null) {
                return unauthorized();
            }
            jwtUtil.validateTokenWithSession(token);
            if (!jwtUtil.isSysUserReadOnlyToken(token)) {
                return error(HttpStatus.FORBIDDEN, "Forbidden", "FORBIDDEN",
                        "Access denied. The SYSUSER role is required.", null, null);
            }

            ResponseEntity<ErrorMessage> unknown = rejectUnknownParameters(request);
            if (unknown != null) {
                return unknown;
            }

            int limit = parseLimit(limitParam);
            return ResponseEntity.ok(mrgService.list(limit, cursor));

        } catch (InvalidLimitException e) {
            return validation("limit", e.getMessage(), "INVALID_LIMIT");
        } catch (InvalidCursorException e) {
            return validation("cursor", "Invalid or malformed cursor.", "INVALID_CURSOR");
        } catch (QueryTimeoutException | DataAccessResourceFailureException e) {
            logger.error("MRG list: database unavailable: {}", e.getMessage());
            return error(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", "SERVICE_UNAVAILABLE",
                    "The service is temporarily unavailable. Please retry later.", null, null);
        } catch (SecurityException e) {
            logger.warn("MRG list: authentication failed: {}", e.getMessage());
            return unauthorized();
        } catch (RuntimeException e) {
            if (isJwtValidationFailure(e)) {
                logger.warn("MRG list: JWT validation failed: {}", e.getMessage());
                return unauthorized();
            }
            logger.error("MRG list: unexpected error", e);
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "INTERNAL_ERROR",
                    "An unexpected server-side error occurred.", null, null);
        }
    }

    private static ResponseEntity<ErrorMessage> rejectUnknownParameters(HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String name : parameterMap.keySet()) {
            if (!ALLOWED_QUERY_PARAMS.contains(name)) {
                String echo = MrgNoFormat.safeEcho(name);
                String detail = "Unknown parameter: " + echo + ". Allowed: " + ALLOWED_PARAMS_TEXT;
                return validation(echo, detail, "UNKNOWN_PARAMETER");
            }
        }
        return null;
    }

    private int parseLimit(String limitParam) {
        if (limitParam == null || limitParam.trim().isEmpty()) {
            return mrgService.getDefaultLimit();
        }
        int limit;
        try {
            limit = Integer.parseInt(limitParam.trim());
        } catch (NumberFormatException e) {
            throw new InvalidLimitException("limit must be an integer between 1 and " + mrgService.getMaxLimit() + ".");
        }
        if (limit < 1 || limit > mrgService.getMaxLimit()) {
            throw new InvalidLimitException("limit must be an integer between 1 and " + mrgService.getMaxLimit() + ".");
        }
        return limit;
    }

    private static boolean isJwtValidationFailure(RuntimeException e) {
        String msg = e.getMessage();
        if (msg == null) {
            return false;
        }
        String lower = msg.toLowerCase();
        return lower.contains("token") || lower.contains("jwt") || lower.contains("expired")
                || lower.contains("signature") || lower.contains("session");
    }

    private static String extractJwtToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private static ResponseEntity<ErrorMessage> unauthorized() {
        return error(HttpStatus.UNAUTHORIZED, "Unauthorized", "UNAUTHORIZED",
                "Authentication failed (Missing or invalid Bearer token).", null, null);
    }

    private static ResponseEntity<ErrorMessage> validation(String parameter, String detail, String nestedCode) {
        ErrorMessage body = new ErrorMessage(detail,
                "Validation Failed", 400, null, INSTANCE, "VALIDATION_FAILED");
        NestedErrorMessage nested = new NestedErrorMessage(detail, null, parameter, null, nestedCode);
        body.setErrors(Collections.singletonList(nested));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private static ResponseEntity<ErrorMessage> error(HttpStatus status, String title, String code, String detail,
                                                      String parameter, String nestedCode) {
        ErrorMessage body = new ErrorMessage(detail, title, status.value(), null, INSTANCE, code);
        NestedErrorMessage nested = new NestedErrorMessage(detail, null, parameter, null, nestedCode != null ? nestedCode : code);
        body.setErrors(Collections.singletonList(nested));
        return ResponseEntity.status(status).body(body);
    }

    private static final class InvalidLimitException extends IllegalArgumentException {
        InvalidLimitException(String message) {
            super(message);
        }
    }
}
