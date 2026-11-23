package com.svm.ecom.rates.controller;

import com.svm.ecom.docs.SwaggerDocs;
import com.svm.ecom.dto.ErrorMessage;
import com.svm.ecom.dto.NestedErrorMessage;
import com.svm.ecom.rates.dto.estimate.RateEstimateRequest;
import com.svm.ecom.rates.dto.estimate.RateEstimateResponse;
import com.svm.ecom.rates.security.CustomerContext;
import com.svm.ecom.rates.service.RateEstimateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;

/**
 * Auto Rate estimate API. Independent of existing contract endpoints.
 */
@RestController
@RequestMapping("/api/v1/rates")
@Validated
@Tag(name = SwaggerDocs.Rates.TAG_NAME, description = SwaggerDocs.Rates.TAG_DESCRIPTION)
@SecurityRequirement(name = "bearerAuth")
public class RateEstimateController {

    private static final Logger logger = LoggerFactory.getLogger(RateEstimateController.class);
    private static final String INSTANCE = "/api/v1/rates/estimate";

    private final RateEstimateService rateEstimateService;
    private final CustomerContext customerContext;

    public RateEstimateController(RateEstimateService rateEstimateService,
                                  CustomerContext customerContext) {
        this.rateEstimateService = rateEstimateService;
        this.customerContext = customerContext;
    }

    @PostMapping(path = "/estimate", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = SwaggerDocs.Rates.SUMMARY_ESTIMATE,
            description = SwaggerDocs.Rates.DESC_ESTIMATE,
            operationId = SwaggerDocs.Rates.OPERATION_ID_ESTIMATE,
            responses = {
                    @ApiResponse(responseCode = "200", description = SwaggerDocs.Rates.RESPONSE_200_ESTIMATE,
                            content = @Content(schema = @Schema(implementation = RateEstimateResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Validation failed",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    public ResponseEntity<?> estimate(@Valid @RequestBody RateEstimateRequest request,
                                      HttpServletRequest httpRequest) {
        try {
            String authorizationHeader = httpRequest.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(buildError("Authentication failed (Missing or invalid Bearer token).",
                                "Unauthorized", 401, INSTANCE, "UNAUTHORIZED"));
            }

            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            logger.info("Auto Rate estimate request from customerCode={}, agrPartyCode={}, routeId={}",
                    customerCode, request.getAgrPartyCode(), request.getRouteId());

            RateEstimateResponse response = rateEstimateService.estimate(request);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            logger.warn("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(buildError("Authentication failed (Missing or invalid Bearer token).",
                            "Unauthorized", 401, INSTANCE, "UNAUTHORIZED"));
        } catch (IllegalArgumentException e) {
            logger.warn("Auto Rate estimate validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildValidationError(INSTANCE, e.getMessage(), "request", "VALIDATION_FAILED"));
        } catch (Exception e) {
            logger.error("Error estimating rates: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildError("An unexpected server-side error occurred.",
                            "Internal Server Error", 500, INSTANCE, "INTERNAL_ERROR"));
        }
    }

    private String extractJwtToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private ErrorMessage buildError(String detail, String title, int status, String instance, String code) {
        ErrorMessage error = new ErrorMessage();
        error.setDetail(detail);
        error.setTitle(title);
        error.setStatus(status);
        error.setInstance(instance);
        error.setCode(code);
        return error;
    }

    private ErrorMessage buildValidationError(String instance, String detail, String pointer, String code) {
        ErrorMessage error = buildError(detail, "Validation Failed", 400, instance, "VALIDATION_FAILED");
        NestedErrorMessage nested = NestedErrorMessage.of(detail, "#/" + pointer, code);
        error.setErrors(Collections.singletonList(nested));
        return error;
    }
}
