package com.svm.ecom.master.controller;

import com.svm.ecom.dto.ErrorMessage;
import com.svm.ecom.dto.NestedErrorMessage;
import com.svm.ecom.docs.SwaggerDocs;
import com.svm.ecom.master.dto.v2.AgencyResponse;
import com.svm.ecom.master.dto.v2.CommodityResponse;
import com.svm.ecom.master.dto.v2.CustomerResponse;
import com.svm.ecom.master.dto.v2.DgPackagingResponse;
import com.svm.ecom.master.dto.v2.ImdgEntryResponse;
import com.svm.ecom.master.security.CustomerContext;
import com.svm.ecom.master.service.AgencyMasterV2Service;
import com.svm.ecom.master.service.CommodityMasterV2Service;
import com.svm.ecom.master.service.CustomerMasterV2Service;
import com.svm.ecom.master.service.DgPackagingMasterV2Service;
import com.svm.ecom.master.service.ImdgMasterV2Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Controller for Master Data API v2 (Customer, Agency, Commodity).
 * Documentation format and wording match client request standard.
 */
@RestController
@RequestMapping("/api/v2/master")
@Tag(name = SwaggerDocs.MasterV2.TAG_NAME, description = SwaggerDocs.MasterV2.TAG_DESCRIPTION)
public class MasterV2Controller {

    private static final Logger logger = LoggerFactory.getLogger(MasterV2Controller.class);

    @Autowired
    private CustomerMasterV2Service customerMasterV2Service;

    @Autowired
    private CustomerContext customerContext;

    @Autowired
    private AgencyMasterV2Service agencyMasterV2Service;

    @Autowired
    private CommodityMasterV2Service commodityMasterV2Service;

    @Autowired
    private DgPackagingMasterV2Service dgPackagingMasterV2Service;

    @Autowired
    private ImdgMasterV2Service imdgMasterV2Service;

    private static final Pattern UN_NO_PATTERN = Pattern.compile("^\\d{4}$");

