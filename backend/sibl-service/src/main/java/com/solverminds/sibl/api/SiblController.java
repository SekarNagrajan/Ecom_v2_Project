package com.solverminds.sibl.api;

import com.solverminds.sibl.dto.SiSummaryDto;
import com.solverminds.sibl.dto.BlSummaryDto;
import com.solverminds.sibl.dto.SIBLRequestDTO;
import com.solverminds.sibl.dto.SIBLResponseDTO;
import com.solverminds.sibl.dto.SICreateResponse;
import com.svm.ecom.dto.ErrorResponse;
import com.solverminds.sibl.repo.SiblRepository;
import com.solverminds.sibl.security.CustomerContext;
import javax.validation.Valid;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;
import com.svm.ecom.error.ErrorCodes;
import com.svm.ecom.error.ErrorReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

// Generic wrapper and Swagger alias imports
import com.solverminds.sibl.dto.SiSummaryListResponse;
import com.solverminds.sibl.dto.BlSummaryListResponse;
import com.solverminds.sibl.dto.DocumentUploadResponse;
import com.solverminds.sibl.dto.DocumentListResponse;
import com.solverminds.sibl.service.DocumentService;
import com.solverminds.sibl.service.SiblTimezoneCacheService;
import com.svm.ecom.common.util.PortLocalTimeUtil;

// Swagger/OpenAPI imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
// Using fully qualified name for Swagger ApiResponse to avoid conflict
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

// Centralized documentation constants
import com.svm.ecom.docs.SwaggerDocs;

// EDI export
import com.solverminds.sibl.service.EdiExportService;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@RestController
@RequestMapping("/api/v1/bill-of-lading")
@Tag(
    name = "SIBL Service", 
    description = "Shipping Instruction and Bill of Lading service endpoints for retrieving SI and BL summary data"
)
@SecurityRequirement(name = "bearerAuth")
public class SiblController {

    private static final Logger logger = LoggerFactory.getLogger(SiblController.class);

    private final SiblRepository repo;
    private final CustomerContext customerContext;
    private final DocumentService documentService;
    private final EdiExportService ediExportService;
    private final SiblTimezoneCacheService timezoneCache;

    public SiblController(SiblRepository repo, CustomerContext customerContext,
                         @Qualifier("siblDocumentService") DocumentService documentService,
                         @Qualifier("siblEdiExportService") EdiExportService ediExportService,
                         SiblTimezoneCacheService timezoneCache) {
        this.repo = repo;
        this.customerContext = customerContext;
        this.documentService = documentService;
        this.ediExportService = ediExportService;
        this.timezoneCache = timezoneCache;
    }

