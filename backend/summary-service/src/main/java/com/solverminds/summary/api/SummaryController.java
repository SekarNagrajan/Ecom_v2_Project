package com.solverminds.summary.api;

import com.solverminds.summary.dto.DashboardSummaryDto;
import com.solverminds.summary.dto.ArrivalNoticeDto;
import com.solverminds.summary.dto.DeliveryOrderDto;
import com.solverminds.summary.dto.CustomerReleaseOrderDto;
import com.solverminds.summary.repo.SummaryRepository;
import com.solverminds.summary.security.CustomerContext;
import com.solverminds.summary.service.SummaryTimezoneCacheService;
import com.svm.ecom.common.util.PortLocalTimeUtil;
import java.time.Instant;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

// Swagger/OpenAPI imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

// Centralized documentation constants
import com.svm.ecom.docs.SwaggerDocs;
// Error response schema
import com.svm.ecom.dto.ErrorResponse;
import com.svm.ecom.error.ErrorCodes;
import com.svm.ecom.error.ErrorReason;

// Generic wrapper and Swagger alias imports
import com.solverminds.summary.dto.DashboardSummaryListResponse;
import com.solverminds.summary.dto.CROListResponse;
import com.solverminds.summary.dto.ArrivalNoticeListResponse;
import com.solverminds.summary.dto.DeliveryOrderListResponse;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/summary")
@Tag(name = "Summary", description = "Summary service endpoints for dashboard, CRO, arrival notices, and delivery orders")
@SecurityRequirement(name = "bearerAuth")
public class SummaryController {

    private final SummaryRepository repo;
    private final CustomerContext customerContext;
    private final SummaryTimezoneCacheService timezoneCache;

    public SummaryController(SummaryRepository repo, CustomerContext customerContext,
                            SummaryTimezoneCacheService timezoneCache) {
        this.repo = repo;
        this.customerContext = customerContext;
        this.timezoneCache = timezoneCache;
    }

