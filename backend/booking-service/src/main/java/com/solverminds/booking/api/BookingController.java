package com.solverminds.booking.api;

// BookingSummaryDto import removed - using BookingSummaryResponseDto directly
import com.solverminds.booking.dto.BookingSummaryResponseDto;
import com.solverminds.booking.dto.BookingRequestDTO;
import com.solverminds.booking.dto.BookingCreateResponse;
import com.solverminds.booking.dto.DocumentUploadResponse;
import com.solverminds.booking.dto.DocumentListResponse;
import com.solverminds.booking.repo.BookingRepository;
import com.solverminds.booking.security.CustomerContext;
import com.solverminds.booking.service.BookingCreateService;
import com.solverminds.booking.service.BookingUpdateLinkedNumberValidationService;
import com.solverminds.booking.util.BookingCargoWeightNormalizer;
import com.solverminds.booking.service.DocumentService;
import com.solverminds.booking.service.EdiExportService;
import com.solverminds.booking.service.MasterDataCacheService;
import com.svm.ecom.common.util.PortLocalTimeUtil;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

// Swagger/OpenAPI imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

// Generic wrapper and Swagger alias imports
import com.solverminds.booking.cursor.SystemBookingListCursorCodec.InvalidCursorException;
import com.solverminds.booking.dto.BookingSummaryListResponse;
import com.solverminds.booking.dto.SystemBookingListResponse;
import com.solverminds.booking.service.SystemBookingListService;
import com.solverminds.booking.service.SystemBookingListService.SystemBookingListRejectedException;
import com.svm.ecom.constants.DateFormatConstants;
import com.svm.ecom.security.JwtUtil;

