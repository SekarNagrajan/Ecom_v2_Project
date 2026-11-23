package com.svm.ecom.schedule.controller;


import com.svm.ecom.schedule.model.ScheduleResponse;
import com.svm.ecom.schedule.model.VesselScheduleResponse;
import com.svm.ecom.schedule.model.PortScheduleResponse;

import com.svm.ecom.schedule.service.ScheduleService;
import com.svm.ecom.schedule.service.VesselService;
import com.svm.ecom.schedule.service.PortService;
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
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import com.svm.ecom.dto.ErrorResponse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.ConstraintViolationException;

@RestController
@RequestMapping("/api/v1/schedule")
@Tag(name = "Schedule API", description = "Fetch schedules for Point-to-Point Routings, Port Schedules, and Vessel Schedules")
@Validated
public class ScheduleController {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    @Autowired
    private ScheduleService scheduleService;
    
    @Autowired
    private VesselService vesselService;
    
    @Autowired
    private PortService portService;

    // Note: PermissionService dependency removed as part of UserPermission system disabling

    @GetMapping("/route")
    @Operation(
        summary = SwaggerDocs.Schedule.SUMMARY_GET_SCHEDULE,
        description = SwaggerDocs.Schedule.DESC_GET_SCHEDULE
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = SwaggerDocs.Common.SUCCESS_200,
            content = @Content(
                schema = @Schema(implementation = ScheduleResponse.class),
                examples = @ExampleObject(
                    name = "Route Schedule Response",
                    value = "{\n" +
                        "  \"scheduleData\": [\n" +
                        "    {\n" +
                        "      \"portpair\": [\n" +
                        "        {\n" +
                        "          \"pol\": \"CNNGB\",\n" +
                        "          \"pod\": \"ESBCN\",\n" +
                        "          \"pol_name\": \"NINGBO, CHINA\",\n" +
                        "          \"pod_name\": \"BARCELONA, SPAIN\",\n" +
                        "          \"etd\": \"2026-03-02T17:00:00+08:00\",\n" +
                        "          \"eta\": \"2026-03-12T10:00:00+01:00\",\n" +
                        "          \"service_code\": \"PAX\",\n" +
                        "          \"service_name\": \"PANDA EXPRESS\",\n" +
                        "          \"vessel_code\": \"BUX\",\n" +
                        "          \"vessel_name\": \"BUXWAVE\",\n" +
                        "          \"vessel_imo\": \"9320427\",\n" +
                        "          \"vessel_operator\": \"TSH\",\n" +
                        "          \"vessel_operator_name\": \"TAILWIND SHIPPING LINE\",\n" +
                        "          \"vessel_owner_type\": \"Chartered In\",\n" +
                        "          \"voyage_number\": \"403\",\n" +
                        "          \"bound\": \"W\",\n" +
                        "          \"schedule_type\": \"Mainline\",\n" +
                        "          \"pol_terminal\": \"CNNGBCTT00\",\n" +
                        "          \"pod_terminal\": \"ESBCNHPB00\",\n" +
                        "          \"pol_terminal_name\": \"Ningbo Beilun Second Container Terminals Co.,LTD\",\n" +
                        "          \"pod_terminal_name\": \"Hutchison Ports BEST\",\n" +
                        "          \"loadType\": \"L\",\n" +
                        "          \"dischType\": \"T\",\n" +
                        "          \"groupCallId\": \"GrpCall-5499\",\n" +
                        "          \"dischargeCallId\": \"GrpCall-5503\",\n" +
                        "          \"loadCallId\": \"GrpCall-5499\",\n" +
                        "          \"dischargeBound\": \"N\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"pol\": \"ESBCN\",\n" +
                        "          \"pod\": \"NLRTM\",\n" +
                        "          \"pol_name\": \"BARCELONA, SPAIN\",\n" +
                        "          \"pod_name\": \"ROTTERDAM, NETHERLANDS\",\n" +
                        "          \"etd\": \"2026-03-14T03:12:00+01:00\",\n" +
                        "          \"eta\": \"2026-03-22T18:18:00+02:00\",\n" +
                        "          \"service_code\": \"DEX2\",\n" +
                        "          \"service_name\": \"DOLPHIN EXPRESS 2\",\n" +
                        "          \"vessel_code\": \"CON\",\n" +
                        "          \"vessel_name\": \"CONTSHIP JET\",\n" +
                        "          \"vessel_imo\": \"9348625\",\n" +
                        "          \"vessel_operator\": \"TSH\",\n" +
                        "          \"vessel_operator_name\": \"TAILWIND SHIPPING LINE\",\n" +
                        "          \"vessel_owner_type\": \"Chartered In\",\n" +
                        "          \"voyage_number\": \"403\",\n" +
                        "          \"bound\": \"N\",\n" +
                        "          \"schedule_type\": \"Barge\",\n" +
                        "          \"pol_terminal\": \"ESBCNHPB00\",\n" +
                        "          \"pod_terminal\": \"NLRTMECT00\",\n" +
                        "          \"pol_terminal_name\": \"Hutchison Ports BEST\",\n" +
                        "          \"pod_terminal_name\": \"Europe Container Terminals\",\n" +
                        "          \"loadType\": \"T\",\n" +
                        "          \"dischType\": \"L\",\n" +
                        "          \"groupCallId\": \"GrpCall-5009\",\n" +
                        "          \"dischargeCallId\": \"GrpCall-5073\",\n" +
                        "          \"loadCallId\": \"GrpCall-5009\",\n" +
                        "          \"dischargeBound\": \"N\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"origin\": \"CNNGB\",\n" +
                        "      \"delivery\": \"NLRTM\",\n" +
                        "      \"origin_name\": \"NINGBO, CHINA\",\n" +
                        "      \"delivery_name\": \"ROTTERDAM, NETHERLANDS\",\n" +
                        "      \"etd\": \"2026-03-02T17:00:00+08:00\",\n" +
                        "      \"eta\": \"2026-03-22T18:18:00+02:00\",\n" +
                        "      \"service_code\": \"PAX\",\n" +
                        "      \"service_name\": \"PANDA EXPRESS\",\n" +
                        "      \"vessel_code\": \"BUX\",\n" +
                        "      \"vessel_name\": \"BUXWAVE\",\n" +
                        "      \"vessel_imo\": \"9320427\",\n" +
                        "      \"vessel_operator\": \"TSH\",\n" +
                        "      \"vessel_operator_name\": \"TAILWIND SHIPPING LINE\",\n" +
                        "      \"vessel_owner_type\": \"Chartered In\",\n" +
                        "      \"voyage_number\": \"403\",\n" +
                        "      \"bound\": \"W\",\n" +
                        "      \"leg_type\": \"Multiple\",\n" +
                        "      \"default_route\": \"Y\",\n" +
                        "      \"transit_time\": 44,\n" +
                        "      \"load_callid\": \"GrpCall-5499\",\n" +
                        "      \"disch_callid\": \"GrpCall-5449\",\n" +
                        "      \"default_route_order\": 1,\n" +
                        "      \"inland_flag\": \"N\",\n" +
                        "      \"transshipment_count\": 1,\n" +
                        "      \"sourcePortName\": \"NINGBO, CHINA\",\n" +
                        "      \"destinationPortName\": \"ROTTERDAM, NETHERLANDS\",\n" +
                        "      \"sourceCountry\": \"CHINA\",\n" +
                        "      \"destinationCountry\": \"NETHERLANDS\",\n" +
                        "      \"noOfTransshipMent\": 1,\n" +
                        "      \"feederInvolved\": false,\n" +
                        "      \"secondary1\": \"N\",\n" +
                        "      \"secondary2\": \"N\",\n" +
                        "      \"routeid\": 1890298,\n" +
                        "      \"cutoff\": {\n" +
                        "        \"docClosing\": \"2025-12-07T20:00:00+08:00\",\n" +
                        "        \"vgmClosing\": \"2025-12-07T20:00:00+08:00\",\n" +
                        "        \"contClosing\": \"2025-12-07T20:00:00+08:00\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"messageCode\": \"200\",\n" +
                        "  \"message\": \"Success\",\n" +
                        "  \"timestamp\": \"2025-09-08T05:22:25Z\",\n" +
                        "  \"processedBy\": \"user1\"\n" +
                        "}"
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
            responseCode = "404", 
            description = SwaggerDocs.Common.NOT_FOUND_404,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
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
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<ScheduleResponse> getSchedule(
            @Parameter(description = "JWT Bearer token for authentication (format: 'Bearer <token>')", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authorizationHeader,
            
            @Parameter(description = "Origin port code", required = true, example = "CNNGB")
            @RequestParam
            @NotBlank(message = "Origin port code is required. Please provide a valid port code (e.g., CNNGB).")
            String origin,
            
            @Parameter(description = "Destination port code", required = true, example = "NLRTM")
            @RequestParam
            @NotBlank(message = "Destination port code is required. Please provide a valid port code (e.g., NLRTM).")
            String delivery,
            
            @Parameter(description = "Start date of the schedule window (YYYY-MM-DD)", required = true, example = "2026-03-02")
            @RequestParam
            @NotBlank(message = "From date is required. Please provide a date in YYYY-MM-DD format.")
            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "From date must be in YYYY-MM-DD format (e.g., 2026-03-02).")
            String fromDate,
            
            @Parameter(description = "End date of the schedule window (YYYY-MM-DD)", required = true, example = "2026-04-02")
            @RequestParam
            @NotBlank(message = "To date is required. Please provide a date in YYYY-MM-DD format.")
            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "To date must be in YYYY-MM-DD format (e.g., 2026-04-02).")
            String toDate,
            
            HttpServletRequest servletRequest
    ) {
        String clientIp = getClientIpAddress(servletRequest);
        logger.info("Schedule request received from IP: {} for origin: {}, delivery: {}", 
                   clientIp, origin, delivery);

        try {
            // Extract JWT token from Authorization header
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                logger.warn("Missing or invalid Authorization header from IP: {}", clientIp);
                ScheduleResponse errorResponse = new ScheduleResponse(null, "400", "Missing or invalid Authorization header");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Validate JWT token and extract username
            String username = scheduleService.validateTokenAndExtractUsername(token);
            logger.info("Valid JWT token for user: {} from IP: {}", username, clientIp);

            // Request validation is handled by @Valid annotations on parameters
            logger.info("Processing schedule request for route: {} -> {} from user: {} at IP: {}", 
                       origin, delivery, username, clientIp);

            // Fetch schedule from external API
            ScheduleResponse response = scheduleService.fetchSchedule(
                origin, delivery, fromDate, toDate, username);
            
            // Return appropriate HTTP status based on response
            HttpStatus httpStatus = determineHttpStatus(response.getMessageCode());
            logger.info("Schedule request completed for user: {} with status: {}", username, response.getMessageCode());
            
            return ResponseEntity.status(httpStatus).body(response);

        } catch (SecurityException e) {
            logger.error("JWT validation failed from IP: {} - {}", clientIp, e.getMessage());
            ScheduleResponse errorResponse = new ScheduleResponse(null, "401", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            
        } catch (Exception e) {
            logger.error("Unexpected error processing schedule request from IP: {} - {}", clientIp, e.getMessage(), e);
            ScheduleResponse errorResponse = new ScheduleResponse(null, "500", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/port")
    @Operation(
        summary = SwaggerDocs.Schedule.SUMMARY_GET_SCHEDULE,
        description = SwaggerDocs.Schedule.DESC_GET_SCHEDULE
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = SwaggerDocs.Common.SUCCESS_200,
            content = @Content(
                schema = @Schema(implementation = PortScheduleResponse.class),
                examples = @ExampleObject(value = "{\"code\":\"200\",\"message\":\"Port schedule fetched successfully\",\"byPort\":[{\"port\":\"SGSIN\",\"portname\":\"Singapore\",\"arrdate\":\"2025-08-05T06:00:00+08:00\",\"depdate\":\"2025-08-06T18:00:00+08:00\"}],\"timestamp\":\"2025-08-01T10:47:49Z\",\"processedBy\":\"user1\"}")
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = SwaggerDocs.Common.BAD_REQUEST_400,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = SwaggerDocs.Common.UNAUTHORIZED_401,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = SwaggerDocs.Common.FORBIDDEN_403,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = SwaggerDocs.Common.NOT_FOUND_404,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = SwaggerDocs.Common.SERVER_ERROR_500,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "503", 
            description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<PortScheduleResponse> getPortSchedule(
            @Parameter(description = "JWT Bearer token for authentication (format: 'Bearer <token>')", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authorizationHeader,
            
            @Parameter(description = "Port code", required = true, example = "SGSIN")
            @RequestParam
            @NotBlank(message = "Port code is required. Please provide a valid port code (e.g., SGSIN).")
            String portCode,
            
            @Parameter(description = "Start date for schedule search (YYYY-MM-DD)", required = true, example = "2025-08-01")
            @RequestParam
            @NotBlank(message = "From date is required. Please provide a date in YYYY-MM-DD format.")
            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "From date must be in YYYY-MM-DD format (e.g., 2025-08-01).")
            String fromDate,
            
            @Parameter(description = "End date for schedule search (YYYY-MM-DD)", required = true, example = "2025-08-10")
            @RequestParam
            @NotBlank(message = "To date is required. Please provide a date in YYYY-MM-DD format.")
            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "To date must be in YYYY-MM-DD format (e.g., 2025-08-10).")
            String toDate,
            
            HttpServletRequest servletRequest
    ) {
        String clientIp = getClientIpAddress(servletRequest);
        logger.info("Port schedule request received from IP: {} for port: {} from {} to {}", 
                   clientIp, portCode, fromDate, toDate);

        try {
            // Extract JWT token from Authorization header
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                logger.warn("Missing or invalid Authorization header from IP: {}", clientIp);
                PortScheduleResponse errorResponse = new PortScheduleResponse("400", "Missing or invalid Authorization header", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Validate JWT token and extract username
            String username = portService.validateTokenAndExtractUsername(token);
            logger.info("Valid JWT token for user: {} from IP: {}", username, clientIp);

            // Request validation is handled by @Valid annotations on parameters
            logger.info("Processing port schedule request for port: {} from user: {} at IP: {}", 
                       portCode, username, clientIp);

            // Fetch port schedule from database using native SQL query
            PortScheduleResponse response = portService.fetchPortScheduleFromDatabase(
                portCode, fromDate, toDate, username);
            
            // Return appropriate HTTP status based on response
            HttpStatus httpStatus = determineHttpStatusForPort(response.getCode());
            logger.info("Port schedule request completed for user: {} with status: {}", username, response.getCode());
            
            return ResponseEntity.status(httpStatus).body(response);

        } catch (SecurityException e) {
            logger.error("JWT validation failed from IP: {} - {}", clientIp, e.getMessage());
            PortScheduleResponse errorResponse = new PortScheduleResponse("401", e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            
        } catch (Exception e) {
            logger.error("Unexpected error processing port schedule request from IP: {} - {}", clientIp, e.getMessage(), e);
            PortScheduleResponse errorResponse = new PortScheduleResponse("500", "Internal server error", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/vessel")
    @Operation(
        summary = SwaggerDocs.Schedule.SUMMARY_GET_SCHEDULE,
        description = SwaggerDocs.Schedule.DESC_GET_SCHEDULE
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = SwaggerDocs.Common.SUCCESS_200,
            content = @Content(
                schema = @Schema(implementation = VesselScheduleResponse.class),
                examples = @ExampleObject(value = "{\"code\":\"200\",\"message\":\"Vessel schedule fetched successfully\",\"byVessel\":[{\"vessel\":\"BUX\",\"vesselname\":\"MSC BUSAN EXPRESS\",\"port\":\"SGSIN\",\"arrdate\":\"2025-08-05T06:00:00+08:00\",\"depdate\":\"2025-08-06T18:00:00+08:00\"}],\"timestamp\":\"2025-08-01T10:47:49Z\",\"processedBy\":\"user1\"}")
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = SwaggerDocs.Common.BAD_REQUEST_400,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = SwaggerDocs.Common.UNAUTHORIZED_401,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403", 
            description = SwaggerDocs.Common.FORBIDDEN_403,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = SwaggerDocs.Common.NOT_FOUND_404,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = SwaggerDocs.Common.SERVER_ERROR_500,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "503", 
            description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<VesselScheduleResponse> getVesselSchedule(
            @Parameter(description = "JWT Bearer token for authentication (format: 'Bearer <token>')", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authorizationHeader,
            
            @Parameter(description = "Vessel code", required = true, example = "BUX")
            @RequestParam
            @NotBlank(message = "Vessel code is required. Please provide a valid vessel code (e.g., BUX).")
            String vesselCode,
            
            @Parameter(description = "Start date for schedule search (YYYY-MM-DD)", required = true, example = "2025-08-01")
            @RequestParam
            @NotBlank(message = "From date is required. Please provide a date in YYYY-MM-DD format.")
            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "From date must be in YYYY-MM-DD format (e.g., 2025-08-01).")
            String fromDate,
            
            @Parameter(description = "End date for schedule search (YYYY-MM-DD)", required = true, example = "2025-08-10")
            @RequestParam
            @NotBlank(message = "To date is required. Please provide a date in YYYY-MM-DD format.")
            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "To date must be in YYYY-MM-DD format (e.g., 2025-08-10).")
            String toDate,
            
            HttpServletRequest servletRequest
    ) {
        String clientIp = getClientIpAddress(servletRequest);
        logger.info("Vessel schedule request received from IP: {} for vessel: {} from {} to {}", 
                   clientIp, vesselCode, fromDate, toDate);

        try {
            // Extract JWT token from Authorization header
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                logger.warn("Missing or invalid Authorization header from IP: {}", clientIp);
                VesselScheduleResponse errorResponse = new VesselScheduleResponse("400", "Missing or invalid Authorization header", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Validate JWT token and extract username
            String username = vesselService.validateTokenAndExtractUsername(token);
            logger.info("Valid JWT token for user: {} from IP: {}", username, clientIp);

            // Request validation is handled by @Valid annotations on parameters
            logger.info("Processing vessel schedule request for vessel: {} from user: {} at IP: {}", 
                       vesselCode, username, clientIp);

            // Fetch vessel schedule from database using native SQL query
            VesselScheduleResponse response = vesselService.fetchVesselScheduleFromDatabase(
                vesselCode, fromDate, toDate, username);
            
            // Return appropriate HTTP status based on response
            HttpStatus httpStatus = determineHttpStatusForVessel(response.getCode());
            logger.info("Vessel schedule request completed for user: {} with status: {}", username, response.getCode());
            
            return ResponseEntity.status(httpStatus).body(response);

        } catch (SecurityException e) {
            logger.error("JWT validation failed from IP: {} - {}", clientIp, e.getMessage());
            VesselScheduleResponse errorResponse = new VesselScheduleResponse("401", e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            
        } catch (Exception e) {
            logger.error("Unexpected error processing vessel schedule request from IP: {} - {}", clientIp, e.getMessage(), e);
            VesselScheduleResponse errorResponse = new VesselScheduleResponse("500", "Internal server error", null);
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
     * Determine HTTP status based on port response code
     */
    private HttpStatus determineHttpStatusForPort(String code) {
        if (code == null) return HttpStatus.INTERNAL_SERVER_ERROR;
        
        switch (code) {
            case "200": return HttpStatus.OK;
            case "400": return HttpStatus.BAD_REQUEST;
            case "401": return HttpStatus.UNAUTHORIZED;
            case "404": return HttpStatus.NOT_FOUND;
            case "500": return HttpStatus.INTERNAL_SERVER_ERROR;
            case "503": return HttpStatus.SERVICE_UNAVAILABLE;
            default: return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Determine HTTP status based on vessel response code
     */
    private HttpStatus determineHttpStatusForVessel(String code) {
        if (code == null) return HttpStatus.INTERNAL_SERVER_ERROR;
        
        switch (code) {
            case "200": return HttpStatus.OK;
            case "400": return HttpStatus.BAD_REQUEST;
            case "401": return HttpStatus.UNAUTHORIZED;
            case "404": return HttpStatus.NOT_FOUND;
            case "500": return HttpStatus.INTERNAL_SERVER_ERROR;
            case "503": return HttpStatus.SERVICE_UNAVAILABLE;
            default: return HttpStatus.INTERNAL_SERVER_ERROR;
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
     * Handle missing request parameter errors
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ScheduleResponse> handleMissingParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        String clientIp = getClientIpAddress(request);
        String paramName = ex.getParameterName();
        String message = String.format("Missing required parameter: '%s'. %s", 
                                      paramName, getParameterDescription(paramName));
        
        logger.warn("Missing required parameter '{}' from IP: {}", paramName, clientIp);
        
        ScheduleResponse errorResponse = new ScheduleResponse(null, "400", message);
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

    /**
     * Get user-friendly parameter descriptions
     */
    private String getParameterDescription(String paramName) {
        switch (paramName) {
            case "origin":
                return "Please provide a valid origin port code (e.g., CNNGB).";
            case "delivery":
                return "Please provide a valid destination port code (e.g., NLRTM).";
            case "portCode":
                return "Please provide a valid port code (e.g., SGSIN).";
            case "vesselCode":
                return "Please provide a valid vessel code (e.g., BUX).";
            case "fromDate":
                return "Please provide a start date in YYYY-MM-DD format (e.g., 2026-03-02).";
            case "toDate":
                return "Please provide an end date in YYYY-MM-DD format (e.g., 2026-04-02).";
            default:
                return "Please provide a valid value for this parameter.";
        }
    }
}
