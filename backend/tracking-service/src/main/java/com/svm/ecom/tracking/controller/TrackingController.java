
package com.svm.ecom.tracking.controller;

import com.svm.ecom.tracking.model.TrackingRequest;
import com.svm.ecom.tracking.model.TrackingResponse;
import com.svm.ecom.tracking.service.TrackingService;
// Swagger/OpenAPI imports
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.*;

// Centralized documentation constants
import com.svm.ecom.docs.SwaggerDocs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.svm.ecom.dto.ErrorResponse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/trackingendpoint/v1")
@Tag(name = "Tracking API", description = "Container and shipment tracking operations with real-time data")
@Validated
public class TrackingController {

    private static final Logger logger = LoggerFactory.getLogger(TrackingController.class);

    @Autowired
    private TrackingService trackingService;

    // Note: PermissionService dependency removed as part of UserPermission system disabling

    @GetMapping("/tracking")
    @Operation(
        summary = SwaggerDocs.Tracking.SUMMARY_TRACK_CONTAINER,
        description = SwaggerDocs.Tracking.DESC_TRACK_CONTAINER
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = SwaggerDocs.Common.SUCCESS_200,
            content = @Content(
                schema = @Schema(implementation = TrackingResponse.class),
                examples = @ExampleObject(value = "{\"messageCode\":\"200\",\"timestamp\":\"2026-02-06T21:17:47Z\",\"processedBy\":\"alice\",\"strcontainer\":null,\"trackingData\":{\"result\":[{\"blNo\":\"TSHGCGP24000999\",\"bookNo\":\"TSHGCGP24000999\",\"originPortDesc\":\"CHATTOGRAM, BANGLADESH\",\"finalPortDesc\":\"CY OOSTERHOUT, NETHERLANDS\",\"podAt\":\"2024-03-01T07:00:00+01:00\",\"polAt\":\"2024-01-21T05:20:00+06:00\",\"dischargePortDesc\":\"NLMOE\",\"loadarrdate\":\"2024-01-20T07:18:00+06:00\",\"polterminal\":\"BDCGPCCT00\",\"polterminalname\":\"Chattogram Container Terminal\",\"podterminal\":\"NLMOECCT00\",\"podterminalname\":null,\"loadportid\":\"BDCGP\",\"movementList\":[{\"containerNo\":\"TAWU4142118\",\"containerActivity\":[{\"containerNo\":\"TAWU4142118\",\"movementDesc\":\"RECEIVED FROM CONSIGNEE\",\"portName\":\"CY OOSTERHOUT, NETHERLANDS\",\"vessel\":\"\",\"activityDate\":\"2024-03-18T15:37:00+01:00\",\"serviceCode\":\"DEX2\",\"voyage\":\"\",\"bound\":null,\"bkSalesAgent\":\"\",\"blSalesAgent\":\"\",\"eqpTypeId\":\"40HC\",\"activityloccode\":\"NLOOS\",\"nextpod\":\"NLMOE\"},{\"containerNo\":\"TAWU4142118\",\"movementDesc\":\"SENT TO CONSIGNEE\",\"portName\":\"CY OOSTERHOUT, NETHERLANDS\",\"vessel\":\"AND\",\"activityDate\":\"2024-03-18T08:50:00+01:00\",\"serviceCode\":\"DEX2\",\"voyage\":\"402\",\"bound\":\"N\",\"bkSalesAgent\":\"\",\"blSalesAgent\":\"\",\"eqpTypeId\":\"40HC\",\"activityloccode\":\"NLOOS\",\"nextpod\":\"NLMOE\"},{\"containerNo\":\"TAWU4142118\",\"movementDesc\":\"LOAD FULL\",\"portName\":\"CHATTOGRAM, BANGLADESH\",\"vessel\":\"PAN\",\"activityDate\":\"2024-01-21T09:05:00+06:00\",\"serviceCode\":\"TEX\",\"voyage\":\"402\",\"bound\":\"S\",\"bkSalesAgent\":\"\",\"blSalesAgent\":\"\",\"eqpTypeId\":\"40HC\",\"activityloccode\":\"BDCGP\",\"nextpod\":\"LKCMB\"},{\"containerNo\":\"TAWU4142118\",\"movementDesc\":\"RECEIVED FROM SHIPPER\",\"portName\":\"CHATTOGRAM, BANGLADESH\",\"vessel\":\"PAN\",\"activityDate\":\"2024-01-20T09:00:00+06:00\",\"serviceCode\":\"TEX\",\"voyage\":\"402\",\"bound\":\"S\",\"bkSalesAgent\":\"\",\"blSalesAgent\":\"\",\"eqpTypeId\":\"40HC\",\"activityloccode\":\"BDCGP\",\"nextpod\":\"NLMOE\"}]}],\"portacklist\":[{\"legType\":\"M\",\"pol\":\"CHATTOGRAM, BANGLADESH\",\"pod\":\"COLOMBO, SRI LANKA\",\"etd\":\"2024-01-21T05:20:00+06:00\",\"eta\":\"2024-01-26T15:42:00+05:30\",\"loadportid\":\"BDCGP\",\"dischargeportid\":\"LKCMB\"},{\"legType\":\"M\",\"pol\":\"COLOMBO, SRI LANKA\",\"pod\":\"BARCELONA, SPAIN\",\"etd\":\"2024-02-01T02:30:00+05:30\",\"eta\":\"2024-02-24T05:42:00+01:00\",\"loadportid\":\"LKCMB\",\"dischargeportid\":\"ESBCN\"},{\"legType\":\"M\",\"pol\":\"BARCELONA, SPAIN\",\"pod\":\"MOERDIJK, NETHERLANDS\",\"etd\":\"2024-03-01T15:42:00+01:00\",\"eta\":\"2024-03-08T02:48:00+01:00\",\"loadportid\":\"ESBCN\",\"dischargeportid\":\"NLMOE\"},{\"legType\":\"B\",\"pol\":\"MOERDIJK, NETHERLANDS\",\"pod\":\"CY OOSTERHOUT, NETHERLANDS\",\"etd\":\"2024-03-09T02:48:00+01:00\",\"eta\":\"2024-03-10T02:48:00+01:00\",\"loadportid\":\"NLMOE\",\"dischargeportid\":\"NLOOS\"}],\"originPortCode\":\"BDCGP\",\"deliveryPortCode\":\"NLOOS\"}]},\"message\":\"Success\"}")
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = SwaggerDocs.Common.BAD_REQUEST_400,
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "ValidationError",
                    value = SwaggerDocs.Examples.ERROR_VALIDATION
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = SwaggerDocs.Common.UNAUTHORIZED_401,
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Unauthorized",
                    value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED
                )
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = SwaggerDocs.Common.FORBIDDEN_403,
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Forbidden",
                    value = SwaggerDocs.Examples.ERROR_FORBIDDEN
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = SwaggerDocs.Common.NOT_FOUND_404,
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "NotFound",
                    value = SwaggerDocs.Examples.ERROR_VALIDATION
                )
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = SwaggerDocs.Common.SERVER_ERROR_500,
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "InternalError",
                    value = SwaggerDocs.Examples.ERROR_INTERNAL
                )
            )
        ),
        @ApiResponse(
            responseCode = "503", 
            description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "ServiceUnavailable",
                    value = SwaggerDocs.Examples.ERROR_SERVICE_UNAVAILABLE
                )
            )
        )
    })
    public ResponseEntity<TrackingResponse> getTracking(
            @Parameter(description = "JWT Bearer token for authentication", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authorizationHeader,
            
            @Parameter(description = SwaggerDocs.Parameters.BOOKING_REFERENCE, required = true, example = "TSHGCGP23000031")
            @RequestParam
            @NotBlank(message = "Tracking key is required. Please provide a booking number, BL number, or container number.")
            @Size(min = 3, max = 50, message = "Tracking key must be between 3 and 50 characters. Please ensure your tracking number is valid.")
            String key,
            
            @Parameter(description = SwaggerDocs.Parameters.CONTAINER_NUMBER, required = false, example = "RCDU7017035")
            @RequestParam(required = false)
            String strcontainer,
            
            HttpServletRequest servletRequest
    ) {
        String clientIp = getClientIpAddress(servletRequest);
        logger.info("Tracking request received from IP: {} for container: {}", 
                   clientIp, strcontainer);

        // Note: JWT authentication is validated by security filter
        // If we reach this point, the user has valid JWT token

        try {
            // Extract JWT token from Authorization header
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                logger.warn("Missing or invalid Authorization header from IP: {}", clientIp);
                TrackingResponse errorResponse = new TrackingResponse(null, "400", "Missing or invalid Authorization header");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Validate JWT token and extract username
            String username = trackingService.validateTokenAndExtractUsername(token);
            logger.info("Valid JWT token for user: {} from IP: {}", username, clientIp);

            // Create TrackingRequest object from query parameters
            TrackingRequest request = new TrackingRequest(key, strcontainer);
            
            // Request validation is handled by @Valid annotations on parameters
            logger.info("Processing tracking request for key: {} from user: {} at IP: {}", 
                       key, username, clientIp);

            // Fetch tracking data from external API
            TrackingResponse response = trackingService.fetchTracking(request, username);
            
            // Return appropriate HTTP status based on response
            HttpStatus httpStatus = determineHttpStatus(response.getMessageCode());
            logger.info("Tracking request completed for user: {} with status: {}", username, response.getMessageCode());
            
            return ResponseEntity.status(httpStatus).body(response);

        } catch (SecurityException e) {
            logger.error("JWT validation failed from IP: {} - {}", clientIp, e.getMessage());
            TrackingResponse errorResponse = new TrackingResponse(null, "401", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            
        } catch (Exception e) {
            logger.error("Unexpected error processing tracking request from IP: {} - {}", clientIp, e.getMessage(), e);
            TrackingResponse errorResponse = new TrackingResponse(null, "500", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

     /**
     * Extract JWT token from Authorization header
     */
    private String extractJwtToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }



    /**
     * Determine HTTP status based on response message code
     */
    private HttpStatus determineHttpStatus(String messageCode) {
        switch (messageCode) {
            case "200":
                return HttpStatus.OK;
            case "400":
                return HttpStatus.BAD_REQUEST;
            case "401":
                return HttpStatus.UNAUTHORIZED;
            case "403":
                return HttpStatus.FORBIDDEN;
            case "404":
                return HttpStatus.NOT_FOUND;
            case "503":
                return HttpStatus.SERVICE_UNAVAILABLE;
            case "500":
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * Handle missing request parameter errors (when required parameter is not provided)
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<TrackingResponse> handleMissingParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        String clientIp = getClientIpAddress(request);
        String paramName = ex.getParameterName();
        String message = String.format("Missing required parameter: '%s'. Please provide a valid %s.", 
                                      paramName, 
                                      paramName.equals("key") ? "tracking key (booking number, BL number, or container number)" : paramName);
        
        logger.warn("Missing required parameter '{}' from IP: {}", paramName, clientIp);
        
        TrackingResponse errorResponse = new TrackingResponse(null, "400", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Get the current authenticated username using Spring Security
     */
    private String getCurrentUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null && !auth.getName().equals("anonymousUser")) {
                return auth.getName();
            }
        } catch (Exception e) {
            logger.debug("Error getting current username: {}", e.getMessage());
        }
        return null;
    }
}