// Centralized documentation constants
import com.svm.ecom.docs.SwaggerDocs;
// Error response schema
import com.svm.ecom.dto.ErrorResponse;
import com.svm.ecom.error.ErrorCodes;
import com.svm.ecom.error.ErrorReason;
import com.svm.ecom.common.enums.BookingStatus;
import com.solverminds.booking.exception.BookingCancellationConflictException;
import com.solverminds.booking.exception.BookingNotFoundException;
import com.solverminds.booking.exception.InvalidStatusTransitionException;
import com.solverminds.booking.exception.LrpBookingLinkNotAllowedException;
import com.solverminds.booking.dto.validation.ValidationGroups;
import com.solverminds.booking.service.BookingViewLookupResult;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Booking", description = "Booking service endpoints for ecom booking summary")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookingRepository bookingRepository;
    private final CustomerContext customerContext;
    private final BookingCreateService bookingCreateService;
    private final DocumentService documentService;
    private final EdiExportService ediExportService;
    private final MasterDataCacheService masterDataCacheService;
    private final Validator validator;
    private final BookingUpdateLinkedNumberValidationService bookingUpdateLinkedNumberValidationService;
    private final SystemBookingListService systemBookingListService;
    private final JwtUtil jwtUtil;

    private static final DateTimeFormatter UTC_DATE_TIME =
            DateTimeFormatter.ofPattern(DateFormatConstants.UTC_DATE_TIME);

    public BookingController(BookingRepository bookingRepository,
                           CustomerContext customerContext,
                           BookingCreateService bookingCreateService,
                           DocumentService documentService,
                           EdiExportService ediExportService,
                           MasterDataCacheService masterDataCacheService,
                           Validator validator,
                           BookingUpdateLinkedNumberValidationService bookingUpdateLinkedNumberValidationService,
                           SystemBookingListService systemBookingListService,
                           JwtUtil jwtUtil) {
        this.bookingRepository = bookingRepository;
        this.customerContext = customerContext;
        this.bookingCreateService = bookingCreateService;
        this.documentService = documentService;
        this.ediExportService = ediExportService;
        this.masterDataCacheService = masterDataCacheService;
        this.validator = validator;
        this.bookingUpdateLinkedNumberValidationService = bookingUpdateLinkedNumberValidationService;
        this.systemBookingListService = systemBookingListService;
        this.jwtUtil = jwtUtil;
    }

    // ---- Summary Endpoint ----------------------------------------------------
    @Operation(
        summary = SwaggerDocs.Booking.SUMMARY_GET_SUMMARY,
        description = SwaggerDocs.Booking.DESC_GET_SUMMARY,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(schema = @Schema(implementation = BookingSummaryListResponse.class))
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
        }
    )
    @GetMapping("/booking/summary")
    public ResponseEntity<?> summary(HttpServletRequest request) {
        try {
            // Extract JWT token from Authorization header
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }
            
            // Extract customerCode from JWT token
            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            
            // Call the stored procedure: exec usp_getecombookingsummary with customer code
            List<BookingSummaryResponseDto> data = bookingRepository.getBookingSummary(customerCode);
            if (data != null && masterDataCacheService != null) {
                for (BookingSummaryResponseDto dto : data) {
                    if (dto.getDepartureDate() != null && dto.getOriginPortId() != null) {
                        try {
                            String tz = masterDataCacheService.getTimezoneOffset(dto.getOriginPortId());
                            tz = PortLocalTimeUtil.normalizeTimezoneValue(tz);
                            dto.setDepartureDate(PortLocalTimeUtil.labelWithOffset(dto.getDepartureDate(), tz));
                        } catch (Exception ignored) { }
                    }
                }
            }
            return ResponseEntity.ok(new BookingSummaryListResponse(data));
            
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


    // ---- Create Endpoint -----------------------------------------------------
    @Operation(
        summary = "Create booking",
        description = "Creates a new booking from the provided payload. Note: Reefer settings are nested under containers (when equipmentType='R'), and dangerous goods are nested under commodities (when applicable).",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BookingRequestDTO.class),
                examples = {
                    @ExampleObject(
                        name = "Minimal", 
                        summary = "Minimal booking request",
                        description = "Basic booking with required fields only",
                        value = com.solverminds.booking.api.BookingExamplesNew.MINIMAL_JSON
                    ),
                    @ExampleObject(
                        name = "MultipleContainers", 
                        summary = "Multiple containers (equipmentQty > 1)",
                        description = "Booking with equipmentQty=3 and NO containerNo. System auto-generates container numbers (20DCTBN1, 20DCTBN2, 20DCTBN3) in EDI tables. Sequence resets per equipment size. Commodities are replicated for each expanded container.",
                        value = com.solverminds.booking.api.BookingExamplesNew.MULTI_CONTAINER_JSON
                    ),
                    @ExampleObject(
                        name = "Full", 
                        summary = "Full booking request with nested data",
                        description = "Complete booking example showing reefer settings nested under containers and dangerous goods nested under commodities",
                        value = com.solverminds.booking.api.BookingExamplesNew.FULL_JSON
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = SwaggerDocs.Common.CREATED_201,
                content = @Content(schema = @Schema(implementation = BookingCreateResponse.class))
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
    @PostMapping(
        path = "/booking/create",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> create(
            HttpServletRequest request,
            @RequestBody BookingRequestDTO body) {
        try {
            // 1) Extract JWT
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

            // 3a) SI-aligned container cargoWeight from commodities when present (before Bean Validation)
            BookingCargoWeightNormalizer.apply(body);
            validateBookingRequestDto(body);

            // 4) Align or validate customerCode
            //    If request body has no customerCode, set it from JWT.
            //    If request body has customerCode and it differs from JWT, reject.
            if (body.getCustomerCode() == null || body.getCustomerCode().trim().isEmpty()) {
                body.setCustomerCode(customerCodeFromJwt);
            } else if (!customerCodeFromJwt.equalsIgnoreCase(body.getCustomerCode().trim())) {
                return forbidden("Customer code mismatch between JWT and payload");
            }
            //4a get the agency code from the request body
            if (body.getAgencyId() == null || body.getAgencyId().trim().isEmpty()) {
                String agencyCode = bookingCreateService.resolveAgencyCode(body.getOriginPortId(), body.getAgencyId());
                body.setAgencyId(agencyCode);
            }
            // 5) Perform creation via new service with both JWT values
            String bookNo = bookingCreateService.createMaster(body, customerCodeFromJwt, loginNameFromJwt);
            
            // 5.1) Insert customers if present
            if (body.getCustomers() != null && !body.getCustomers().isEmpty()) {
                bookingCreateService.addCustomers(bookNo, body.getCustomers(), loginNameFromJwt);
            }

            // 5.2) Insert routes if present
            if (body.getRoutes() != null && !body.getRoutes().isEmpty()) {
                bookingCreateService.addRoutes(bookNo, body.getRoutes(), loginNameFromJwt);
            }

            // 5.3) Insert containers if present
            if (body.getContainers() != null && !body.getContainers().isEmpty()) {
                bookingCreateService.addContainers(bookNo, body.getContainers(), body.getCommodities(), loginNameFromJwt);
            }

            // 5.4) Insert commodities if present (requires containers for containerNo -> seqno mapping)
            if (body.getCommodities() != null && !body.getCommodities().isEmpty()) {
                bookingCreateService.addCommodities(bookNo, body.getCommodities(), body.getContainers(), loginNameFromJwt);
            }
            // 5.5) Export to EDI temp tables when status is "S" (Submitted)
            String status = body.getStatus() != null ? body.getStatus().getValue() : "D";
            if ("S".equals(status)) {
                String agencyCode = body.getAgencyId(); // Use agencyId from request
                ediExportService.exportToEdiTempTables(bookNo, body, loginNameFromJwt);
            }            

            // 6) Create response
            BookingCreateResponse result = new BookingCreateResponse(
                bookNo,
                body.getStatus() != null ? body.getStatus() : BookingStatus.D,
                "Booking created successfully"
            );

            // 7) Build Location header if we have a booking number
            HttpHeaders headers = new HttpHeaders();
            if (result != null && result.getOnlineBookNo() != null && !result.getOnlineBookNo().trim().isEmpty()) {
                headers.setLocation(
                    URI.create("/api/v1/booking/" + result.getOnlineBookNo().trim())
                );
            }
          

            return new ResponseEntity<>(result, headers, HttpStatus.CREATED);

        } catch (ConstraintViolationException e) {
            throw e;
        } catch (SecurityException e) {
            logger.error("Security exception during booking creation", e);
            return unauthorized("Invalid JWT token or authentication failed");
        } catch (IllegalStateException ex) { // e.g., DB/report service unavailable
            logger.error("Service unavailable during booking creation", ex);
            return svcUnavailable();
        } catch (Exception e) {
            logger.error("Unexpected error occurred while creating the booking", e);
            return internalError("An unexpected error occurred while creating the booking");
        }
    }

    // ---- Update Endpoint -----------------------------------------------------
    @Operation(
        summary = "Update booking",
        description = "Updates an existing booking identified by onlineBookNo. Reefer is nested under containers and dangerous goods under commodities.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BookingRequestDTO.class),
                examples = {
                    @ExampleObject(
                        name = "Update-Minimal",
                        summary = "Minimal update",
                        description = "Minimal fields to update with nested structure",
                        value = com.solverminds.booking.api.BookingExamplesNew.MINIMAL_JSON
                    ),
                    @ExampleObject(
                        name = "Update-Full",
                        summary = "Full update",
                        description = "Full example payload including nested reefer and dangerous goods",
                        value = com.solverminds.booking.api.BookingExamplesNew.FULL_JSON
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(schema = @Schema(implementation = BookingCreateResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.Common.BAD_REQUEST_400,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))
            ),
            @ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))
            ),
            @ApiResponse(
                responseCode = "403",
                description = SwaggerDocs.Common.FORBIDDEN_403,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))
            ),
            @ApiResponse(
                responseCode = "404",
                description = SwaggerDocs.Common.NOT_FOUND_404,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))
            ),
            @ApiResponse(
                responseCode = "422",
                description = "Unprocessable Entity - Invalid status transition. The requested status change is not allowed based on the current booking state.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "InvalidStatusTransition", 
                        value = "{\"statusCode\":422,\"statusCodeText\":\"Unprocessable Entity\",\"errorDateTime\":\"2025-09-10T14:30:00Z\",\"errors\":[{\"errorCode\":2003,\"field\":\"status\",\"value\":\"V\",\"reason\":\"BUSINESS_RULE\",\"message\":\"Cannot change Confirmed (C) booking to status 'V'. No status changes or edits are allowed.\"}]}"))
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL))
            ),
            @ApiResponse(
                responseCode = "503",
                description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "ServiceUnavailable", value = SwaggerDocs.Examples.ERROR_SERVICE_UNAVAILABLE))
            )
        }
    )
    @PutMapping(
        path = "/booking/{onlineBookNo}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> update(
            HttpServletRequest request,
            @PathVariable("onlineBookNo") String onlineBookNo,
            @RequestBody BookingRequestDTO body) {
        try {
            // 1) JWT
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }

            // 2) Customer from JWT
            String customerCodeFromJwt = customerContext.extractCustomerCodeFromToken(token);
            if (customerCodeFromJwt == null || customerCodeFromJwt.trim().isEmpty()) {
                return unauthorized("Customer code missing in JWT");
            }

            // 2.5) Extract login name from JWT
            String loginNameFromJwt = customerContext.extractLoginFromToken(token);
            if (loginNameFromJwt == null || loginNameFromJwt.trim().isEmpty()) {
                return unauthorized("Login name missing in JWT");
            }

            // 3) Basic validation
            if (onlineBookNo == null || onlineBookNo.trim().isEmpty()) {
                return badRequest("onlineBookNo", null, "onlineBookNo path value is required");
            }
            if (body == null) {
                return badRequest(null, null, "Request body cannot be null");
            }

            BookingCargoWeightNormalizer.apply(body);
            validateBookingRequestDtoForUpdate(body);

            // 4) Align / validate customerCode
            if (body.getCustomerCode() == null || body.getCustomerCode().trim().isEmpty()) {
                body.setCustomerCode(customerCodeFromJwt);
            } else if (!customerCodeFromJwt.equalsIgnoreCase(body.getCustomerCode().trim())) {
                return forbidden("Customer code mismatch between JWT and payload");
            }

            // 5) Path vs body booking id (BKON or LRP book_no before resolve)
            String pathBookingId = onlineBookNo.trim();
            boolean isBkonPath = pathBookingId.toUpperCase(Locale.ROOT).startsWith("BKON");
            if (isBkonPath) {
                if (body.getOnlineBookNo() != null
                        && !body.getOnlineBookNo().trim().isEmpty()
                        && !pathBookingId.equalsIgnoreCase(body.getOnlineBookNo().trim())) {
                    return badRequest(
                            "onlineBookNo",
                            body.getOnlineBookNo(),
                            "Online booking number provided does not match the path value.");
                }
            } else {
                if (body.getBookingNumber() != null
                        && !body.getBookingNumber().trim().isEmpty()
                        && !pathBookingId.equalsIgnoreCase(body.getBookingNumber().trim())) {
                    return badRequest(
                            "bookingNumber",
                            body.getBookingNumber(),
                            "Booking number provided does not match the path value.");
                }
            }

            Optional<BookingUpdateLinkedNumberValidationService.ValidationError> relationshipError =
                    bookingUpdateLinkedNumberValidationService.validateOnlineBookNoBookingNumberRelationship(
                            pathBookingId, body, isBkonPath);
            if (relationshipError.isPresent()) {
                BookingUpdateLinkedNumberValidationService.ValidationError e = relationshipError.get();
                return badRequest(e.getField(), e.getValue(), e.getMessage());
            }

            Optional<BookingUpdateLinkedNumberValidationService.ValidationError> linkedNumberError =
                    bookingUpdateLinkedNumberValidationService.validateBlankOnlineBookNoWithBookingNumber(
                            pathBookingId, body, isBkonPath);
            if (linkedNumberError.isPresent()) {
                BookingUpdateLinkedNumberValidationService.ValidationError e = linkedNumberError.get();
                return badRequest(e.getField(), e.getValue(), e.getMessage());
            }

            // 6) Resolve BKON (pass-through for BKON path; link or shell for LRP path)
            String resolvedOnlineBookNo = bookingRepository.resolveOnlineBookNoForUpdate(
                    pathBookingId, body, customerCodeFromJwt, loginNameFromJwt);
            body.setOnlineBookNo(resolvedOnlineBookNo);

            // 7) Repository/service call with JWT parameters (unchanged update logic)
            BookingCreateResponse result = bookingRepository.updateBooking(
                    resolvedOnlineBookNo, body, customerCodeFromJwt, loginNameFromJwt);
            // if (result == null) {
            //     return notFound("Booking not found for onlineBookNo: " + resolvedOnlineBookNo);
            // }

            // 8) Location header
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("/api/v1/booking/" + resolvedOnlineBookNo));
            if (result.getMessage() == null || result.getMessage().trim().isEmpty()) {
                result.setMessage("Booking updated");
            }

            return new ResponseEntity<>(result, headers, HttpStatus.OK);

        } catch (ConstraintViolationException e) {
            throw e;
        } catch (BookingNotFoundException e) {
            String pathId = onlineBookNo != null ? onlineBookNo.trim() : "";
            if (!pathId.isEmpty()
                    && !pathId.toUpperCase(Locale.ROOT).startsWith("BKON")
                    && e.getMessage() != null
                    && e.getMessage().startsWith("Booking not found for onlineBookNo:")) {
                String onlineBookNoValue = pathId;
                if (body != null && body.getOnlineBookNo() != null && !body.getOnlineBookNo().trim().isEmpty()) {
                    onlineBookNoValue = body.getOnlineBookNo().trim();
                }
                return badRequest(
                        "onlineBookNo",
                        onlineBookNoValue,
                        "The API path supports the online booking number (" + onlineBookNoValue + ").");
            }
            return notFound(e.getMessage());
        } catch (LrpBookingLinkNotAllowedException e) {
            logger.warn("LRP booking link not allowed for {}: {}", onlineBookNo, e.getMessage());
            return unprocessableEntity(e.getMessage(), e.getLrpStatus(), null);
        } catch (BookingCancellationConflictException e) {
            logger.warn("Booking update blocked for {}: {}", onlineBookNo, e.getMessage());
            return unprocessableEntity(e.getMessage(), null, null);
        } catch (InvalidStatusTransitionException e) {
            logger.warn("Invalid status transition for booking {}: {}", onlineBookNo, e.getMessage());
            return unprocessableEntity(e.getMessage(), e.getCurrentStatus(), e.getRequestedStatus());
        } catch (SecurityException e) {
            return forbidden("Customer code mismatch - unauthorized access");
        } catch (IllegalStateException ex) {
            logger.error("Service unavailable during booking update", ex);
            return svcUnavailable();
        } catch (Exception e) {
            logger.error("Unexpected error occurred while updating the booking", e);
            return internalError("An unexpected error occurred while updating the booking");
        }
    }

    // ---- View Endpoint -------------------------------------------------------
    @Operation(
        summary = "View booking",
        description = "Fetch a booking by onlineBookNo or bookingNumber. At least one of these parameters is required.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(
                    schema = @Schema(implementation = BookingRequestDTO.class),
                    examples = @ExampleObject(name = "Success", value = com.solverminds.booking.api.BookingExamplesNew.VIEW_RESPONSE_200))
            ),
            @ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.Common.BAD_REQUEST_400,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))
            ),
            @ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))
            ),
            @ApiResponse(
                responseCode = "403",
                description = SwaggerDocs.Common.FORBIDDEN_403,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))
            ),
            @ApiResponse(
                responseCode = "404",
                description = SwaggerDocs.Common.NOT_FOUND_404,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL))
            ),
            @ApiResponse(
                responseCode = "503",
                description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "ServiceUnavailable", value = SwaggerDocs.Examples.ERROR_SERVICE_UNAVAILABLE))
            )
        }
    )
    @GetMapping(
        path = "/booking/view",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> view(
            HttpServletRequest request,
            @RequestParam(name = "onlineBookNo", required = false) String onlineBookNo,
            @RequestParam(name = "bookingNumber", required = false) String bookingNumber,
            @RequestParam(name = "status", required = false) String status) {
        try {
            // 1) JWT
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }

            // 2) Customer from JWT (SYSUSER read-only: no customerCode; customer tokens unchanged)
            String customerCodeFromJwt = customerContext.resolveCustomerCodeForBookingDetailRead(token);

            // 3) Validate that at least one search parameter is provided
            if ((onlineBookNo == null || onlineBookNo.trim().isEmpty()) 
                    && (bookingNumber == null || bookingNumber.trim().isEmpty())
                    ) {
                return badRequest("onlineBookNo/bookingNumber", null, 
                    "At least one of onlineBookNo or bookingNumber is required");
            }

            BookingViewLookupResult lookupResult = bookingRepository.findBookingForView(
                    customerCodeFromJwt,
                    safeTrim(onlineBookNo),
                    safeTrim(bookingNumber),
                    safeTrim(status));

            if (lookupResult.getError().isPresent()) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(lookupResult.getError().get());
            }
            if (!lookupResult.getBooking().isPresent()) {
                if (lookupResult.hasNotFoundContext()) {
                    return notFound(
                            "Booking not found for given parameters",
                            lookupResult.getNotFoundField(),
                            lookupResult.getNotFoundValue());
                }
                return notFound("Booking not found for given parameters");
            }

            return ResponseEntity.ok(lookupResult.getBooking().get());

        } catch (SecurityException e) {
            return unauthorized("Invalid JWT token or authentication failed");
        } catch (IllegalStateException ex) {
            logger.error("Service unavailable during booking fetch", ex);
            return svcUnavailable();
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching the booking", e);
            return internalError("An unexpected error occurred while fetching the booking");
        }
    }

    @Operation(
        summary = "List all bookings for system integration (keyset pagination)",
        description = "SYSUSER-only. Returns all customers' bookings via usp_getbookingsummary_system. "
            + "Does not filter by customerCode. Isolated from GET /api/v1/booking/summary.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(schema = @Schema(implementation = SystemBookingListResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.Common.BAD_REQUEST_400,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))
            ),
            @ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))
            ),
            @ApiResponse(
                responseCode = "403",
                description = SwaggerDocs.Common.FORBIDDEN_403,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL))
            ),
            @ApiResponse(
                responseCode = "503",
                description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "ServiceUnavailable", value = SwaggerDocs.Examples.ERROR_SERVICE_UNAVAILABLE))
            )
        }
    )
    @GetMapping(path = "/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listSystemBookings(
            HttpServletRequest request,
            @RequestParam(name = "limit", required = false) String limitParam,
            @RequestParam(name = "cursor", required = false) String cursor,
            @RequestParam(name = "changeDateAfter", required = false) String changeDateAfterParam) {
        try {
            String token = extractJwtToken(request.getHeader("Authorization"));
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }
            jwtUtil.validateTokenWithSession(token);
            if (!jwtUtil.isSysUserReadOnlyToken(token)) {
                return forbidden("SYSUSER role required");
            }

            int limit = parseSystemListLimit(limitParam);
            Timestamp changeDateAfter = parseChangeDateAfter(changeDateAfterParam);
            return ResponseEntity.ok(systemBookingListService.list(limit, cursor, changeDateAfter));

        } catch (InvalidCursorException e) {
            return badRequest("cursor", cursor, "Invalid or malformed cursor");
        } catch (NumberFormatException e) {
            return badRequest("limit", limitParam, "limit must be an integer");
        } catch (DateTimeParseException e) {
            return badRequest("changeDateAfter", changeDateAfterParam,
                    "changeDateAfter must be UTC date-time in format " + DateFormatConstants.UTC_DATE_TIME);
        } catch (IllegalArgumentException e) {
            return badRequest(null, null, e.getMessage());
        } catch (SystemBookingListRejectedException e) {
            logger.warn("System booking list rejected: {}", e.getMessage());
            return svcUnavailable();
        } catch (QueryTimeoutException e) {
            logger.error("Query timeout during system booking list", e);
            return svcUnavailable();
        } catch (SecurityException e) {
            return unauthorized("Invalid JWT token or authentication failed");
        } catch (RuntimeException e) {
            if (isJwtValidationFailure(e)) {
                return unauthorized("Invalid JWT token or authentication failed");
            }
            if (e instanceof IllegalStateException) {
                logger.error("Service unavailable during system booking list", e);
                return svcUnavailable();
            }
            logger.error("Unexpected error during system booking list", e);
            return internalError("An unexpected error occurred while listing bookings");
        } catch (Exception e) {
            logger.error("Unexpected error during system booking list", e);
            return internalError("An unexpected error occurred while listing bookings");
        }
    }

    private static int parseSystemListLimit(String limitParam) {
        if (limitParam == null || limitParam.trim().isEmpty()) {
            return SystemBookingListService.DEFAULT_LIMIT;
        }
        int limit = Integer.parseInt(limitParam.trim());
        if (limit < 1) {
            throw new IllegalArgumentException("limit must be at least 1");
        }
        return Math.min(limit, SystemBookingListService.MAX_LIMIT);
    }

    private static Timestamp parseChangeDateAfter(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        LocalDateTime parsed = LocalDateTime.parse(raw.trim(), UTC_DATE_TIME);
        return Timestamp.from(parsed.atZone(ZoneOffset.UTC).toInstant());
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


    // ---- Helper Methods ------------------------------------------------------
    
    /**
     * Create a proper ErrorResponse for 400 Bad Request (validation)
     */
    private ResponseEntity<ErrorResponse> badRequest(String field, String value, String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            400,
            "Bad Request",
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.VALIDATION_GENERIC,
            field,
            value,
            ErrorReason.VALIDATION_ERROR,
            message != null ? message : "Invalid input"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 403 Forbidden
     */
    private ResponseEntity<ErrorResponse> forbidden(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            403,
            "Forbidden",
            LocalDateTime.now()
        );
        // Determine specific error code based on message
        int errorCode = ErrorCodes.FORBIDDEN;
        if (message != null && message.contains("Customer code mismatch")) {
            errorCode = ErrorCodes.AUTH_CUSTOMER_CODE_MISMATCH;
        }
        errorResponse.addError(
            errorCode,
            null,
            null,
            ErrorReason.FORBIDDEN,
            message != null ? message : "Access denied"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

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
     * Create a proper ErrorResponse for 401 Unauthorized errors
     */
    private ResponseEntity<ErrorResponse> unauthorized(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            401,
            "Unauthorized", 
            LocalDateTime.now()
        );
        // Determine specific error code based on message
        int errorCode = ErrorCodes.UNAUTHORIZED;
        if (message != null) {
            if (message.contains("Missing") && message.contains("JWT")) {
                errorCode = ErrorCodes.AUTH_MISSING_JWT;
            } else if (message.contains("Invalid JWT") || message.contains("authentication failed")) {
                errorCode = ErrorCodes.AUTH_INVALID_JWT;
            } else if (message.contains("Customer code missing")) {
                errorCode = ErrorCodes.AUTH_CUSTOMER_CODE_MISSING;
            } else if (message.contains("Login name missing")) {
                errorCode = ErrorCodes.AUTH_LOGIN_NAME_MISSING;
            }
        }
        errorResponse.addError(
            errorCode,
            null,
            null,
            ErrorReason.UNAUTHORIZED,
            message != null ? message : "Authentication required or invalid token"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
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
     * Create a proper ErrorResponse for 404 Not Found
     */
    private ResponseEntity<ErrorResponse> notFound(String message) {
        return notFound(message, null, null);
    }

    /**
     * Create a proper ErrorResponse for 404 Not Found with field and rejected value populated.
     */
    private ResponseEntity<ErrorResponse> notFound(String message, String field, String value) {
        ErrorResponse errorResponse = new ErrorResponse(
            404,
            "Not Found",
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.NOT_FOUND,
            field,
            value,
            ErrorReason.NOT_FOUND,
            message != null ? message : "Requested resource not found"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 422 Unprocessable Entity - Invalid Status Transition
     */
    private ResponseEntity<ErrorResponse> unprocessableEntity(String message, String currentStatus, String requestedStatus) {
        ErrorResponse errorResponse = new ErrorResponse(
            422,
            "Unprocessable Entity",
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.BUSINESS_RULE_VIOLATION,
            "status",
            requestedStatus,
            ErrorReason.BUSINESS_RULE,
            message != null ? message : "Invalid status transition"
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    /**
     * Utility method for null-safe string trimming
     */
    private String safeTrim(String v) {
        return v == null ? null : v.trim();
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

    // ---- Document Upload Endpoint --------------------------------------------
    @Operation(
        summary = "Upload documents for booking",
        description = "Upload 1-5 documents (max 10MB per file, max 50MB total request). Forbidden types: executables, audio, video files. " +
            "Set overwrite=true to replace existing files.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Documents uploaded successfully. Returns array of upload results.",
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
                            "    \"fileName\": \"packing_list.xlsx\",\n" +
                            "    \"sizeBytes\": 89012\n" +
                            "  }\n" +
                            "]"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad Request - Invalid file count, size, type, or duplicate names",
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "ValidationError",
                        value = "{\n" +
                            "  \"statusCode\": 400,\n" +
                            "  \"statusCodeText\": \"Bad Request\",\n" +
                            "  \"errorDateTime\": \"2025-10-22T18:00:00\",\n" +
                            "  \"errors\": [\n" +
                            "    {\n" +
                            "      \"errorCode\": 1001,\n" +
                            "      \"message\": \"File type not allowed: exe\"\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Not Found - Booking does not exist",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))
            ),
            @ApiResponse(
                responseCode = "409",
                description = "Conflict - File already exists and overwrite=false",
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "FileExists",
                        value = "{\n" +
                            "  \"statusCode\": 409,\n" +
                            "  \"statusCodeText\": \"Conflict\",\n" +
                            "  \"errorDateTime\": \"2025-10-22T18:00:00\",\n" +
                            "  \"errors\": [\n" +
                            "    {\n" +
                            "      \"errorCode\": 1008,\n" +
                            "      \"message\": \"File already exists and overwrite=false: invoice.pdf\"\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL))
            )
        }
    )
    @PostMapping(
        path = "/booking/{bookingId}/documents",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> uploadDocuments(
            HttpServletRequest request,
            @PathVariable("bookingId") String bookingId,
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

            // 3) Validate bookingId (booking number)
            if (bookingId == null || bookingId.trim().isEmpty()) {
                return badRequest("bookingId", bookingId, "Booking ID (number) cannot be empty");
            }

            // 4) Upload documents (AP/BP customer access validated in service)
            List<DocumentUploadResponse> responses = documentService.uploadDocuments(
                bookingId, customerCodeFromJwt, files, overwrite
            );

            // 5) Check if any files had errors
            boolean hasErrors = responses.stream().anyMatch(r -> r.getError() != null);
            if (hasErrors) {
                logger.warn("Some files failed validation during upload for booking ID: {}", bookingId);
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
            logger.error("Unexpected error during document upload for booking ID: {}", bookingId, e);
            return internalError("An unexpected error occurred while uploading documents");
        }
    }

    // ---- Document Listing Endpoint -------------------------------------------
    @Operation(
        summary = "List documents for booking",
        description = "List all documents in booking folder. Set includeChecksum=true to get SHA-256 hash for each file. " +
            "Returns empty array if no documents uploaded yet.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "List of documents. Returns empty array if no documents uploaded yet.",
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
                                "    \"lastModified\": \"2025-10-22T10:30:00Z\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"fileName\": \"packing_list.xlsx\",\n" +
                                "    \"sizeBytes\": 89012,\n" +
                                "    \"lastModified\": \"2025-10-22T11:15:00Z\"\n" +
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
                                "    \"lastModified\": \"2025-10-22T10:30:00Z\",\n" +
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
            @ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.Common.BAD_REQUEST_400,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))
            ),
            @ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Not Found - Booking does not exist",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL))
            )
        }
    )
    @GetMapping(
        path = "/booking/{bookingId}/documents",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> listDocuments(
            HttpServletRequest request,
            @PathVariable("bookingId") String bookingId,
            @RequestParam(value = "includeChecksum", defaultValue = "false") boolean includeChecksum) {
        try {
            // 1) JWT authentication
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }

            // 2) Customer scope from JWT (SYSUSER read-only: no customerCode required)
            String customerCode = customerContext.resolveCustomerCodeForBookingDetailRead(token);

            // 3) Validate bookingId (booking number)
            if (bookingId == null || bookingId.trim().isEmpty()) {
                return badRequest("bookingId", bookingId, "Booking ID (number) cannot be empty");
            }

            // 4) List documents (AP/BP customer access validated when customerCode present)
            List<DocumentListResponse> documents = documentService.listDocuments(bookingId, customerCode, includeChecksum);

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
            logger.error("Unexpected error during document listing for booking ID: {}", bookingId, e);
            return internalError("An unexpected error occurred while listing documents");
        }
    }

    // ---- Document Download/Preview Endpoint ----------------------------------
    @Operation(
        summary = "Download or preview a document",
        description = "Download or preview a specific document. PDFs and images preview in browser, " +
            "other files download to disk. Returns file content with appropriate Content-Type.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "File content returned successfully. Content-Type varies by file type.",
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
                    ),
                    @Content(
                    mediaType = "application/octet-stream",
                        examples = @ExampleObject(
                            name = "Other",
                            description = "Other files - downloads to disk",
                            value = "[Binary file content]"
                        )
                    )
                }
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad Request - Invalid file name (e.g., contains path traversal characters)",
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "InvalidFileName",
                        value = "{\n" +
                            "  \"statusCode\": 400,\n" +
                            "  \"statusCodeText\": \"Bad Request\",\n" +
                            "  \"errorDateTime\": \"2025-10-22T18:00:00\",\n" +
                            "  \"errors\": [\n" +
                            "    {\n" +
                            "      \"errorCode\": 1001,\n" +
                            "      \"message\": \"Invalid file name: ../../../secret.txt\"\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "File not found or booking does not exist",
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "FileNotFound",
                        value = "{\n" +
                            "  \"statusCode\": 404,\n" +
                            "  \"statusCodeText\": \"Not Found\",\n" +
                            "  \"errorDateTime\": \"2025-10-22T18:00:00\",\n" +
                            "  \"errors\": [\n" +
                            "    {\n" +
                            "      \"errorCode\": 1004,\n" +
                            "      \"message\": \"File not found: nonexistent.pdf\"\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @GetMapping(
        path = "/booking/{bookingId}/documents/{fileName}",
        produces = {
            MediaType.APPLICATION_OCTET_STREAM_VALUE,
            MediaType.APPLICATION_PDF_VALUE,
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE
        }
    )
    public ResponseEntity<?> downloadDocument(
            HttpServletRequest request,
            @PathVariable("bookingId") String bookingId,
            @PathVariable("fileName") String fileName) {
        try {
            // 1) JWT authentication
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                return unauthorized("Missing or invalid Authorization header");
            }

            // 2) Extract customerCode from JWT (SYSUSER read-only: no customerCode required)
            String customerCode = customerContext.resolveCustomerCodeForBookingDetailRead(token);

            logger.info("Download request - Booking: {}, File: {}, Customer: {}",
                bookingId, fileName, customerCode);

            // 3) Get file from service (AP/BP customer access validated when customerCode present)
            File file = documentService.downloadDocument(bookingId, fileName, customerCode);

            // 4) Determine content type
            String contentType = determineContentType(fileName);

            // 5) Create resource
            Resource resource = new FileSystemResource(file);

            // 6) Set headers for inline display (preview) or download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(file.length());
            
            // For PDFs, images, etc., use inline to preview in browser
            if (isPreviews(contentType)) {
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
            logger.error("Unexpected error during document download - Booking: {}, File: {}", 
                bookingId, fileName, e);
            return internalError("An unexpected error occurred while downloading the document");
        }
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
    private boolean isPreviews(String contentType) {
        return contentType.startsWith("image/") ||
               contentType.equals("application/pdf") ||
               contentType.startsWith("text/");
    }

    // ---- Additional Helper Method --------------------------------------------

    private void validateBookingRequestDto(BookingRequestDTO body) {
        Set<ConstraintViolation<BookingRequestDTO>> violations = validator.validate(body, Default.class);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    /**
     * PUT / update: {@link Default} + {@link ValidationGroups.UpdateBooking} (e.g. {@code containerNo} on containers and commodities).
     */
    private void validateBookingRequestDtoForUpdate(BookingRequestDTO body) {
        Set<ConstraintViolation<BookingRequestDTO>> violations =
                validator.validate(body, Default.class, ValidationGroups.UpdateBooking.class);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
    
    /**
     * Create a proper ErrorResponse for 409 Conflict
     */
    private ResponseEntity<ErrorResponse> conflict(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            409,
            "Conflict",
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.CONFLICT,
            null,
            null,
            ErrorReason.CONFLICT,
            message != null ? message : "Resource conflict"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