    @GetMapping("/customers")
    @Operation(
        summary = SwaggerDocs.MasterV2.SUMMARY_GET_CUSTOMERS,
        description = SwaggerDocs.MasterV2.DESC_GET_CUSTOMERS,
        operationId = "getCustomers",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.MasterV2.RESPONSE_200_CUSTOMERS,
                content = @Content(
                    array = @ArraySchema(
                        arraySchema = @Schema(name = "CustomerList", description = "A list of customers"),
                        schema = @Schema(implementation = CustomerResponse.class)
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad Request - Invalid request parameters or date format",
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "ValidationError",
                        value = SwaggerDocs.Examples.ERROR_V2_VALIDATION
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.MasterV2.RESPONSE_401,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Unauthorized",
                        value = SwaggerDocs.Examples.ERROR_V2_UNAUTHORIZED
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = SwaggerDocs.MasterV2.RESPONSE_403,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Forbidden",
                        value = SwaggerDocs.Examples.ERROR_V2_FORBIDDEN
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.MasterV2.RESPONSE_500,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "InternalError",
                        value = SwaggerDocs.Examples.ERROR_V2_INTERNAL
                    )
                )
            ),
            @ApiResponse(
                responseCode = "503",
                description = SwaggerDocs.MasterV2.RESPONSE_503,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "ServiceUnavailable",
                        value = SwaggerDocs.Examples.ERROR_V2_SERVICE_UNAVAILABLE
                    )
                )
            )
        }
    )
    @Parameter(
        name = "updatedAt_after",
        description = "Filter results to match only items that were updated after the provided timestamp",
        required = false,
        example = "2026-01-01T00:00:00Z",
        schema = @Schema(type = "string", format = "date-time")
    )
    public ResponseEntity<?> getCustomers(
            HttpServletRequest request,
            @RequestParam(value = "updatedAt_after", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) 
            LocalDateTime updatedAt_after) {
        String clientIp = getClientIpAddress(request);
        logger.info("GET /api/v2/master/customers from IP: {}, updatedAt_after: {}", clientIp, updatedAt_after);
        try {
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                logger.warn("Missing or invalid JWT token from IP: {}", clientIp);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Missing or invalid JWT token");
            }

            String loginCustomerCode = customerContext.extractCustomerCodeOrNull(token);

            List<CustomerResponse> list;
            if (loginCustomerCode == null) {
                logger.info("Global auth token detected from IP: {}, returning all customers", clientIp);
                list = customerMasterV2Service.findAllCustomers(updatedAt_after);
            } else {
                logger.info("Customer auth token detected, login customer code: {} from IP: {}", loginCustomerCode, clientIp);
                list = customerMasterV2Service.findCustomersByLoginCustomer(loginCustomerCode, updatedAt_after);
            }

            logger.info("Returned {} customers from IP: {}", list.size(), clientIp);
            return ResponseEntity.ok(list);
        } catch (SecurityException e) {
            logger.warn("Authentication failed from IP: {} - {}", clientIp, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching customers from IP: {} - {}", clientIp, e.getMessage(), e);
            throw e;
        }
    }

    private String extractJwtToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    // ── Agencies ─────────────────────────────────────────────────────────────

    @GetMapping("/agencies")
    @Operation(
        summary = SwaggerDocs.MasterV2.SUMMARY_GET_AGENCIES,
        description = SwaggerDocs.MasterV2.DESC_GET_AGENCIES,
        operationId = "getAgencies",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.MasterV2.RESPONSE_200_AGENCIES,
                content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = AgencyResponse.class)),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "AgencyList",
                        value = SwaggerDocs.Examples.AGENCY_LIST_200
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.MasterV2.RESPONSE_400,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "ValidationError",
                        value = SwaggerDocs.Examples.ERROR_V2_AGENCY_VALIDATION
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.MasterV2.RESPONSE_401,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Unauthorized",
                        value = SwaggerDocs.Examples.ERROR_V2_UNAUTHORIZED
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = SwaggerDocs.MasterV2.RESPONSE_403,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Forbidden",
                        value = SwaggerDocs.Examples.ERROR_V2_FORBIDDEN
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.MasterV2.RESPONSE_500,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "InternalError",
                        value = SwaggerDocs.Examples.ERROR_V2_INTERNAL
                    )
                )
            ),
            @ApiResponse(
                responseCode = "503",
                description = SwaggerDocs.MasterV2.RESPONSE_503,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "ServiceUnavailable",
                        value = SwaggerDocs.Examples.ERROR_V2_SERVICE_UNAVAILABLE
                    )
                )
            )
        }
    )
    public ResponseEntity<List<AgencyResponse>> getAgencies(HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        logger.info("GET /api/v2/master/agencies from IP: {}", clientIp);
        try {
            List<AgencyResponse> list = agencyMasterV2Service.findAllAgencies();
            logger.info("Returned {} agencies to IP: {}", list.size(), clientIp);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.error("Error fetching agencies from IP: {} - {}", clientIp, e.getMessage(), e);
            throw e;
        }
    }

    // ── Commodities ─────────────────────────────────────────────────────────

    @GetMapping("/commodities")
    @Operation(
        summary = SwaggerDocs.MasterV2.SUMMARY_GET_COMMODITIES,
        description = SwaggerDocs.MasterV2.DESC_GET_COMMODITIES,
        operationId = "getCommodities",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.MasterV2.RESPONSE_200_COMMODITIES,
                content = @Content(
                    array = @ArraySchema(
                        arraySchema = @Schema(name = "CommodityList", description = "A list of commodities"),
                        schema = @Schema(implementation = CommodityResponse.class)
                    ),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "CommodityList",
                        value = SwaggerDocs.Examples.COMMODITY_LIST_200
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.MasterV2.RESPONSE_400,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "ValidationError",
                        value = SwaggerDocs.Examples.ERROR_V2_COMMODITY_VALIDATION
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.MasterV2.RESPONSE_401,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Unauthorized",
                        value = SwaggerDocs.Examples.ERROR_V2_UNAUTHORIZED
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = SwaggerDocs.MasterV2.RESPONSE_403,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Forbidden",
                        value = SwaggerDocs.Examples.ERROR_V2_FORBIDDEN
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.MasterV2.RESPONSE_500,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "InternalError",
                        value = SwaggerDocs.Examples.ERROR_V2_INTERNAL
                    )
                )
            ),
            @ApiResponse(
                responseCode = "503",
                description = SwaggerDocs.MasterV2.RESPONSE_503,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "ServiceUnavailable",
                        value = SwaggerDocs.Examples.ERROR_V2_SERVICE_UNAVAILABLE
                    )
                )
            )
        }
    )
    public ResponseEntity<List<CommodityResponse>> getCommodities(HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        logger.info("GET /api/v2/master/commodities from IP: {}", clientIp);
        try {
            List<CommodityResponse> list = commodityMasterV2Service.findAllCommodities();
            logger.info("Returned {} commodities to IP: {}", list.size(), clientIp);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.error("Error fetching commodities from IP: {} - {}", clientIp, e.getMessage(), e);
            throw e;
        }
    }

    // ── DG Packagings ───────────────────────────────────────────────────────

    @GetMapping("/dg-packagings")
    @Operation(
        summary = SwaggerDocs.MasterV2.SUMMARY_GET_DG_PACKAGINGS,
        description = SwaggerDocs.MasterV2.DESC_GET_DG_PACKAGINGS,
        operationId = "getDgPackagings",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.MasterV2.RESPONSE_200_DG_PACKAGINGS,
                content = @Content(
                    array = @ArraySchema(
                        arraySchema = @Schema(name = "DgPackagingList", description = "A list of DG packagings"),
                        schema = @Schema(implementation = DgPackagingResponse.class)
                    ),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "DgPackagingList",
                        value = SwaggerDocs.Examples.DG_PACKAGING_LIST_200
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.MasterV2.RESPONSE_400,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "ValidationError",
                        value = SwaggerDocs.Examples.ERROR_V2_DG_PACKAGING_VALIDATION
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.MasterV2.RESPONSE_401,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Unauthorized",
                        value = SwaggerDocs.Examples.ERROR_V2_UNAUTHORIZED
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = SwaggerDocs.MasterV2.RESPONSE_403,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Forbidden",
                        value = SwaggerDocs.Examples.ERROR_V2_FORBIDDEN
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.MasterV2.RESPONSE_500,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "InternalError",
                        value = SwaggerDocs.Examples.ERROR_V2_INTERNAL
                    )
                )
            ),
            @ApiResponse(
                responseCode = "503",
                description = SwaggerDocs.MasterV2.RESPONSE_503,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "ServiceUnavailable",
                        value = SwaggerDocs.Examples.ERROR_V2_SERVICE_UNAVAILABLE
                    )
                )
            )
        }
    )
    public ResponseEntity<?> getDgPackagings(
            @Parameter(description = "Filter by exact packaging type: OUTER (O only), INNER (I only), or BOTH (B only)", example = "OUTER")
            @RequestParam(value = "usage", required = false) String usage,
            @Parameter(description = "Exact packaging code filter", example = "1G")
            @RequestParam(value = "code", required = false) String code,
            HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        String instance = "/api/v2/master/dg-packagings";
        logger.info("GET {} usage={}, code={} from IP: {}", instance, usage, code, clientIp);
        try {
            List<DgPackagingResponse> list = dgPackagingMasterV2Service.findAll(usage, code);
            logger.info("Returned {} DG packagings to IP: {}", list.size(), clientIp);
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid DG packaging request from IP: {} - {}", clientIp, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildValidationError(
                            instance,
                            "DG packaging data validation failed. Please review the request.",
                            e.getMessage(),
                            "usage",
                            "INVALID_PARAMETER"));
        } catch (Exception e) {
            logger.error("Error fetching DG packagings from IP: {} - {}", clientIp, e.getMessage(), e);
            throw e;
        }
    }

    // ── IMDG Entries ────────────────────────────────────────────────────────

    @GetMapping("/imdg-entries")
    @Operation(
        summary = SwaggerDocs.MasterV2.SUMMARY_GET_IMDG_ENTRIES,
        description = SwaggerDocs.MasterV2.DESC_GET_IMDG_ENTRIES,
        operationId = "getImdgEntries",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.MasterV2.RESPONSE_200_IMDG_ENTRIES,
                content = @Content(
                    array = @ArraySchema(
                        arraySchema = @Schema(name = "ImdgEntryList", description = "A list of IMDG entries"),
                        schema = @Schema(implementation = ImdgEntryResponse.class)
                    ),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "ImdgEntryList",
                        value = SwaggerDocs.Examples.IMDG_ENTRY_LIST_200
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.MasterV2.RESPONSE_400,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "ValidationError",
                        value = SwaggerDocs.Examples.ERROR_V2_IMDG_VALIDATION
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.MasterV2.RESPONSE_401,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Unauthorized",
                        value = SwaggerDocs.Examples.ERROR_V2_UNAUTHORIZED
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = SwaggerDocs.MasterV2.RESPONSE_403,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Forbidden",
                        value = SwaggerDocs.Examples.ERROR_V2_FORBIDDEN
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.MasterV2.RESPONSE_500,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "InternalError",
                        value = SwaggerDocs.Examples.ERROR_V2_INTERNAL
                    )
                )
            ),
            @ApiResponse(
                responseCode = "503",
                description = SwaggerDocs.MasterV2.RESPONSE_503,
                content = @Content(
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "ServiceUnavailable",
                        value = SwaggerDocs.Examples.ERROR_V2_SERVICE_UNAVAILABLE
                    )
                )
            )
        }
    )
    public ResponseEntity<?> getImdgEntries(
            @Parameter(description = "United Nations number (4 digits)", required = true, example = "2779")
            @RequestParam("unNo") String unNo,
            @Parameter(description = "Optional IMDG variant filter", example = "~")
            @RequestParam(value = "variant", required = false) String variant,
            HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        String instance = "/api/v2/master/imdg-entries";
        logger.info("GET {} unNo={}, variant={} from IP: {}", instance, unNo, variant, clientIp);

        if (unNo == null || !UN_NO_PATTERN.matcher(unNo.trim()).matches()) {
            logger.warn("Invalid unNo '{}' from IP: {}", unNo, clientIp);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildValidationError(
                            instance,
                            "IMDG data validation failed. Please review the request.",
                            "unNo is required and must be 4 digits.",
                            "unNo",
                            "DG_UNNO_INVALID"));
        }

        try {
            List<ImdgEntryResponse> list = imdgMasterV2Service.findByUnNo(unNo.trim(), variant);
            logger.info("Returned {} IMDG entries for unNo={} to IP: {}", list.size(), unNo, clientIp);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.error("Error fetching IMDG entries from IP: {} - {}", clientIp, e.getMessage(), e);
            throw e;
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private ErrorMessage buildValidationError(
            String instance, String detail, String nestedDetail, String parameter, String nestedCode) {
        ErrorMessage error = new ErrorMessage(detail, "Validation Failed", 400, null, instance, "VALIDATION_FAILED");
        NestedErrorMessage nested = new NestedErrorMessage();
        nested.setDetail(nestedDetail);
        nested.setParameter(parameter);
        nested.setCode(nestedCode);
        error.setErrors(Collections.singletonList(nested));
        return error;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

}