    // ---- SI Summary -----------------------------------------------------------
    @Operation(
        summary = SwaggerDocs.Sibl.SUMMARY_SI_SUMMARY,
        description = SwaggerDocs.Sibl.DESC_SI_SUMMARY,
        tags = {"SIBL Service"}
    )
    @Parameter(
        name = "Authorization",
        description = "JWT Bearer token for authentication",
        required = true,
        in = ParameterIn.HEADER,
        schema = @Schema(type = "string"),
        example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = SwaggerDocs.Common.SUCCESS_200,
            content = @Content(
                schema = @Schema(implementation = SiSummaryListResponse.class),
                examples = @ExampleObject(
                    name = "Successful Response",
                    value = "{\n" +
                            "  \"lstSiSummary\": [\n" +
                            "    {\n" +
                            "      \"bookNo\": \"BKG20240115001\",\n" +
                            "      \"docId\": \"SI20240115001\",\n" +
                            "      \"siStatus\": \"ACTIVE\",\n" +
                            "      \"bookSource\": \"ONLINE\",\n" +
                            "      \"blNo\": \"BL20240115001\",\n" +
                            "      \"blStatus\": \"CONFIRMED\",\n" +
                            "      \"loadPortId\": \"INMAA\",\n" +
                            "      \"dischargePortId\": \"SGSIN\",\n" +
                            "      \"polAt\": \"2025-11-23T15:30:00+08:00\",\n" +
                            "      \"originPortId\": \"INMAA\",\n" +
                            "      \"destinationPortId\": \"SGSIN\"\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = SwaggerDocs.Common.BAD_REQUEST_400,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = SwaggerDocs.Common.UNAUTHORIZED_401,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = SwaggerDocs.Common.FORBIDDEN_403,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = SwaggerDocs.Common.NOT_FOUND_404,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = SwaggerDocs.Common.SERVER_ERROR_500,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "503",
            description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "ServiceUnavailable", value = SwaggerDocs.Examples.ERROR_SERVICE_UNAVAILABLE))
        )
    })
    @GetMapping("/sisummary")
    public ResponseEntity<?> siSummary(HttpServletRequest request) {
        try {
            // Extract JWT token from Authorization header
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            
            // Extract customerCode from JWT token
            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            
            List<SiSummaryDto> data = repo.getSiSummary(customerCode);
            if (data != null && timezoneCache != null) {
                for (SiSummaryDto dto : data) {
                    if (dto.getPolAt() != null && dto.getLoadPortId() != null) {
                        try {
                            String tz = timezoneCache.getTimezoneForPort(dto.getLoadPortId());
                            dto.setPolAt(PortLocalTimeUtil.labelWithOffset(dto.getPolAt(), tz));
                        } catch (Exception ignored) { }
                    }
                }
            }
            return ResponseEntity.ok(new SiSummaryListResponse(data));
            
        } catch (SecurityException e) {
            // JWT validation failures
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ---- BL Summary -----------------------------------------------------------
    @Operation(
        summary = SwaggerDocs.Sibl.SUMMARY_BL_SUMMARY,
        description = SwaggerDocs.Sibl.DESC_BL_SUMMARY,
        tags = {"SIBL Service"}
    )
    @Parameter(
        name = "Authorization",
        description = "JWT Bearer token for authentication",
        required = true,
        in = ParameterIn.HEADER,
        schema = @Schema(type = "string"),
        example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = SwaggerDocs.Common.SUCCESS_200,
            content = @Content(
                schema = @Schema(implementation = BlSummaryListResponse.class),
                examples = @ExampleObject(
                    name = "Successful Response",
                    value = "{\n" +
                            "  \"lstBlSummary\": [\n" +
                            "    {\n" +
                            "      \"bookNo\": \"BKG20240115001\",\n" +
                            "      \"blNo\": \"BL20240115001\",\n" +
                            "      \"agencyrefno\": \"REF001\",\n" +
                            "      \"originPortId\": \"INMAA\",\n" +
                            "      \"loadportid\": \"INMAA\",\n" +
                            "      \"dischargeportid\": \"SGSIN\",\n" +
                            "      \"finalPortId\": \"SGSIN\",\n" +
                            "      \"polat\": \"2023-11-23T15:30:00+08:00\",\n" +
                            "      \"status\": \"ACTIVE\",\n" +
                            "      \"blType\": \"AP\"\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = SwaggerDocs.Common.BAD_REQUEST_400,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = SwaggerDocs.Common.UNAUTHORIZED_401,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = SwaggerDocs.Common.FORBIDDEN_403,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = SwaggerDocs.Common.NOT_FOUND_404,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = SwaggerDocs.Common.SERVER_ERROR_500,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "503",
            description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "ServiceUnavailable", value = SwaggerDocs.Examples.ERROR_SERVICE_UNAVAILABLE))
        )
    })
    @GetMapping("/blsummary")
    public ResponseEntity<?> blSummary(
            @Parameter(
                description = "Bill of Lading types to retrieve (comma-separated). Common types: AP (Arrival Port), BP (Berth Port), DP (Departure Port)", 
                required = false, 
                example = "AP,BP",
                schema = @Schema(type = "string", defaultValue = "AP,BP")
            )
            @RequestParam(value = "types", defaultValue = "AP,BP") String types,
            @Parameter(
                description = "Processing flag. 'Y' for processed, 'N' for unprocessed", 
                required = false, 
                example = "N",
                schema = @Schema(type = "string", defaultValue = "N", allowableValues = {"Y", "N"})
            )
            @RequestParam(value = "flag", defaultValue = "N") String flag,
            HttpServletRequest request) {
        
        try {
            // Extract JWT token from Authorization header
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            
            // Extract customerCode from JWT token
            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            
            List<BlSummaryDto> data = repo.getBlSummary(customerCode, types, flag);
            if (data != null && timezoneCache != null) {
                for (BlSummaryDto dto : data) {
                    if (dto.getPolat() != null && dto.getLoadportid() != null) {
                        try {
                            String tz = timezoneCache.getTimezoneForPort(dto.getLoadportid());
                            dto.setPolat(PortLocalTimeUtil.labelWithOffset(dto.getPolat(), tz));
                        } catch (Exception ignored) { }
                    }
                    if (dto.getReceiptshipdate() != null && dto.getLoadportid() != null) {
                        try {
                            String tz = timezoneCache.getTimezoneForPort(dto.getLoadportid());
                            dto.setReceiptshipdate(PortLocalTimeUtil.labelWithOffset(dto.getReceiptshipdate(), tz));
                        } catch (Exception ignored) { }
                    }
                }
            }
            return ResponseEntity.ok(new BlSummaryListResponse(data));
            
        } catch (SecurityException e) {
            // JWT validation failures
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ---- SI Create -----------------------------------------------------------
    @Operation(
        summary = SwaggerDocs.Sibl.SUMMARY_SI_CREATE,
        description = SwaggerDocs.Sibl.DESC_SI_CREATE,
        tags = {"SIBL Service"},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SIBLRequestDTO.class),
                examples = {
                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Minimal",
                        value = SIExamples.MINIMAL_JSON
                    ),
                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Full",
                        value = SIExamples.FULL_JSON
                    )
                }
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = SwaggerDocs.Common.CREATED_201,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SICreateResponse.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.Common.BAD_REQUEST_400,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "ValidationError",
                        value = SwaggerDocs.Examples.ERROR_VALIDATION
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Unauthorized",
                        value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403",
                description = SwaggerDocs.Common.FORBIDDEN_403,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Forbidden",
                        value = SwaggerDocs.Examples.ERROR_FORBIDDEN
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "InternalError",
                        value = SwaggerDocs.Examples.ERROR_INTERNAL
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "503",
                description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "ServiceUnavailable",
                        value = SwaggerDocs.Examples.ERROR_SERVICE_UNAVAILABLE
                    )
                )
            )
        }
    )
    @PostMapping(path = "/sicreate", produces = MediaType.APPLICATION_JSON_VALUE)
    public org.springframework.http.ResponseEntity<?> siCreate(
            javax.servlet.http.HttpServletRequest request,
            @Valid @RequestBody SIBLRequestDTO body) {

        try {
            // 1) Extract JWT token
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }

            // 2) Extract customerCode from JWT
            String customerCodeFromJwt = customerContext.extractCustomerCodeFromToken(token);
            if (customerCodeFromJwt == null || customerCodeFromJwt.trim().isEmpty()) {
                return unauthorized("Customer code missing in JWT");
            }

            // 2.5) Extract login name from JWT
            String loginNameFromJwt = customerContext.extractLoginFromToken(token);
            if (loginNameFromJwt == null || loginNameFromJwt.trim().isEmpty()) {
                return unauthorized("Login name missing in JWT");
            }

            // 3) Validate request presence
            if (body == null) {
                return badRequest(null, null, "Request body cannot be null");
            }

            // 4) Align or validate customerCode
            if (body.getCustomerCode() == null || body.getCustomerCode().trim().isEmpty()) {
                body.setCustomerCode(customerCodeFromJwt);
            } else if (!customerCodeFromJwt.equalsIgnoreCase(body.getCustomerCode().trim())) {
                return forbidden("Customer code mismatch between JWT and payload");
            }

            // 5) Perform creation via service with validation
            logger.info("[CONTROLLER] Calling repo.createSi() for customer: {}", customerCodeFromJwt);
            SICreateResponse result = repo.createSi(customerCodeFromJwt, loginNameFromJwt, body);
            logger.info("[CONTROLLER] SI created with number: {}", result != null ? result.getOnlineSINo() : null);

            // 5.5) Export to EDI temp tables only when status is Submit ('S') and sino length check (sino present)
            // Per spec: insert in ediexport when status is 'S' only and sino length check
            String bookNoForEdi = (result != null && result.getOnlineSINo() != null && !result.getOnlineSINo().trim().isEmpty())
                    ? result.getOnlineSINo() : body.getOnlineSINo();
            boolean statusIsSubmit = body.getStatus() != null && body.getStatus() == com.svm.ecom.common.enums.SiStatus.S;
            boolean sinoPresent = bookNoForEdi != null && !bookNoForEdi.trim().isEmpty();
            if (statusIsSubmit && sinoPresent) {
                try {
                    Map<String, Object> ediResult = ediExportService.exportToEdiTempTables(
                            bookNoForEdi, body, loginNameFromJwt);
                    if (Boolean.TRUE.equals(ediResult.get("success"))) {
                        logger.info("EDI export for SI {}: {}", bookNoForEdi, ediResult.get("message"));
                    } else {
                        logger.warn("EDI export for SI {} failed: {}", bookNoForEdi, ediResult.get("message"));
                    }
                } catch (Exception e) {
                    logger.error("Failed to export SI {} to EDI temp tables: {}", bookNoForEdi, e.getMessage(), e);
                }
            } else if (!statusIsSubmit) {
                logger.debug("EDI export skipped: status is not Submit (S), current status={}", body.getStatus() != null ? body.getStatus().getValue() : null);
            } else if (!sinoPresent) {
                logger.debug("EDI export skipped: SI number (sino) not present or empty");
            }

            // 6) Build Location header
            HttpHeaders headers = new HttpHeaders();
            if (result != null && result.getOnlineSINo() != null && !result.getOnlineSINo().trim().isEmpty()) {
                headers.setLocation(URI.create("/api/v1/bill-of-lading/" + result.getOnlineSINo().trim()));
            }
            if (result != null && (result.getMessage() == null || result.getMessage().trim().isEmpty())) {
                result.setMessage("SI created successfully");
            }
            return new ResponseEntity<>(result, headers, HttpStatus.CREATED);

        } catch (SecurityException e) {
            logger.error("Security exception during SI creation", e);
            return unauthorized("Invalid JWT token or authentication failed");
        } catch (com.solverminds.sibl.exception.SIValidationException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error during SI creation: {}", e.getMessage());
            return badRequest(null, null, e.getMessage());
        } catch (IllegalStateException ex) {
            logger.error("Service unavailable during SI creation", ex);
            return svcUnavailable();
        } catch (Exception e) {
            logger.error("Unexpected error occurred while creating SI", e);
            return internalError("An unexpected error occurred while creating SI: " + e.getMessage());
        }
    }

    // ---- SI Update -----------------------------------------------------------
    @Operation(
        summary = SwaggerDocs.Sibl.SUMMARY_SI_UPDATE,
        description = SwaggerDocs.Sibl.DESC_SI_UPDATE,
        tags = {"SIBL Service"}
    )
    @Parameter(
        name = "Authorization",
        description = "JWT Bearer token for authentication",
        required = true,
        in = ParameterIn.HEADER,
        schema = @Schema(type = "string"),
        example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = SwaggerDocs.Common.SUCCESS_200,
            content = @Content(schema = @Schema(implementation = SICreateResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = SwaggerDocs.Common.BAD_REQUEST_400,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = SwaggerDocs.Common.UNAUTHORIZED_401,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = SwaggerDocs.Common.FORBIDDEN_403,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = SwaggerDocs.Common.NOT_FOUND_404,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = SwaggerDocs.Common.SERVER_ERROR_500,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "503",
            description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "ServiceUnavailable", value = SwaggerDocs.Examples.ERROR_SERVICE_UNAVAILABLE))
        )
    })
    @PutMapping(
        path = "/siupdate/{onlineSINo}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> siUpdate(
            HttpServletRequest request,
            @Parameter(description = "Online SI Number", required = true, example = "SIREQ2826")
            @PathVariable("onlineSINo") String onlineSINo,
            @Valid @RequestBody SIBLRequestDTO body) {

        try {
            // 1) JWT
            String token = extractJwtToken(request.getHeader("Authorization"));
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }

            // 2) Customer scope
            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            if (customerCode == null || customerCode.trim().isEmpty()) {
                return unauthorized("Customer code missing in JWT");
            }

            String loginNameFromJwt = customerContext.extractLoginFromToken(token);
            if (loginNameFromJwt == null || loginNameFromJwt.trim().isEmpty()) {
                loginNameFromJwt = customerCode;
            }

            if (onlineSINo == null || onlineSINo.trim().isEmpty()) {
                return badRequest("onlineSINo", null, "onlineSINo is required");
            }
            if (body == null) {
                return badRequest(null, null, "Request body cannot be null");
            }

            // 2.5) Align or validate customerCode (same as SI create)
            if (body.getCustomerCode() == null || body.getCustomerCode().trim().isEmpty()) {
                body.setCustomerCode(customerCode);
            } else if (!customerCode.equalsIgnoreCase(body.getCustomerCode().trim())) {
                return forbidden("Customer code mismatch between JWT and payload");
            }

            // 3) Repo call (validates SI number in ecomSI; service-level validation via SIValidationService; Draft=D update in place, Submit=S delete+insert newly)
            SICreateResponse result = repo.updateSi(customerCode, loginNameFromJwt, onlineSINo.trim(), body);

            // 4) EDI export when status is Submit ('S'): same as booking create run full EDI export so editempbooking and child tables are inserted.
            boolean statusIsSubmit = body.getStatus() != null && body.getStatus() == com.svm.ecom.common.enums.SiStatus.S;
            boolean sinoPresent = onlineSINo != null && !onlineSINo.trim().isEmpty();
            if (statusIsSubmit && sinoPresent) {
                try {
                    ediExportService.updateEdiDocStatusToV(onlineSINo.trim());
                    Map<String, Object> ediResult = ediExportService.exportToEdiTempTables(
                            onlineSINo.trim(), body, loginNameFromJwt);
                    if (Boolean.TRUE.equals(ediResult.get("success"))) {
                        logger.info("EDI export for SI update {}: {}", onlineSINo, ediResult.get("message"));
                    } else {
                        logger.warn("EDI export for SI update {} failed: {}", onlineSINo, ediResult.get("message"));
                    }
                } catch (Exception e) {
                    logger.error("Failed to export SI {} to EDI temp tables after update: {}", onlineSINo, e.getMessage(), e);
                }
            }

            // 5) Response
            if (result != null && (result.getMessage() == null || result.getMessage().trim().isEmpty())) {
                result.setMessage("SI updated");
            }
            return ResponseEntity.ok(result);

        } catch (SecurityException e) {
            return unauthorized("Invalid JWT token or authentication failed");
        } catch (com.solverminds.sibl.exception.SIValidationException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("SI_NOT_FOUND:")) {
                return notFound("SI not found: " + onlineSINo);
            }
            return badRequest(null, null, e.getMessage());
        } catch (IllegalStateException ex) {
            return svcUnavailable();
        } catch (Exception e) {
            return internalError("An unexpected error occurred while updating SI");
        }
    }

    // ---- BL Update (composite path: {blNumber,onlineSINo}) -------------------
    @Operation(
        summary = SwaggerDocs.Sibl.SUMMARY_BL_UPDATE,
        description = SwaggerDocs.Sibl.DESC_BL_UPDATE,
        tags = {"SIBL Service"}
    )
    @Parameter(
        name = "Authorization",
        description = "JWT Bearer token for authentication",
        required = true,
        in = ParameterIn.HEADER,
        schema = @Schema(type = "string"),
        example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = SwaggerDocs.Common.SUCCESS_200,
            content = @Content(schema = @Schema(implementation = SICreateResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = SwaggerDocs.Common.BAD_REQUEST_400,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = SwaggerDocs.Common.UNAUTHORIZED_401,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = SwaggerDocs.Common.FORBIDDEN_403,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = SwaggerDocs.Common.NOT_FOUND_404,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = SwaggerDocs.Common.SERVER_ERROR_500,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "503",
            description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "ServiceUnavailable", value = SwaggerDocs.Examples.ERROR_SERVICE_UNAVAILABLE))
        )
    })
    @PutMapping(
        path = "/blupdate/{path}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> blUpdate(
            HttpServletRequest request,
            @Parameter(
                description = "Composite path segment in the form 'BL_NUMBER' or 'BL_NUMBER,ONLINE_SI_NO'. "
                            + "Examples: 'BL20240115001' or 'BL20240115001,SIREQ2826'.",
                required = true,
                example = "BL20240115001,SIREQ2826"
            )
            @PathVariable("path") String path,
            @Valid @RequestBody SIBLRequestDTO body) {

        try {
            // 1) JWT
            String token = extractJwtToken(request.getHeader("Authorization"));
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }

            // 2) Customer scope
            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            if (customerCode == null || customerCode.trim().isEmpty()) {
                return unauthorized("Customer code missing in JWT");
            }

            // 3) Parse composite path: BL required, SI optional
            if (path == null || path.trim().isEmpty()) {
                return badRequest("path", null, "Composite path segment is required");
            }
            String trimmed = path.trim();
            String blNumber = null;
            String onlineSINo = null;

            int commaIdx = trimmed.indexOf(',');
            if (commaIdx >= 0) {
                blNumber   = trimmed.substring(0, commaIdx).trim();
                onlineSINo = trimmed.substring(commaIdx + 1).trim();
                if (onlineSINo.isEmpty()) {
                    onlineSINo = null; // treat trailing comma as missing SI
                }
            } else {
                blNumber = trimmed;
            }

            if (blNumber == null || blNumber.isEmpty()) {
                return badRequest("blNumber", null, "blNumber is required in the composite path");
            }
            if (body == null) {
                return badRequest(null, null, "Request body cannot be null");
            }

            // 4) Repo call (onlineSINo may be null)
            SICreateResponse result = repo.updateBl(customerCode, blNumber, onlineSINo, body);

            // 5) Response
            if (result != null && (result.getMessage() == null || result.getMessage().trim().isEmpty())) {
                result.setMessage("BL updated");
            }
            return ResponseEntity.ok(result);

        } catch (SecurityException e) {
            return unauthorized("Invalid JWT token or authentication failed");
        } catch (IllegalStateException ex) {
            return svcUnavailable();
        } catch (Exception e) {
            return internalError("An unexpected error occurred while updating BL");
        }
    }

    // ---- SI / Bill-of-Lading View Endpoint --------------------------------------
    @Operation(
        summary = SwaggerDocs.Sibl.SUMMARY_SI_VIEW,
        description = SwaggerDocs.Sibl.DESC_SI_VIEW,
        tags = {"SIBL Service"}
    )
    @Parameter(
        name = "Authorization",
        description = "JWT Bearer token for authentication",
        required = true,
        in = ParameterIn.HEADER,
        schema = @Schema(type = "string"),
        example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = SwaggerDocs.Common.SUCCESS_200,
            content = @Content(schema = @Schema(implementation = SIBLResponseDTO.class),
                examples = @ExampleObject(
                    name = "Sample View Response",
                    value = SIExamples.VIEW_RESPONSE_200
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = SwaggerDocs.Sibl.BAD_REQUEST_VIEW_400,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = SwaggerDocs.Common.UNAUTHORIZED_401,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = SwaggerDocs.Common.FORBIDDEN_403,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = SwaggerDocs.Sibl.NOT_FOUND_VIEW_404,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = SwaggerDocs.Common.SERVER_ERROR_500,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "503",
            description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "ServiceUnavailable", value = SwaggerDocs.Examples.ERROR_SERVICE_UNAVAILABLE))
        )
    })
    
    @GetMapping(path = "/view", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> siView(
            @Parameter(description = "Booking number (same data as booking view)", required = false, example = "BKG20240115001")
            @RequestParam(value = "bookingNo", required = false) String bookingNo,
            @Parameter(description = "Online SI Number (fetch from ecomSI)", required = false, example = "SIREQ2826")
            @RequestParam(value = "onlineSINo", required = false) String onlineSINo,
            @Parameter(description = "Bill of Lading number (fetch from bl table)", required = false, example = "BL20240115001")
            @RequestParam(value = "blNumber", required = false) String blNumber,
            HttpServletRequest request) {

        try {
            logger.info("Bill-of-lading view called - bookingNo: {}, onlineSINo: {}, blNumber: {}", bookingNo, onlineSINo, blNumber);

            String token = extractJwtToken(request.getHeader("Authorization"));
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }

            // 2) Customer scope
            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            if (customerCode == null || customerCode.trim().isEmpty()) {
                return unauthorized("Customer code missing in JWT");
            }

            boolean hasBookingNo = bookingNo != null && !bookingNo.trim().isEmpty();
            boolean hasOnlineSINo = onlineSINo != null && !onlineSINo.trim().isEmpty();
            boolean hasBlNumber = blNumber != null && !blNumber.trim().isEmpty();
            if (!hasBookingNo && !hasOnlineSINo && !hasBlNumber) {
                return badRequest("bookingNo/onlineSINo/blNumber", null,
                    "At least one of bookingNo, onlineSINo, or blNumber is required");
            }

            Optional<SIBLRequestDTO> result = repo.viewBillOfLading(customerCode,
                    safeTrim(bookingNo), safeTrim(onlineSINo), safeTrim(blNumber));

            if (!result.isPresent()) {
                logger.warn("Bill-of-lading not found - customerCode: {}, bookingNo: {}, onlineSINo: {}, blNumber: {}",
                        customerCode, bookingNo, onlineSINo, blNumber);
                return notFound("Bill-of-lading not found for given parameters");
            }

            SIBLResponseDTO response = SIBLResponseDTO.from(result.get());
            return ResponseEntity.ok(response);

        } catch (SecurityException e) {
            return unauthorized("Invalid JWT token or authentication failed");
        } catch (IllegalStateException ex) {
            return svcUnavailable();
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching bill-of-lading", e);
            return internalError("An unexpected error occurred while fetching the bill-of-lading");
        }
    }

    // ---- Document Upload Endpoint -------------------------------------------
    @Operation(
        summary = SwaggerDocs.Sibl.SUMMARY_DOC_UPLOAD,
        description = SwaggerDocs.Sibl.DESC_DOC_UPLOAD,
        tags = {"SIBL Service"},
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(
                    schema = @Schema(implementation = DocumentUploadResponse.class),
                    examples = @ExampleObject(
                        name = "Success",
                        value = "[\n" +
                            "  {\n" +
                            "    \"fileName\": \"invoice.pdf\",\n" +
                            "    \"sizeBytes\": 123456\n" +
                            "  },\n" +
                            "  {\n" +
                            "    \"fileName\": \"rejected.exe\",\n" +
                            "    \"error\": \"File type not allowed: exe\"\n" +
                            "  }\n" +
                            "]"
                    )
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.Common.BAD_REQUEST_400,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = SwaggerDocs.Common.NOT_FOUND_404,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "Conflict - File already exists and overwrite=false",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Conflict", value = SwaggerDocs.Examples.ERROR_VALIDATION))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "503",
                description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "ServiceUnavailable", value = SwaggerDocs.Examples.ERROR_SERVICE_UNAVAILABLE))
            )
        }
    )
    @PostMapping(
        path = "/{onlineSINo}/documents",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> uploadDocuments(
            HttpServletRequest request,
            @PathVariable("onlineSINo") String onlineSINo,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "overwrite", defaultValue = "false") boolean overwrite) {
        try {
            // 1) JWT authentication
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }

            // 2) Extract customerCode from JWT
            String customerCodeFromJwt = customerContext.extractCustomerCodeFromToken(token);
            if (customerCodeFromJwt == null || customerCodeFromJwt.trim().isEmpty()) {
                return unauthorized("Customer code missing in JWT");
            }

            // 3) Validate onlineSINo
            if (onlineSINo == null || onlineSINo.trim().isEmpty()) {
                return badRequest("onlineSINo", onlineSINo, "Online SI number cannot be empty");
            }

            // 4) Upload documents
            List<DocumentUploadResponse> responses = documentService.uploadDocuments(
                onlineSINo.trim(), files, overwrite
            );

            // 5) Check if any files had errors
            boolean hasErrors = responses.stream().anyMatch(r -> r.getError() != null);
            if (hasErrors) {
                logger.warn("Some files failed validation during upload for online SI number: {}", onlineSINo);
            }

            return ResponseEntity.ok(responses);

        } catch (IllegalArgumentException e) {
            // Validation errors (file count, size, type, duplicate names, booking not found)
            if (e.getMessage().contains("not found")) {
                return notFound(e.getMessage());
            } else if (e.getMessage().contains("already exists")) {
                return conflict(e.getMessage());
            } else {
                return badRequest(null, null, e.getMessage());
            }
        } catch (IllegalStateException e) {
            // File exists and overwrite=false, or folder not found
            if (e.getMessage().contains("already exists")) {
                return conflict(e.getMessage());
            } else if (e.getMessage().contains("not found")) {
                return notFound(e.getMessage());
            } else {
                return internalError(e.getMessage());
            }
        } catch (SecurityException e) {
            return unauthorized("Invalid JWT token or authentication failed");
        } catch (Exception e) {
            logger.error("Unexpected error during document upload for online SI number: {}", onlineSINo, e);
            return internalError("An unexpected error occurred while uploading documents");
        }
    }

    // ---- Document Listing Endpoint -------------------------------------------
    @Operation(
        summary = SwaggerDocs.Sibl.SUMMARY_DOC_LIST,
        description = SwaggerDocs.Sibl.DESC_DOC_LIST,
        tags = {"SIBL Service"},
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(
                    schema = @Schema(implementation = DocumentListResponse.class),
                    examples = {
                        @ExampleObject(
                            name = "WithoutChecksum",
                            description = "Default response (includeChecksum=false)",
                            value = "[\n" +
                                "  {\n" +
                                "    \"fileName\": \"invoice.pdf\",\n" +
                                "    \"sizeBytes\": 123456,\n" +
                                "    \"lastModified\": \"2025-10-22T10:30:00\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"fileName\": \"packing_list.xlsx\",\n" +
                                "    \"sizeBytes\": 89012,\n" +
                                "    \"lastModified\": \"2025-10-22T11:15:00\"\n" +
                                "  }\n" +
                                "]"
                        ),
                        @ExampleObject(
                            name = "WithChecksum",
                            description = "With checksum (includeChecksum=true)",
                            value = "[\n" +
                                "  {\n" +
                                "    \"fileName\": \"invoice.pdf\",\n" +
                                "    \"sizeBytes\": 123456,\n" +
                                "    \"lastModified\": \"2025-10-22T10:30:00\",\n" +
                                "    \"checksum\": \"sha256:4c75e52322464a4426dcf38fb0245ab63ac80d046024b0e62ac756aacc5eaff9\"\n" +
                                "  }\n" +
                                "]"
                        ),
                        @ExampleObject(
                            name = "Empty",
                            description = "No documents uploaded yet",
                            value = "[]"
                        )
                    }
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.Common.BAD_REQUEST_400,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403",
                description = SwaggerDocs.Common.FORBIDDEN_403,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = SwaggerDocs.Common.NOT_FOUND_404,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "503",
                description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "ServiceUnavailable", value = SwaggerDocs.Examples.ERROR_SERVICE_UNAVAILABLE))
            )
        }
    )
    @GetMapping(
        path = "/{onlineSINo}/documents",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> listDocuments(
            HttpServletRequest request,
            @PathVariable("onlineSINo") String onlineSINo,
            @RequestParam(value = "includeChecksum", defaultValue = "false") boolean includeChecksum) {
        try {
            // 1) JWT authentication
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }

            // 2) Extract customerCode from JWT
            String customerCodeFromJwt = customerContext.extractCustomerCodeFromToken(token);
            if (customerCodeFromJwt == null || customerCodeFromJwt.trim().isEmpty()) {
                return unauthorized("Customer code missing in JWT");
            }

            // 3) Validate onlineSINo
            if (onlineSINo == null || onlineSINo.trim().isEmpty()) {
                return badRequest("onlineSINo", onlineSINo, "Online SI number cannot be empty");
            }

            // 4) List documents
            List<DocumentListResponse> documents = documentService.listDocuments(onlineSINo.trim(), includeChecksum);

            return ResponseEntity.ok(documents);

        } catch (IllegalArgumentException e) {
            // Booking not found or folder not found
            if (e.getMessage().contains("not found")) {
                return notFound(e.getMessage());
            } else {
                return badRequest(null, null, e.getMessage());
            }
        } catch (SecurityException e) {
            return unauthorized("Invalid JWT token or authentication failed");
        } catch (Exception e) {
            logger.error("Unexpected error during document listing for online SI number: {}", onlineSINo, e);
            return internalError("An unexpected error occurred while listing documents");
        }
    }

    // ---- Document Download/Preview Endpoint ----------------------------------
    @Operation(
        summary = SwaggerDocs.Sibl.SUMMARY_DOC_DOWNLOAD,
        description = SwaggerDocs.Sibl.DESC_DOC_DOWNLOAD,
        tags = {"SIBL Service"},
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = {
                    @Content(
                        mediaType = "application/pdf",
                        examples = @ExampleObject(
                            name = "PDF",
                            description = "PDF file - previews in browser",
                            value = "[Binary PDF content]"
                        )
                    ),
                    @Content(
                        mediaType = "image/jpeg",
                        examples = @ExampleObject(
                            name = "Image",
                            description = "Image file - displays in browser",
                            value = "[Binary image content]"
                        )
                    ),
                    @Content(
                        mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        examples = @ExampleObject(
                            name = "Excel",
                            description = "Excel file - downloads to disk",
                            value = "[Binary Excel content]"
                        )
                    )
                }
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.Common.BAD_REQUEST_400,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403",
                description = SwaggerDocs.Common.FORBIDDEN_403,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = SwaggerDocs.Common.NOT_FOUND_404,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "503",
                description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "ServiceUnavailable", value = SwaggerDocs.Examples.ERROR_SERVICE_UNAVAILABLE))
            )
        }
    )
    @GetMapping(
        path = "/{onlineSINo}/documents/{fileName}",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<?> downloadDocument(
            HttpServletRequest request,
            @PathVariable("onlineSINo") String onlineSINo,
            @PathVariable("fileName") String fileName) {
        try {
            // 1) JWT authentication
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                return unauthorized("Missing or invalid Authorization header");
            }

            // 2) Extract customerCode from JWT
            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            if (customerCode == null || customerCode.trim().isEmpty()) {
                return unauthorized("Customer code not found in token");
            }

            logger.info("Download request - OnlineSINo: {}, File: {}, Customer: {}", 
                onlineSINo, fileName, customerCode);

            // 3) Get file from service
            File file = documentService.downloadDocument(onlineSINo.trim(), fileName);

            // 4) Determine content type
            String contentType = determineContentType(fileName);

            // 5) Create resource
            Resource resource = new FileSystemResource(file);

            // 6) Set headers for inline display (preview) or download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(file.length());
            
            // For PDFs, images, etc., use inline to preview in browser
            if (isPreviewable(contentType)) {
                headers.setContentDisposition(ContentDisposition.inline().filename(fileName).build());
            } else {
                // For other files, force download
                headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
            }

            return ResponseEntity.ok()
                .headers(headers)
                .body(resource);

        } catch (IllegalArgumentException e) {
            // File not found or validation error
            if (e.getMessage().contains("not found")) {
                return notFound(e.getMessage());
            } else {
                return badRequest(null, null, e.getMessage());
            }
        } catch (SecurityException e) {
            return unauthorized("Invalid JWT token or authentication failed");
        } catch (Exception e) {
            logger.error("Unexpected error during document download - OnlineSINo: {}, File: {}", 
                onlineSINo, fileName, e);
            return internalError("An unexpected error occurred while downloading the document");
        }
    }

    // ---- Error Helper Methods (following BookingController pattern) ---------

    /**
     * Create a proper ErrorResponse for 400 Bad Request (validation)
     */
    private ResponseEntity<ErrorResponse> badRequest(String field, String value, String message) {
        ErrorResponse errorResponse = new ErrorResponse(400, "Bad Request", LocalDateTime.now());
        errorResponse.addError(ErrorCodes.VALIDATION_GENERIC, field, value, ErrorReason.VALIDATION_ERROR,
            message != null ? message : "Invalid input");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 400 Bad Request with custom error code
     */
    private ResponseEntity<ErrorResponse> badRequest(int errorCode, String field, String value, String message) {
        ErrorResponse errorResponse = new ErrorResponse(400, "Bad Request", LocalDateTime.now());
        errorResponse.addError(errorCode, field, value, ErrorReason.VALIDATION_ERROR,
            message != null ? message : "Invalid input");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 401 Unauthorized errors
     */
    private ResponseEntity<ErrorResponse> unauthorized(String message) {
        ErrorResponse errorResponse = new ErrorResponse(401, "Unauthorized", LocalDateTime.now());
        errorResponse.addError(ErrorCodes.UNAUTHORIZED, null, null, ErrorReason.UNAUTHORIZED,
            message != null ? message : "Authentication required or invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 403 Forbidden
     */
    private ResponseEntity<ErrorResponse> forbidden(String message) {
        ErrorResponse errorResponse = new ErrorResponse(403, "Forbidden", LocalDateTime.now());
        errorResponse.addError(ErrorCodes.FORBIDDEN, null, null, ErrorReason.FORBIDDEN,
            message != null ? message : "Access denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 404 Not Found
     */
    private ResponseEntity<ErrorResponse> notFound(String message) {
        ErrorResponse errorResponse = new ErrorResponse(404, "Not Found", LocalDateTime.now());
        errorResponse.addError(ErrorCodes.NOT_FOUND, null, null, ErrorReason.NOT_FOUND,
            message != null ? message : "Requested resource not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 500 Internal Server Error
     */
    private ResponseEntity<ErrorResponse> internalError(String message) {
        ErrorResponse errorResponse = new ErrorResponse(500, "Internal Server Error", LocalDateTime.now());
        errorResponse.addError(ErrorCodes.INTERNAL_ERROR, null, null, ErrorReason.INTERNAL_ERROR,
            message != null ? message : "An unexpected server error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 503 Service Unavailable errors
     */
    private ResponseEntity<ErrorResponse> svcUnavailable() {
        ErrorResponse errorResponse = new ErrorResponse(503, "Service Unavailable", LocalDateTime.now());
        errorResponse.addError(ErrorCodes.SERVICE_UNAVAILABLE, null, null, ErrorReason.SERVICE_UNAVAILABLE,
            "Report database is currently unavailable");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
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

    private String safeTrim(String v) {
        return v == null ? null : v.trim();
    }

    /**
     * Create a proper ErrorResponse for 409 Conflict
     */
    private ResponseEntity<ErrorResponse> conflict(String message) {
        ErrorResponse errorResponse = new ErrorResponse(409, "Conflict", LocalDateTime.now());
        errorResponse.addError(ErrorCodes.CONFLICT, null, null, ErrorReason.CONFLICT,
            message != null ? message : "Resource conflict");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Determine content type based on file extension
     */
    private String determineContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        
        switch (extension) {
            // Documents
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            
            // Images
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
            
            // Text
            case "txt":
                return "text/plain";
            case "csv":
                return "text/csv";
            case "xml":
                return "application/xml";
            case "json":
                return "application/json";
            
            // Archives
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            case "7z":
                return "application/x-7z-compressed";
            
            default:
                return "application/octet-stream";
        }
    }

    /**
     * Check if content type can be previewed inline in browser
     */
    private boolean isPreviewable(String contentType) {
        return contentType.startsWith("image/") ||
               contentType.equals("application/pdf") ||
               contentType.startsWith("text/");
    }

}