package com.svm.ecom.carrier.controller;

import com.svm.ecom.carrier.model.CoastalScheduleBlock;
import com.svm.ecom.carrier.model.CoastalScheduleRequest;
import com.svm.ecom.carrier.model.LongRangeScheduleBlock;
import com.svm.ecom.carrier.model.LongRangeScheduleRequest;
import com.svm.ecom.carrier.service.CoastalScheduleException;
import com.svm.ecom.carrier.service.CoastalScheduleService;
import com.svm.ecom.carrier.service.LongRangeScheduleException;
import com.svm.ecom.carrier.service.LongRangeScheduleService;
import com.svm.ecom.docs.SwaggerDocs;
import com.svm.ecom.dto.ErrorResponse;
import com.svm.ecom.error.ErrorCodes;
import com.svm.ecom.error.ErrorReason;
import com.svm.ecom.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/schedule")
@Tag(name = "Carrier API", description = "Coastal and long range carrier schedule details")
public class CarrierController {

    private static final Logger logger = LoggerFactory.getLogger(CarrierController.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CoastalScheduleService coastalScheduleService;

    @Autowired
    private LongRangeScheduleService longRangeScheduleService;

    @PostMapping("/getscheduledetail")
    @Operation(
        summary = SwaggerDocs.Schedule.SUMMARY_GET_COASTAL_SCHEDULE,
        description = SwaggerDocs.Schedule.DESC_GET_COASTAL_SCHEDULE
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = SwaggerDocs.Common.SUCCESS_200,
            content = @Content(
                array = @ArraySchema(schema = @Schema(implementation = CoastalScheduleBlock.class)),
                examples = @ExampleObject(
                    name = "Coastal Schedule Response",
                    value = "[\n" +
                        "  {\n" +
                        "    \"coastalSchedule\": [\n" +
                        "      {\n" +
                        "        \"serviceCode\": \"PAX\",\n" +
                        "        \"serviceName\": \"PACIFIC XPRESS\",\n" +
                        "        \"vesselCode\": \"PCX\",\n" +
                        "        \"vesselName\": \"PC XPRESS\",\n" +
                        "        \"vesselImoNumber\": \"12345\",\n" +
                        "        \"voyageNumber\": \"045\",\n" +
                        "        \"pfBound\": \"N\",\n" +
                        "        \"bound\": \"N\",\n" +
                        "        \"pivotVoyage1\": \"612\",\n" +
                        "        \"pivotBound1\": \"N\",\n" +
                        "        \"pivotVoyage2\": \"\",\n" +
                        "        \"pivotBound2\": null,\n" +
                        "        \"pivotVoyage3\": \"\",\n" +
                        "        \"pivotBound3\": null,\n" +
                        "        \"portCode\": \"INNSA\",\n" +
                        "        \"portName\": \"NHAVA SHEVA, INDIA\",\n" +
                        "        \"terminalCode\": \"INNSABMCT0\",\n" +
                        "        \"terminalName\": \"BMCT TERMINAL\",\n" +
                        "        \"callType\": \"C\",\n" +
                        "        \"groupCallId\": \"Grpcall-90142\",\n" +
                        "        \"arrPilot\": \"2026-08-19T09:00:00+05:30\",\n" +
                        "        \"arrDock\": \"2026-08-19T11:00:00+05:30\",\n" +
                        "        \"depDock\": \"2026-08-21T05:00:00+05:30\",\n" +
                        "        \"depPilot\": \"2026-08-21T06:00:00+05:30\",\n" +
                        "        \"terminalTime\": 48.0,\n" +
                        "        \"remarks\": \"\",\n" +
                        "        \"status\": \"S\",\n" +
                        "        \"statusFlag\": \"Completed\",\n" +
                        "        \"arrivalReported\": \"Y\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  }\n" +
                        "]"
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = SwaggerDocs.Common.BAD_REQUEST_400,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                name = "Bad Request",
                value = "{\n  \"statusCode\": 400,\n  \"statusCodeText\": \"Bad Request\",\n  \"errorDateTime\": \"2026-08-08T11:54:11Z\",\n  \"errors\": [\n    {\n      \"errorCode\": 1006,\n      \"field\": \"fromDate\",\n      \"value\": null,\n      \"reason\": \"BAD_REQUEST\",\n      \"message\": \"fromDate must be on or before toDate\"\n    }\n  ]\n}"
            ))),
        @ApiResponse(responseCode = "401", description = SwaggerDocs.Common.UNAUTHORIZED_401,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                name = "Unauthorized",
                value = "{\n  \"statusCode\": 401,\n  \"statusCodeText\": \"Unauthorized\",\n  \"errorDateTime\": \"2026-08-08T11:54:11Z\",\n  \"errors\": [\n    {\n      \"errorCode\": 1001,\n      \"field\": null,\n      \"value\": null,\n      \"reason\": \"UNAUTHORIZED\",\n      \"message\": \"JWT token has expired\"\n    }\n  ]\n}"
            ))),
        @ApiResponse(responseCode = "403", description = SwaggerDocs.Common.FORBIDDEN_403,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                name = "Forbidden",
                value = "{\n  \"statusCode\": 403,\n  \"statusCodeText\": \"Forbidden\",\n  \"errorDateTime\": \"2026-08-08T11:54:11Z\",\n  \"errors\": [\n    {\n      \"errorCode\": 1002,\n      \"field\": null,\n      \"value\": null,\n      \"reason\": \"FORBIDDEN\",\n      \"message\": \"Coastal schedule request is forbidden\"\n    }\n  ]\n}"
            ))),
        @ApiResponse(responseCode = "404", description = SwaggerDocs.Common.NOT_FOUND_404,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                name = "Not Found",
                value = "{\n  \"statusCode\": 404,\n  \"statusCodeText\": \"Not Found\",\n  \"errorDateTime\": \"2026-08-08T11:54:11Z\",\n  \"errors\": [\n    {\n      \"errorCode\": 1007,\n      \"field\": null,\n      \"value\": null,\n      \"reason\": \"NOT_FOUND\",\n      \"message\": \"Coastal schedule service was not found\"\n    }\n  ]\n}"
            ))),
        @ApiResponse(responseCode = "500", description = SwaggerDocs.Common.SERVER_ERROR_500,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                name = "Server Error",
                value = "{\n  \"statusCode\": 500,\n  \"statusCodeText\": \"Internal Server Error\",\n  \"errorDateTime\": \"2026-08-08T11:54:11Z\",\n  \"errors\": [\n    {\n      \"errorCode\": 1000,\n      \"field\": null,\n      \"value\": null,\n      \"reason\": \"INTERNAL_ERROR\",\n      \"message\": \"Coastal schedule service error\"\n    }\n  ]\n}"
            ))),
        @ApiResponse(responseCode = "503", description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                name = "Service Unavailable",
                value = "{\n  \"statusCode\": 503,\n  \"statusCodeText\": \"Service Unavailable\",\n  \"errorDateTime\": \"2026-08-08T11:54:11Z\",\n  \"errors\": [\n    {\n      \"errorCode\": 1003,\n      \"field\": null,\n      \"value\": null,\n      \"reason\": \"SERVICE_UNAVAILABLE\",\n      \"message\": \"Coastal schedule service is unavailable\"\n    }\n  ]\n}"
            )))
    })
    public ResponseEntity<?> getScheduleDetail(
            @Parameter(description = "JWT Bearer token for authentication (format: 'Bearer <token>')", required = true)
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody List<CoastalScheduleRequest> requests,
            HttpServletRequest servletRequest) {

        String clientIp = clientIp(servletRequest);
        logger.info("Coastal schedule request received from IP: {} with {} item(s)", clientIp,
                requests == null ? 0 : requests.size());
        try {
            String username = username(authorizationHeader, clientIp);
            if (username == null) {
                return error(HttpStatus.BAD_REQUEST, "Missing or invalid Authorization header");
            }
            List<CoastalScheduleBlock> body = coastalScheduleService.fetch(requests, username);
            return ResponseEntity.ok(body);
        } catch (CoastalScheduleException e) {
            logger.warn("Coastal schedule request failed from IP {}: {}", clientIp, e.getMessage());
            return error(e.getStatus(), e.getMessage());
        } catch (SecurityException e) {
            logger.error("JWT validation failed from IP: {} - {}", clientIp, e.getMessage());
            return error(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error processing coastal schedule request from IP: {} - {}", clientIp, e.getMessage(), e);
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @PostMapping("/getLongRangeScheduleInfo")
    @Operation(
        summary = SwaggerDocs.Schedule.SUMMARY_GET_LONG_RANGE_SCHEDULE,
        description = SwaggerDocs.Schedule.DESC_GET_LONG_RANGE_SCHEDULE
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = SwaggerDocs.Common.SUCCESS_200,
            content = @Content(
                array = @ArraySchema(schema = @Schema(implementation = LongRangeScheduleBlock.class)),
                examples = @ExampleObject(
                    name = "Long Range Schedule Response",
                    value = "[\n" +
                        "  {\n" +
                        "    \"longRangeSchedule\": [\n" +
                        "      {\n" +
                        "        \"serviceCode\": \"PAX\",\n" +
                        "        \"serviceName\": \"PANDA EXPRESS\",\n" +
                        "        \"proformaCode\": \"PAXPRFSCH0001\",\n" +
                        "        \"vesselCode\": \"EXC\",\n" +
                        "        \"vesselName\": \"XIN CHANG SHU\",\n" +
                        "        \"vesselImo\": \"9304813\",\n" +
                        "        \"maxTeu\": 8500,\n" +
                        "        \"voyageNumber\": \"025\",\n" +
                        "        \"bound\": \"S\",\n" +
                        "        \"portCode\": \"CNSHK\",\n" +
                        "        \"portName\": \"SHEKOU, CHINA\",\n" +
                        "        \"terminalCode\": \"CNSHKCCT00\",\n" +
                        "        \"terminalName\": \"CHIWAN CONTAINER TERMINAL\",\n" +
                        "        \"arrivalPilot\": \"2026-08-19T01:00:00+08:00\",\n" +
                        "        \"arrivalDock\": \"2026-08-19T01:49:00+08:00\",\n" +
                        "        \"departureDock\": \"2026-08-19T11:36:00+08:00\",\n" +
                        "        \"departurePilot\": \"2026-08-19T12:36:00+08:00\",\n" +
                        "        \"callStatus\": \"S\",\n" +
                        "        \"remarks\": \"\",\n" +
                        "        \"scheduleWeek\": 34,\n" +
                        "        \"scheduleMonthYear\": \"Aug-2026\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  }\n" +
                        "]"
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = SwaggerDocs.Common.BAD_REQUEST_400,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                name = "Bad Request",
                value = "{\n  \"statusCode\": 400,\n  \"statusCodeText\": \"Bad Request\",\n  \"errorDateTime\": \"2026-08-08T11:54:11Z\",\n  \"errors\": [\n    {\n      \"errorCode\": 1006,\n      \"field\": \"weekStartDay\",\n      \"value\": null,\n      \"reason\": \"BAD_REQUEST\",\n      \"message\": \"weekStartDay must be M or S\"\n    }\n  ]\n}"
            ))),
        @ApiResponse(responseCode = "401", description = SwaggerDocs.Common.UNAUTHORIZED_401,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                name = "Unauthorized",
                value = "{\n  \"statusCode\": 401,\n  \"statusCodeText\": \"Unauthorized\",\n  \"errorDateTime\": \"2026-08-08T11:54:11Z\",\n  \"errors\": [\n    {\n      \"errorCode\": 1001,\n      \"field\": null,\n      \"value\": null,\n      \"reason\": \"UNAUTHORIZED\",\n      \"message\": \"JWT token has expired\"\n    }\n  ]\n}"
            ))),
        @ApiResponse(responseCode = "403", description = SwaggerDocs.Common.FORBIDDEN_403,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                name = "Forbidden",
                value = "{\n  \"statusCode\": 403,\n  \"statusCodeText\": \"Forbidden\",\n  \"errorDateTime\": \"2026-08-08T11:54:11Z\",\n  \"errors\": [\n    {\n      \"errorCode\": 1002,\n      \"field\": null,\n      \"value\": null,\n      \"reason\": \"FORBIDDEN\",\n      \"message\": \"Long range schedule request is forbidden\"\n    }\n  ]\n}"
            ))),
        @ApiResponse(responseCode = "404", description = SwaggerDocs.Common.NOT_FOUND_404,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                name = "Not Found",
                value = "{\n  \"statusCode\": 404,\n  \"statusCodeText\": \"Not Found\",\n  \"errorDateTime\": \"2026-08-08T11:54:11Z\",\n  \"errors\": [\n    {\n      \"errorCode\": 1007,\n      \"field\": null,\n      \"value\": null,\n      \"reason\": \"NOT_FOUND\",\n      \"message\": \"Long range schedule service was not found\"\n    }\n  ]\n}"
            ))),
        @ApiResponse(responseCode = "500", description = SwaggerDocs.Common.SERVER_ERROR_500,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                name = "Server Error",
                value = "{\n  \"statusCode\": 500,\n  \"statusCodeText\": \"Internal Server Error\",\n  \"errorDateTime\": \"2026-08-08T11:54:11Z\",\n  \"errors\": [\n    {\n      \"errorCode\": 1000,\n      \"field\": null,\n      \"value\": null,\n      \"reason\": \"INTERNAL_ERROR\",\n      \"message\": \"Long range schedule service error\"\n    }\n  ]\n}"
            ))),
        @ApiResponse(responseCode = "503", description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                name = "Service Unavailable",
                value = "{\n  \"statusCode\": 503,\n  \"statusCodeText\": \"Service Unavailable\",\n  \"errorDateTime\": \"2026-08-08T11:54:11Z\",\n  \"errors\": [\n    {\n      \"errorCode\": 1003,\n      \"field\": null,\n      \"value\": null,\n      \"reason\": \"SERVICE_UNAVAILABLE\",\n      \"message\": \"Long range schedule service is unavailable\"\n    }\n  ]\n}"
            )))
    })
    public ResponseEntity<?> getLongRangeScheduleInfo(
            @Parameter(description = "JWT Bearer token for authentication (format: 'Bearer <token>')", required = true)
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody List<LongRangeScheduleRequest> requests,
            HttpServletRequest servletRequest) {

        String clientIp = clientIp(servletRequest);
        logger.info("Long range schedule request received from IP: {} with {} item(s)", clientIp,
                requests == null ? 0 : requests.size());
        try {
            String username = username(authorizationHeader, clientIp);
            if (username == null) {
                return error(HttpStatus.BAD_REQUEST, "Missing or invalid Authorization header");
            }
            List<LongRangeScheduleBlock> body = longRangeScheduleService.fetch(requests, username);
            return ResponseEntity.ok(body);
        } catch (LongRangeScheduleException e) {
            logger.warn("Long range schedule request failed from IP {}: {}", clientIp, e.getMessage());
            return error(e.getStatus(), e.getMessage());
        } catch (SecurityException e) {
            logger.error("JWT validation failed from IP: {} - {}", clientIp, e.getMessage());
            return error(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error processing long range schedule request from IP: {} - {}", clientIp, e.getMessage(), e);
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private String username(String authorizationHeader, String clientIp) {
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }
        if (token == null) {
            logger.warn("Missing or invalid Authorization header from IP: {}", clientIp);
            return null;
        }
        try {
            return jwtUtil.validateTokenWithSession(token);
        } catch (RuntimeException e) {
            throw new SecurityException(e.getMessage());
        }
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message) {
        ErrorResponse body = new ErrorResponse(status.value(), status.getReasonPhrase(), LocalDateTime.now());
        body.addError(errorCode(status), fieldFrom(message), null, errorReason(status), message);
        return ResponseEntity.status(status).body(body);
    }

    private int errorCode(HttpStatus status) {
        if (status == HttpStatus.BAD_REQUEST) {
            return ErrorCodes.BAD_REQUEST;
        }
        if (status == HttpStatus.UNAUTHORIZED) {
            return ErrorCodes.UNAUTHORIZED;
        }
        if (status == HttpStatus.FORBIDDEN) {
            return ErrorCodes.FORBIDDEN;
        }
        if (status == HttpStatus.NOT_FOUND) {
            return ErrorCodes.NOT_FOUND;
        }
        if (status == HttpStatus.SERVICE_UNAVAILABLE) {
            return ErrorCodes.SERVICE_UNAVAILABLE;
        }
        if (status == HttpStatus.TOO_MANY_REQUESTS) {
            return ErrorCodes.RATE_LIMITED;
        }
        return ErrorCodes.INTERNAL_ERROR;
    }

    private ErrorReason errorReason(HttpStatus status) {
        if (status == HttpStatus.BAD_REQUEST) {
            return ErrorReason.BAD_REQUEST;
        }
        if (status == HttpStatus.UNAUTHORIZED) {
            return ErrorReason.UNAUTHORIZED;
        }
        if (status == HttpStatus.FORBIDDEN) {
            return ErrorReason.FORBIDDEN;
        }
        if (status == HttpStatus.NOT_FOUND) {
            return ErrorReason.NOT_FOUND;
        }
        if (status == HttpStatus.SERVICE_UNAVAILABLE) {
            return ErrorReason.SERVICE_UNAVAILABLE;
        }
        if (status == HttpStatus.TOO_MANY_REQUESTS) {
            return ErrorReason.RATE_LIMITED;
        }
        return ErrorReason.INTERNAL_ERROR;
    }

    private String fieldFrom(String message) {
        if (message == null) {
            return null;
        }
        if (message.startsWith("service ")) {
            return "service";
        }
        if (message.startsWith("vessel ")) {
            return "vessel";
        }
        if (message.startsWith("fromDate ")) {
            return "fromDate";
        }
        if (message.startsWith("toDate ")) {
            return "toDate";
        }
        if (message.startsWith("weekStartDay ")) {
            return "weekStartDay";
        }
        return null;
    }

    private String clientIp(HttpServletRequest request) {
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
}