    // ---- Dashboard -----------------------------------------------------------
    @Operation(
        summary = SwaggerDocs.Summary.SUMMARY_DASHBOARD,
        description = SwaggerDocs.Summary.DESC_DASHBOARD,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(schema = @Schema(implementation = DashboardSummaryListResponse.class))
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
        }
    )
    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(HttpServletRequest request) {
        try {
            // Extract JWT token from Authorization header
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }
            
            // Extract customerCode from JWT token
            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            
            List<DashboardSummaryDto> data = repo.dashboard(customerCode);
            if (data != null && timezoneCache != null) {
                for (DashboardSummaryDto dto : data) {
                    if (dto.getPolAt() != null && dto.getLoadPortIc() != null) {
                        try {
                            String tz = timezoneCache.getTimezoneForPort(dto.getLoadPortIc());
                            dto.setPolAt(PortLocalTimeUtil.labelWithOffset(dto.getPolAt(), tz));
                        } catch (Exception ignored) { }
                    }
                }
            }
            return ResponseEntity.ok(new DashboardSummaryListResponse(data));
            
        } catch (SecurityException e) {
            // JWT validation failures
            return unauthorized("Invalid JWT token or authentication failed");
        } catch (IllegalStateException ex) {
            return svcUnavailable();
        } catch (Exception e) {
            e.printStackTrace();
            return internalError("An unexpected error occurred while processing the request");
        }
    }

    // ---- CRO -----------------------------------------------------------------
    @Operation(
        summary = SwaggerDocs.Summary.SUMMARY_CRO,
        description = SwaggerDocs.Summary.DESC_CRO,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(schema = @Schema(implementation = CROListResponse.class))
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
        }
    )
    @GetMapping("/cro")
    public ResponseEntity<?> cro(
            @Parameter(description = SwaggerDocs.Parameters.FROM_DATE, required = true, example = "2023-01-01")
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = SwaggerDocs.Parameters.TO_DATE, required = true, example = "2023-12-31")
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            HttpServletRequest request) {

        if (from.isAfter(to)) return bad("'from' must be <= 'to'");
        
        try {
            // Extract JWT token from Authorization header
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }
            
            // Extract customerCode from JWT token
            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            
            List<CustomerReleaseOrderDto> data = repo.cro(customerCode, from, to);
            return ResponseEntity.ok(new CROListResponse(data));
            
        } catch (SecurityException e) {
            return unauthorized("Invalid JWT token or authentication failed");
        } catch (IllegalStateException ex) {
            return svcUnavailable();
        } catch (DataAccessException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.contains("EXECUTE permission was denied") || msg.contains("229")) {
                ErrorResponse err = new ErrorResponse(503, "Service Unavailable", LocalDateTime.now());
                err.addError(ErrorCodes.SERVICE_UNAVAILABLE, null, null, ErrorReason.SERVICE_UNAVAILABLE,
                    "Documentation database: EXECUTE permission denied on usp_ecomcrodetails. Grant EXECUTE to the application user.");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(err);
            }
            return internalError("Database error: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        } catch (Exception e) {
            return internalError("An unexpected error occurred while processing the request");
        }
    }

    // ---- Arrival Notice ---------------------------------------
    @Operation(
        summary = SwaggerDocs.Summary.SUMMARY_ARRIVAL_NOTICE,
        description = SwaggerDocs.Summary.DESC_ARRIVAL_NOTICE,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(
                    schema = @Schema(implementation = ArrivalNoticeListResponse.class),
                    examples = @ExampleObject(
                        name = "Success",
                        value = "{\"arrivalNotices\":[{\"customerCode\":\"20121345938\",\"blNumber\":\"BL20240115001\",\"arrivalNoticeNo\":\"AN001\",\"originPortId\":\"INNSA\",\"finalPortId\":\"AEJEA\",\"vesselCode\":\"MSC123\",\"voyage\":\"001E\",\"bound\":\"N\",\"hblNumber\":\"HBL001\",\"arrDate\":\"2025-09-26T02:00:00+04:00\",\"podTerminal\":\"AEJEA01\",\"ecomPrintStatus\":\"N\"}]}"
                    )
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
        }
    )
    @GetMapping("/arrival-notice")
    public ResponseEntity<?> arrivalNotice(
            @Parameter(description = SwaggerDocs.Parameters.FROM_DATE, required = true, example = "2023-01-01")
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = SwaggerDocs.Parameters.TO_DATE, required = true, example = "2023-12-31")
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            HttpServletRequest request) {

        if (from.isAfter(to)) return bad("'from' must be <= 'to'");
        
        try {
            // Extract JWT token from Authorization header
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }
            
            // Extract customerCode from JWT token
            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            
            List<ArrivalNoticeDto> data = repo.arrivalNotice(customerCode, from, to);
            if (data != null && timezoneCache != null) {
                for (ArrivalNoticeDto dto : data) {
                    if (dto.getArrDate() != null && dto.getFinalPortId() != null) {
                        try {
                            String tz = timezoneCache.getTimezoneForPort(dto.getFinalPortId());
                            dto.setArrDate(PortLocalTimeUtil.labelWithOffset(dto.getArrDate(), tz));
                        } catch (Exception ignored) { }
                    }
                }
            }
            return ResponseEntity.ok(new ArrivalNoticeListResponse(data));
            
        } catch (SecurityException e) {
            // JWT validation failures
            return unauthorized("Invalid JWT token or authentication failed");
        } catch (IllegalStateException ex) {
            return svcUnavailable();
        } catch (Exception e) {
            return internalError("An unexpected error occurred while processing the request");
        }
    }

    // ---- Delivery Order ---------------------------------------
    @Operation(
        summary = SwaggerDocs.Summary.SUMMARY_DELIVERY_ORDER,
        description = SwaggerDocs.Summary.DESC_DELIVERY_ORDER,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(
                    schema = @Schema(implementation = DeliveryOrderListResponse.class),
                    examples = @ExampleObject(
                        name = "Success",
                        value = "{\"deliveryOrders\":[{\"printStatus\":\"A\",\"ecomPrintStatus\":\"N\",\"delOrdNo\":\"DO001\",\"doValidityDate\":\"2025-09-30\",\"delOrdDate\":\"2025-09-25\",\"blNumber\":\"BL20240115001\",\"vessel\":\"MSC123\",\"voyage\":\"001E\",\"bound\":\"N\",\"loadPort\":\"INNSA\",\"dischargePort\":\"AEJEA\",\"terminal\":\"AEJEA01\",\"arrDate\":\"2025-09-26T02:00:00+04:00\"}]}"
                    )
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
        }
    )
    @GetMapping("/delivery-order")
    public ResponseEntity<?> deliveryOrder(
            @Parameter(description = SwaggerDocs.Parameters.FROM_DATE, required = true, example = "2023-01-01")
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = SwaggerDocs.Parameters.TO_DATE, required = true, example = "2023-12-31")
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            HttpServletRequest request) {

        if (from.isAfter(to)) return bad("'from' must be <= 'to'");
        
        try {
            // Extract JWT token from Authorization header
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }
            
            // Extract customerCode from JWT token
            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            
            List<DeliveryOrderDto> data = repo.deliveryOrder(customerCode, from, to);
            if (data != null && timezoneCache != null) {
                for (DeliveryOrderDto dto : data) {
                    if (dto.getArrDate() != null && dto.getDischargePort() != null) {
                        try {
                            String tz = timezoneCache.getTimezoneForPort(dto.getDischargePort());
                            dto.setArrDate(PortLocalTimeUtil.labelWithOffset(dto.getArrDate(), tz));
                        } catch (Exception ignored) { }
                    }
                }
            }
            return ResponseEntity.ok(new DeliveryOrderListResponse(data));
            
        } catch (SecurityException e) {
            // JWT validation failures
            return unauthorized("Invalid JWT token or authentication failed");
        } catch (IllegalStateException ex) {
            return svcUnavailable();
        } catch (Exception e) {
            return internalError("An unexpected error occurred while processing the request");
        }
    }

    // ---- helpers -------------------------------------------------------------

    /**
     * Create a proper ErrorResponse for 503 Service Unavailable errors
     */
    private ResponseEntity<ErrorResponse> svcUnavailable() {
        ErrorResponse errorResponse = new ErrorResponse(
            503,
            "Service Unavailable", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.SERVICE_UNAVAILABLE,
            null,
            null,
            ErrorReason.SERVICE_UNAVAILABLE,
            "Report database is currently unavailable"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 400 Bad Request errors
     */
    private ResponseEntity<ErrorResponse> bad(String msg) {
        ErrorResponse errorResponse = new ErrorResponse(
            400,
            "Bad Request", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.BAD_REQUEST,
            null,
            null,
            ErrorReason.BAD_REQUEST,
            msg != null ? msg : "Bad request parameters"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 500 Internal Server Error
     */
    private ResponseEntity<ErrorResponse> internalError(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            500,
            "Internal Server Error", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.INTERNAL_ERROR,
            null,
            null,
            ErrorReason.INTERNAL_ERROR,
            message != null ? message : "An unexpected server error occurred"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Create a proper ErrorResponse for 401 Unauthorized errors
     */
    private ResponseEntity<ErrorResponse> unauthorized(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            401,
            "Unauthorized", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.UNAUTHORIZED,
            null,
            null,
            ErrorReason.UNAUTHORIZED,
            message != null ? message : "Authentication required or invalid token"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
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
}
