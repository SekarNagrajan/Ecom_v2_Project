package com.solverminds.booking.api;

import com.solverminds.booking.dto.BookingCreateResponse;
import com.solverminds.booking.dto.BookingRequestDTO;
import com.solverminds.booking.dto.v2.*; // ErrorMessage, NestedErrorMessage (renamed from V2 suffix)
import com.solverminds.booking.dto.v2.vgm.Address;
import com.solverminds.booking.dto.v2.vgm.AddressRequest;
import com.solverminds.booking.dto.v2.vgm.ContainerVGM;
import com.solverminds.booking.dto.v2.vgm.ContainerVGMRequest;
import com.solverminds.booking.dto.v2.vgm.VGMInfo;
import com.solverminds.booking.dto.v2.vgm.VGMCreateRequest;
import com.solverminds.booking.dto.v2.vgm.VgmUpdateResponse;
import com.solverminds.booking.entity.EcomBooking;
import com.solverminds.booking.exception.BookingCancelAccessDeniedException;
import com.solverminds.booking.exception.BookingCancellationConflictException;
import com.solverminds.booking.exception.BookingNotFoundException;
import com.solverminds.booking.exception.BookingNotFoundForCancelException;
import com.solverminds.booking.exception.LrpBookingLinkNotAllowedException;
import com.solverminds.booking.exception.BookingVgmForbiddenException;
import com.solverminds.booking.exception.ContainerNotFoundException;
import com.solverminds.booking.exception.InvalidStatusTransitionException;
import com.solverminds.booking.mapper.BookingMapperV2;
import com.solverminds.booking.repo.BookingRepository;
import com.solverminds.booking.repo.EcomBookingRepository;
import com.solverminds.booking.security.CustomerContext;
import com.solverminds.booking.service.BookingCancelService;
import com.solverminds.booking.service.BookingCreateService;
import com.solverminds.booking.service.DocumentationBookingViewService;
import com.solverminds.booking.service.EdiExportService;
import com.solverminds.booking.util.BookingCargoWeightNormalizer;
import com.solverminds.booking.repo.VgmRepository;
import com.solverminds.booking.validation.DangerousGoodsLockService;
import com.solverminds.booking.validation.V2BookingRequestValidationService;
import com.svm.ecom.common.enums.BlStatus;
import com.svm.ecom.common.enums.BookingStatus;
import com.svm.ecom.common.enums.EquipmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Locale;
import org.springframework.validation.annotation.Validated;
import com.solverminds.booking.dto.validation.ValidationGroups;

/**
 * V2 Booking API Controller.
 * Implements RESTful endpoints following the customer's OpenAPI specification.
 * This controller does NOT affect the existing V1 API endpoints.
 */
@RestController
@RequestMapping("/api/v2/bookings")
@Tag(name = "Bookings", description = "Operations related to managing bookings.")
@SecurityRequirement(name = "bearerAuth")
public class BookingControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(BookingControllerV2.class);
    
    private static final String BKON_PREFIX = "BKON";
    private static final String INSTANCE_BOOKINGS = "/api/v2/bookings";
    private static final String CANCEL_SUCCESS_MESSAGE = "Submitted booking is canceled.";
    private static final java.util.Set<String> ACTIVE_ECOM_STATUSES = 
        new java.util.HashSet<>(Arrays.asList("D", "S", "T", "V"));
    private static final java.util.Set<String> LRP_STATUSES = 
        new java.util.HashSet<>(Arrays.asList("P", "A", "C"));

    private final BookingRepository bookingRepository;
    private final EcomBookingRepository ecomBookingRepository;
    private final CustomerContext customerContext;
    private final BookingCreateService bookingCreateService;
    private final BookingMapperV2 bookingMapperV2;
    private final EdiExportService ediExportService;
    private final DocumentationBookingViewService documentationBookingViewService;
    private final V2BookingRequestValidationService v2BookingRequestValidationService;
    private final DangerousGoodsLockService dangerousGoodsLockService;
    private final BookingCancelService bookingCancelService;
    private final VgmRepository vgmRepository;

    public BookingControllerV2(BookingRepository bookingRepository,
                               EcomBookingRepository ecomBookingRepository,
                               CustomerContext customerContext,
                               BookingCreateService bookingCreateService,
                               BookingMapperV2 bookingMapperV2,
                               EdiExportService ediExportService,
                               DocumentationBookingViewService documentationBookingViewService,
                               V2BookingRequestValidationService v2BookingRequestValidationService,
                               DangerousGoodsLockService dangerousGoodsLockService,
                               BookingCancelService bookingCancelService,
                               VgmRepository vgmRepository) {
        this.bookingRepository = bookingRepository;
        this.ecomBookingRepository = ecomBookingRepository;
        this.customerContext = customerContext;
        this.bookingCreateService = bookingCreateService;
        this.bookingMapperV2 = bookingMapperV2;
        this.ediExportService = ediExportService;
        this.documentationBookingViewService = documentationBookingViewService;
        this.v2BookingRequestValidationService = v2BookingRequestValidationService;
        this.dangerousGoodsLockService = dangerousGoodsLockService;
        this.bookingCancelService = bookingCancelService;
        this.vgmRepository = vgmRepository;
    }

    // ==================== GET /api/v2/bookings ====================
    
    // Supported sort values for Booking Summary V2 (Swagger-defined fields)
    private static final List<String> SUPPORTED_SORT_FIELDS = Arrays.asList(
        "createdAt", "-createdAt", "updatedAt", "-updatedAt", "confirmedAt", "-confirmedAt",
        "etd", "-etd", "eta", "-eta",
        "status", "-status", "teus", "-teus",
        "originId", "-originId", "originName", "-originName",
        "deliveryId", "-deliveryId", "deliveryName", "-deliveryName",
        "polId", "-polId", "polName", "-polName",
        "podId", "-podId", "podName", "-podName",
        "blNumber", "-blNumber", "blStatus", "-blStatus"
    );
    private static final String DEFAULT_SORT = "-createdAt";
    private static final int DEFAULT_OFFSET = 0;
    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 200;
    private static final int MIN_LIMIT = 1;
    
    private static final String SORT_AVAILABLE_VALUES =
        "originId, -originId, originName, -originName, deliveryId, -deliveryId, deliveryName, -deliveryName, "
        + "polId, -polId, polName, -polName, podId, -podId, podName, -podName, eta, -eta, etd, -etd, "
        + "status, -status, teus, -teus, createdAt, -createdAt, updatedAt, -updatedAt, confirmedAt, -confirmedAt, "
        + "blNumber, -blNumber, blStatus, -blStatus";

    @Hidden
    @Operation(
        summary = "Get a list of bookings for authenticated user, default ordered descending by creation date (i.e. most recent one first)",
        operationId = "getBookings",
        tags = { "Bookings" },
        parameters = {
            @Parameter(
                name = "filter",
                in = ParameterIn.QUERY,
                description = "Filtering criteria for booking items. Pass as query params: filter[key]=value. "
                    + "Keys: id_contains, status_equals, origin_id_contains|originId_contains, origin_name_contains|originName_contains, delivery_id_contains|deliveryId_contains, delivery_name_contains|deliveryName_contains.",
                required = false,
                style = ParameterStyle.FORM,
                explode = Explode.TRUE,
                schema = @Schema(implementation = BookingFilterV2.class)
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Response object containing a list of bookings.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedBookingListV2.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed (Missing or invalid Bearer token).",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "The client does not have access rights to the content.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected server-side error occurred.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "503", description = "The service is currently not available.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBookings(
            HttpServletRequest request,
            @Parameter(
                name = "offset",
                description = "The number of items to skip before starting to collect the result set.",
                schema = @Schema(implementation = Integer.class, defaultValue = "0", minimum = "0"),
                example = "0"
            )
            @RequestParam(defaultValue = "0") int offset,
            @Parameter(
                name = "limit",
                description = "The number of items to return.",
                schema = @Schema(implementation = Integer.class, defaultValue = "20", minimum = "1", maximum = "200"),
                example = "20"
            )
            @RequestParam(defaultValue = "20") int limit,
            @Parameter(
                name = "sort",
                description = "Sorting criteria for booking items. Pass multiple values for multi-column sorting. "
                    + "Prefix with '-' for descending order. When ordering a column which does not contain values, "
                    + "non-existing values are always considered less than existing ones (e.g. empty confirmedAt date < non-empty confirmedAt date). "
                    + "Available values: " + SORT_AVAILABLE_VALUES,
                schema = @Schema(type = "array", example = "[\"-createdAt\"]"),
                example = "-createdAt"
            )
            @RequestParam(required = false, defaultValue = "-createdAt") List<String> sort,
            @Parameter(hidden = true)
            @RequestParam(required = false) Map<String, String> allParams) {
        try {
            // 1) Extract JWT
            String token = extractJwtToken(request.getHeader("Authorization"));
            if (token == null) {
                return unauthorized("Missing or invalid JWT token", INSTANCE_BOOKINGS);
            }

            // 2) Extract customerCode from JWT
            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            if (customerCode == null || customerCode.trim().isEmpty()) {
                return unauthorized("Customer code missing in JWT", INSTANCE_BOOKINGS);
            }

            // 3) Validate offset
            if (offset < 0) offset = DEFAULT_OFFSET;

            // 4) Validate limit
            if (limit < MIN_LIMIT) limit = MIN_LIMIT;
            if (limit > MAX_LIMIT) limit = MAX_LIMIT;

            // 5) Validate and normalize sort fields
            List<String> validSortFields = new ArrayList<>();
            if (sort != null && !sort.isEmpty()) {
                for (String s : sort) {
                    if (s != null && SUPPORTED_SORT_FIELDS.contains(s.trim())) {
                        validSortFields.add(s.trim());
                    }
                }
            }
            if (validSortFields.isEmpty()) {
                validSortFields.add(DEFAULT_SORT);
            }

            // 6) Build filter object from filter[key]=value query params
            BookingFilterV2 filter = buildFilterFromParams(allParams);

            // 7) Get paged bookings and total count in one SP call
            com.solverminds.booking.dto.PagedBookingSummaryResult paged = bookingRepository
                    .getBookingSummaryPagedWithCount(customerCode, filter, validSortFields, offset, limit);
            int total = paged.getTotalCount();
            List<com.solverminds.booking.dto.BookingSummaryResponseDto> summaryList = paged.getItems();

            // 8) Convert to V2 list items
            List<PagedBookingListItemV2> items = new ArrayList<>();
            for (com.solverminds.booking.dto.BookingSummaryResponseDto summary : summaryList) {
                PagedBookingListItemV2 item = convertToListItem(summary);
                items.add(item);
            }

            // 9) Build response with offset-based pagination metadata (use requested limit, not returned count)
            PaginationMeta meta = new PaginationMeta(total, offset, limit);
            PagedBookingListV2 response = new PagedBookingListV2(items, meta);

            return ResponseEntity.ok(response);

        } catch (SecurityException e) {
            logger.error("Security exception during booking list", e);
            return unauthorized("Invalid JWT token or authentication failed", INSTANCE_BOOKINGS);
        } catch (IllegalStateException ex) {
            logger.error("Service unavailable during booking list", ex);
            return serviceUnavailable("Report database is currently unavailable", INSTANCE_BOOKINGS);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching bookings", e);
            return internalServerError("An unexpected error occurred while fetching bookings", INSTANCE_BOOKINGS);
        }
    }

    /**
     * Builds {@link BookingFilterV2} from request params. Supports both client spec (camelCase) and snake_case keys:
     * filter[status_equals]=D, filter[originId_contains]=INMAA or filter[origin_id_contains]=INMAA, etc.
     */
    private static BookingFilterV2 buildFilterFromParams(Map<String, String> allParams) {
        BookingFilterV2 filter = new BookingFilterV2();
        if (allParams == null) return filter;
        String prefix = "filter[";
        String suffix = "]";
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key == null || value == null || !key.startsWith(prefix) || !key.endsWith(suffix)) continue;
            String filterKey = key.substring(prefix.length(), key.length() - suffix.length()).trim();
            if (filterKey.isEmpty()) continue;
            switch (filterKey) {
                case "id_contains":
                    filter.setIdContains(value);
                    break;
                case "status_equals":
                    filter.setStatusEquals(value);
                    break;
                case "origin_id_contains":
                case "originId_contains":
                    filter.setOriginIdContains(value);
                    break;
                case "origin_name_contains":
                case "originName_contains":
                    filter.setOriginNameContains(value);
                    break;
                case "delivery_id_contains":
                case "deliveryId_contains":
                    filter.setDeliveryIdContains(value);
                    break;
                case "delivery_name_contains":
                case "deliveryName_contains":
                    filter.setDeliveryNameContains(value);
                    break;
                default:
                    break;
            }
        }
        return filter;
    }

    // ==================== POST /api/v2/bookings ====================

    @Hidden
    @Operation(
        summary = "Create booking.",
        operationId = "createBooking",
        tags = { "Bookings" },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BookingRequestUpsertPayloadV2.class, example = BookingExamplesNew.V2_FULL_DG_JSON)
            )
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Response object containing a newly created booking.",
                content = @Content(schema = @Schema(implementation = BookingV2.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "The resource could not be found.",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "503", description = "Service Unavailable",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> createBooking(
            HttpServletRequest request,
            @Valid @RequestBody BookingRequestUpsertPayloadV2 body) {
        try {
            // 1) Extract JWT
            String token = extractJwtToken(request.getHeader("Authorization"));
            if (token == null) {
                return unauthorized("Missing or invalid JWT token", INSTANCE_BOOKINGS);
            }

            // 2) Extract customerCode and loginName from JWT
            String customerCodeFromJwt = customerContext.extractCustomerCodeFromToken(token);
            if (customerCodeFromJwt == null || customerCodeFromJwt.trim().isEmpty()) {
                return unauthorized("Customer code missing in JWT", INSTANCE_BOOKINGS);
            }

            String loginNameFromJwt = customerContext.extractLoginFromToken(token);
            if (loginNameFromJwt == null || loginNameFromJwt.trim().isEmpty()) {
                return unauthorized("Login name missing in JWT", INSTANCE_BOOKINGS);
            }

            // 3) Validate request (V2 body: @Valid above — failures → BookingV2ExceptionHandler / MethodArgumentNotValidException)
            if (body == null) {
                return badRequest("Request body cannot be null", INSTANCE_BOOKINGS);
            }

            // 3a) Normalize: clear reefer details for DC (Dry) containers (before map to V1)
            clearReeferSettingsForDryContainers(body);

            // 4) Convert V2 request to V1 DTO (same target model as V1 API BookingController create)
            BookingRequestDTO v1Request = bookingMapperV2.toV1BookingRequest(body, customerCodeFromJwt);

            // 4a) SI-aligned container cargoWeight from commodities when present (before Bean Validation)
            BookingCargoWeightNormalizer.apply(v1Request);

            // 4b) Bean Validation on mapped BookingRequestDTO (Default group — same as V1 @Valid BookingRequestDTO)
            Optional<ErrorMessage> v1ModelError =
                v2BookingRequestValidationService.validateV1BookingRequestDto(v1Request, INSTANCE_BOOKINGS);
            if (v1ModelError.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(v1ModelError.get());
            }

            // 5) Resolve agency code if not provided
            if (v1Request.getAgencyId() == null || v1Request.getAgencyId().trim().isEmpty()) {
                String agencyCode = bookingCreateService.resolveAgencyCode(v1Request.getOriginPortId(), v1Request.getAgencyId());
                v1Request.setAgencyId(agencyCode);
            }

            // 6) Create booking using existing service
            String bookNo = bookingCreateService.createMaster(v1Request, customerCodeFromJwt, loginNameFromJwt);

            // 7) Insert customers if present
            if (v1Request.getCustomers() != null && !v1Request.getCustomers().isEmpty()) {
                bookingCreateService.addCustomers(bookNo, v1Request.getCustomers(), loginNameFromJwt);
            }

            // 8) Insert routes if present
            if (v1Request.getRoutes() != null && !v1Request.getRoutes().isEmpty()) {
                bookingCreateService.addRoutes(bookNo, v1Request.getRoutes(), loginNameFromJwt);
            }

            // 9) Insert containers if present
            if (v1Request.getContainers() != null && !v1Request.getContainers().isEmpty()) {
                bookingCreateService.addContainers(bookNo, v1Request.getContainers(), v1Request.getCommodities(), loginNameFromJwt);
            }

            // 10) Insert commodities if present
            if (v1Request.getCommodities() != null && !v1Request.getCommodities().isEmpty()) {
                bookingCreateService.addCommodities(bookNo, v1Request.getCommodities(), v1Request.getContainers(), loginNameFromJwt);
            }

            // 11) Export to EDI temp tables when status is "S" (Submitted)
            String status = v1Request.getStatus() != null ? v1Request.getStatus().getValue() : "D";
            if ("S".equals(status)) {
                java.util.Map<String, Object> ediResult = ediExportService.exportToEdiTempTables(bookNo, v1Request, loginNameFromJwt);
                if (!Boolean.TRUE.equals(ediResult.get("success"))) {
                    logger.error("EDI export failed for booking {}: {}", bookNo, ediResult.get("message"));
                }
            }

            // 12) Build V2 response
            v1Request.setOnlineBookNo(bookNo);
            BookingV2 response = bookingMapperV2.toV2Booking(v1Request, bookNo);
            response.setCreatedBy(loginNameFromJwt);

            // 13) Build Location header
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("/api/v2/bookings/" + bookNo));

            return new ResponseEntity<>(response, headers, HttpStatus.CREATED);

        } catch (SecurityException e) {
            logger.error("Security exception during booking creation", e);
            return unauthorized("Invalid JWT token or authentication failed", INSTANCE_BOOKINGS);
        } catch (IllegalStateException ex) {
            logger.error("Service unavailable during booking creation", ex);
            return serviceUnavailable("Report database is currently unavailable", INSTANCE_BOOKINGS);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while creating the booking", e);
            return internalServerError("An unexpected error occurred while creating the booking", INSTANCE_BOOKINGS);
        }
    }

    // ==================== GET /api/v2/bookings/{bookingId} ====================

    @Hidden
    @Operation(
        summary = "Get a booking by id.",
        operationId = "getBookingById",
        tags = { "Bookings" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Response object containing a single booking.",
                content = @Content(schema = @Schema(implementation = BookingV2.class),
                    examples = @ExampleObject(name = "Success", value = BookingExamplesNew.V2_GET_BOOKING_RESPONSE_200))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "503", description = "Service Unavailable",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @GetMapping(
        path = "/{bookingId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getBookingById(
            HttpServletRequest request,
            @Parameter(description = "Booking ID (e.g., BKON123 for ECOM bookings or LRP booking number)") 
            @PathVariable("bookingId") String bookingId) {
        try {
            // 1) Extract JWT
            String token = extractJwtToken(request.getHeader("Authorization"));
            String instanceById = (bookingId != null && !bookingId.trim().isEmpty()) ? INSTANCE_BOOKINGS + "/" + bookingId.trim() : INSTANCE_BOOKINGS;
            if (token == null) {
                return unauthorized("Missing or invalid JWT token", instanceById);
            }

            // 2) Customer scope from JWT (SYSUSER read-only: no customerCode; customer tokens unchanged)
            String customerCodeFromJwt = customerContext.resolveCustomerCodeForBookingDetailRead(token);

            // 3) Validate bookingId
            if (bookingId == null || bookingId.trim().isEmpty()) {
                return badRequest("Booking ID is required", INSTANCE_BOOKINGS);
            }

            String trimmedBookingId = bookingId.trim();
            instanceById = INSTANCE_BOOKINGS + "/" + trimmedBookingId;

            // 4) Resolve booking: BKON -> ECOM (D/S/T/V only); non-BKON -> LRP (P/A/C only when in ecombooking)
            if (trimmedBookingId.toUpperCase().startsWith(BKON_PREFIX)) {
                return handleBkonBooking(trimmedBookingId, customerCodeFromJwt, instanceById);
            }
            if (customerCodeFromJwt == null) {
                return resolveSysUserNonBkonBooking(trimmedBookingId, instanceById);
            }
            {
                Optional<EcomBooking> byExtBookNo = ecomBookingRepository.findByExtBookNoAndCustCode(trimmedBookingId, customerCodeFromJwt);
                if (byExtBookNo.isPresent()) {
                    EcomBooking ecomBooking = byExtBookNo.get();
                    // Verify customer ownership
                    if (!customerCodeFromJwt.equalsIgnoreCase(ecomBooking.getCustCode())) {
                        return forbidden("Access denied to booking: " + trimmedBookingId, instanceById);
                    }
                    String status = ecomBooking.getEbookStatus();
                    if (!isLrpStatus(status)) {
                        return buildStatusMismatchError(trimmedBookingId, status, "LRP");
                    }
                    return handleLrpBooking(trimmedBookingId, customerCodeFromJwt, instanceById);
                }
                return handleLrpBooking(trimmedBookingId, customerCodeFromJwt, instanceById);
            }

        } catch (SecurityException e) {
            logger.error("Security exception during booking fetch", e);
            String instanceById = bookingId != null ? INSTANCE_BOOKINGS + "/" + bookingId.trim() : INSTANCE_BOOKINGS;
            return unauthorized("Invalid JWT token or authentication failed", instanceById);
        } catch (IllegalStateException ex) {
            logger.error("Service unavailable during booking fetch", ex);
            String instanceById = bookingId != null ? INSTANCE_BOOKINGS + "/" + bookingId.trim() : INSTANCE_BOOKINGS;
            return serviceUnavailable("Report database is currently unavailable", instanceById);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching the booking", e);
            String instanceById = bookingId != null ? INSTANCE_BOOKINGS + "/" + bookingId.trim() : INSTANCE_BOOKINGS;
            return internalServerError("An unexpected error occurred while fetching the booking", instanceById);
        }
    }

    /**
     * Handle booking retrieval for BKON-prefixed booking IDs.
     * Looks in ECOM database, only returns data if status is D, S, T, or V.
     * If status is other (P, A, C, etc.), returns 422 error - use LRP booking number instead.
     */
    private ResponseEntity<?> handleBkonBooking(String bookingId, String customerCode, String instance) {
        logger.debug("Looking up BKON booking in ECOM database: {}", bookingId);
        
        // Find booking in ECOM database
        Optional<EcomBooking> ecomBookingOpt = ecomBookingRepository.findByEbookno(bookingId);
        
        if (!ecomBookingOpt.isPresent()) {
            logger.debug("BKON booking not found in ECOM database: {}", bookingId);
            return notFound("Booking not found: " + bookingId, instance);
        }
        
        EcomBooking ecomBooking = ecomBookingOpt.get();
        
        // Verify customer ownership (skipped for SYSUSER read-only)
        if (customerCode != null && !customerCode.equalsIgnoreCase(ecomBooking.getCustCode())) {
            logger.warn("Customer code mismatch for booking {}: JWT={}, Booking={}", 
                bookingId, customerCode, ecomBooking.getCustCode());
            return forbidden("Access denied to booking: " + bookingId, instance);
        }
        
        String status = ecomBooking.getEbookStatus();
        logger.debug("BKON booking {} has status: {}", bookingId, status);
        
        // Check if status is one of the active ECOM statuses (D, S, T, V)
        if (!isActiveEcomStatus(status)) {
            logger.info("BKON booking {} has status '{}' - use LRP booking number instead", bookingId, status);
            return buildStatusMismatchError(bookingId, status, "ECOM");
        }
        
        // Status is D, S, T, or V - fetch full booking details from ECOM
        Optional<BookingRequestDTO> result = bookingRepository.findBooking(
            customerCode, 
            bookingId, 
            null, 
            null
        );
        
        if (!result.isPresent()) {
            return notFound("Booking details not found: " + bookingId, instance);
        }
        
        // Convert to V2 response (guard against mapper returning null)
        BookingV2 response = bookingMapperV2.toV2Booking(result.get(), bookingId);
        if (response == null) {
            return notFound("Booking not found: " + bookingId, instance);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * SYSUSER read-only lookup for non-BKON booking ids (ext book no or LRP book_no).
     */
    private ResponseEntity<?> resolveSysUserNonBkonBooking(String trimmedBookingId, String instance) {
        List<EcomBooking> linked = ecomBookingRepository.findByExtBookNo(trimmedBookingId);
        if (!linked.isEmpty()) {
            EcomBooking ecomBooking = linked.get(0);
            String status = ecomBooking.getEbookStatus();
            if (isActiveEcomStatus(status)) {
                return handleBkonBooking(ecomBooking.getEbookno(), null, instance);
            }
            if (isLrpStatus(status)) {
                return handleLrpBooking(trimmedBookingId, null, instance);
            }
            return buildStatusMismatchError(trimmedBookingId, status, "LRP");
        }
        return handleLrpBooking(trimmedBookingId, null, instance);
    }

    /**
     * Handle booking retrieval for non-BKON booking IDs.
     * Fetches directly from LRP (Documentation) database.
     */
    private ResponseEntity<?> handleLrpBooking(String bookingId, String customerCode, String instance) {
        logger.debug("Looking up non-BKON booking in LRP database: {}", bookingId);
        
        // Fetch from Documentation/LRP database
        Optional<BookingRequestDTO> result = documentationBookingViewService.findByBookingNumber(bookingId);
        
        if (!result.isPresent()) {
            logger.debug("Booking not found in LRP database: {}", bookingId);
            return notFound("Booking not found: " + bookingId, instance);
        }
        
        // Convert to V2 response (guard against mapper returning null)
        BookingV2 response = bookingMapperV2.toV2Booking(result.get(), bookingId);
        if (response == null) {
            return notFound("Booking not found: " + bookingId, instance);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Check if the status is one of the active ECOM statuses (D, S, T, V).
     * These are the statuses where the booking data is still authoritative in ECOM.
     * 
     * @param status The booking status code
     * @return true if status is D (Draft), S (Submitted), T (Template), or V (Rejected)
     */
    private boolean isActiveEcomStatus(String status) {
        return status != null && ACTIVE_ECOM_STATUSES.contains(status.toUpperCase());
    }

    /**
     * Check if the status is one of the LRP statuses (P, A, C).
     * These are the statuses where the booking is managed in LRP and should be retrieved using LRP booking number.
     */
    private boolean isLrpStatus(String status) {
        return status != null && LRP_STATUSES.contains(status.toUpperCase());
    }

    /**
     * Build 422 error when booking status does not match the required statuses for the ID type used.
     * @param bookingId The booking ID used in the request
     * @param status The current ebook_status from ecombooking
     * @param idType "ECOM" or "LRP" - which ID format was used
     */
    private ResponseEntity<?> buildStatusMismatchError(String bookingId, String status, String idType) {
        String statusDesc = getStatusDescription(status);
        String detail;
        if ("ECOM".equals(idType)) {
            detail = "Booking " + bookingId + " has status '" + statusDesc + "'. Use Ecom booking number only for bookings with status Draft, Submitted, Template, or Rejected.";
        } else {
            detail = "Booking " + bookingId + " has status '" + statusDesc + "'. Use LRP booking number only for bookings with status In-Progress, Accepted, or Confirmed.";
        }
        return unprocessableEntity(detail);
    }

    // ==================== PUT /api/v2/bookings/{bookingId} ====================

    @Hidden
    @Operation(
        summary = "Update booking by id (only possible with booking request id, e.g. BKON...)",
        operationId = "updateBookingById",
        tags = { "Bookings" },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Payload to update a booking.",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookingRequestUpsertPayloadV2.class, example = BookingExamplesNew.V2_UPDATE_BOOKING_DG_JSON)
            )),
        responses = {
            @ApiResponse(responseCode = "200", description = "Booking updated successfully",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BookingCreateResponse.class),
                    examples = @ExampleObject(name = "Success", value = "{\"onlineBookNo\":\"BKON1234\",\"status\":\"D\",\"message\":\"Booking updated successfully\"}"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "503", description = "Service Unavailable",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PutMapping(
        path = "/{bookingId}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateBookingById(
            HttpServletRequest request,
            @Parameter(description = "Booking id, might be a booking request id (e.g. BKON123) or a confirmed booking id (e.g. TSHG1234)", required = true)
            @PathVariable("bookingId") String bookingId,
            @Valid @RequestBody BookingRequestUpsertPayloadV2 body) {
        String instanceById = (bookingId != null && !bookingId.trim().isEmpty()) ? INSTANCE_BOOKINGS + "/" + bookingId.trim() : INSTANCE_BOOKINGS;
        try {
            // 1) Extract JWT
            String token = extractJwtToken(request.getHeader("Authorization"));
            if (token == null) {
                return unauthorized("Missing or invalid JWT token", instanceById);
            }

            // 2) Extract customerCode and loginName from JWT
            String customerCodeFromJwt = customerContext.extractCustomerCodeFromToken(token);
            if (customerCodeFromJwt == null || customerCodeFromJwt.trim().isEmpty()) {
                return unauthorized("Customer code missing in JWT", instanceById);
            }

            String loginNameFromJwt = customerContext.extractLoginFromToken(token);
            if (loginNameFromJwt == null || loginNameFromJwt.trim().isEmpty()) {
                return unauthorized("Login name missing in JWT", instanceById);
            }

            // 3) Validate inputs (V2 body: @Valid above — failures → BookingV2ExceptionHandler)
            if (bookingId == null || bookingId.trim().isEmpty()) {
                return badRequest("Booking ID is required", INSTANCE_BOOKINGS);
            }
            if (body == null) {
                return badRequest("Request body cannot be null", instanceById);
            }

            // 3a) V1 update parity: path onlineBookNo vs body (V2: optional bookingRequestId / id vs path bookingId)
            Optional<ErrorMessage> pathBodyIdError =
                v2BookingRequestValidationService.validateUpdatePathMatchesPayloadBookingIds(bookingId, body, instanceById);
            if (pathBodyIdError.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pathBodyIdError.get());
            }

            // 3b) Normalize: clear reefer details for DC (Dry) containers
            clearReeferSettingsForDryContainers(body);

            // 4) Convert V2 request to V1 DTO (same target model as V1 API BookingController update)
            BookingRequestDTO v1Request = bookingMapperV2.toV1BookingRequest(body, customerCodeFromJwt);

            // 4a) SI-aligned container cargoWeight from commodities when present (before Bean Validation)
            BookingCargoWeightNormalizer.apply(v1Request);

            // 4b) Bean Validation: Default + UpdateBooking (containerNo required on commodities/containers for updates)
            Optional<ErrorMessage> v1ModelError =
                v2BookingRequestValidationService.validateV1BookingRequestDtoForUpdate(v1Request, instanceById);
            if (v1ModelError.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(v1ModelError.get());
            }

            // 5) Resolve BKON (pass-through for BKON path; link or shell for LRP path), then existing update
            String resolvedOnlineBookNo = bookingRepository.resolveOnlineBookNoForUpdate(
                    bookingId.trim(), v1Request, customerCodeFromJwt, loginNameFromJwt);
            v1Request.setOnlineBookNo(resolvedOnlineBookNo);

            // 5a) DG locked after agency acceptance
            Optional<ErrorMessage> dgLocked =
                dangerousGoodsLockService.validateNotLocked(resolvedOnlineBookNo, v1Request, instanceById);
            if (dgLocked.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(dgLocked.get());
            }

            BookingCreateResponse result = bookingRepository.updateBooking(
                    resolvedOnlineBookNo, v1Request, customerCodeFromJwt, loginNameFromJwt);
            if (result == null) {
                return notFound("Booking not found: " + bookingId, instanceById);
            }

            // 6) Return 200 OK with body: onlineBookNo, status, message
            if (result.getMessage() == null || result.getMessage().trim().isEmpty()) {
                result.setMessage("Booking updated successfully");
            }
            return ResponseEntity.ok().body(result);

        } catch (BookingNotFoundException e) {
            return notFound(e.getMessage(), instanceById);
        } catch (LrpBookingLinkNotAllowedException e) {
            logger.warn("LRP booking link not allowed for {}: {}", bookingId, e.getMessage());
            return unprocessableEntity(e.getMessage());
        } catch (BookingCancellationConflictException e) {
            logger.warn("Booking update blocked for {}: {}", bookingId, e.getMessage());
            return unprocessableEntity(e.getMessage());
        } catch (InvalidStatusTransitionException e) {
            logger.warn("Invalid status transition for booking {}: {}", bookingId, e.getMessage());
            return unprocessableEntity(e.getMessage());
        } catch (SecurityException e) {
            logger.error("Security exception during booking update", e);
            return forbidden("Customer code mismatch - unauthorized access", instanceById);
        } catch (IllegalStateException ex) {
            logger.error("Service unavailable during booking update", ex);
            return serviceUnavailable("Report database is currently unavailable", instanceById);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while updating the booking", e);
            return internalServerError("An unexpected error occurred while updating the booking", instanceById);
        }
    }

    // ==================== POST /api/v2/bookings/{bookingId}/cancel ====================

    @Operation(
        summary = "Cancel a submitted booking request",
        operationId = "cancelBookingRequest",
        tags = { "Bookings" },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Cancellation reason (required). Matches OpenAPI BookingRequestCancellationPayload.",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(name = "BookingRequestCancellationPayload", implementation = BookingCancelRequestDto.class),
                examples = @ExampleObject(name = "Example", value = "{\"reason\":\"Wrong order\"}")
            )),
        responses = {
            @ApiResponse(responseCode = "200", description = "Cancellation succeeded",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BookingCancelResponseV2.class),
                    examples = @ExampleObject(name = "Success",
                        value = "{\"onlineBookNo\":\"BKON1234\",\"message\":\"Submitted booking is canceled.\"}"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "503", description = "Service Unavailable",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
        }
    )
    @PostMapping(
        path = "/{bookingId}/cancel",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> cancelBooking(
            HttpServletRequest request,
            @Parameter(description = "Booking id: booking request id (e.g. BKON…) or ERP / external id (ecombooking.extBookNo) for this customer — same resolution as GET/PUT by id.", required = true)
            @PathVariable("bookingId") String bookingId,
            @Valid @RequestBody BookingCancelRequestDto body) {
        String trimmedPathId = bookingId != null ? bookingId.trim() : "";
        String instanceCancel = trimmedPathId.isEmpty()
                ? INSTANCE_BOOKINGS
                : INSTANCE_BOOKINGS + "/" + trimmedPathId + "/cancel";
        try {
            // 1) Extract JWT
            String token = extractJwtToken(request.getHeader("Authorization"));
            if (token == null) {
                return unauthorized("Missing or invalid JWT token", instanceCancel);
            }

            // 2) Customer + login from JWT (same as PUT /api/v2/bookings/{bookingId})
            String customerCodeFromJwt = customerContext.extractCustomerCodeFromToken(token);
            if (customerCodeFromJwt == null || customerCodeFromJwt.trim().isEmpty()) {
                return unauthorized("Customer code missing in JWT", instanceCancel);
            }

            String loginNameFromJwt = customerContext.extractLoginFromToken(token);
            if (loginNameFromJwt == null || loginNameFromJwt.trim().isEmpty()) {
                return unauthorized("Login name missing in JWT", instanceCancel);
            }

            // 3) Validate path and body (@Valid on body → BookingV2ExceptionHandler)
            if (trimmedPathId.isEmpty()) {
                return badRequest("Booking ID is required", INSTANCE_BOOKINGS);
            }
            instanceCancel = INSTANCE_BOOKINGS + "/" + trimmedPathId + "/cancel";
            if (body == null) {
                return badRequest("Request body cannot be null", instanceCancel);
            }

            // 4) Resolve ecombooking.ebookno (BKON* as-is; else extBookNo + customer)
            String ebookNo = resolveEcomEbookNoForCancel(trimmedPathId, customerCodeFromJwt);
            if (ebookNo == null) {
                return notFound("Booking not found: " + trimmedPathId, instanceCancel);
            }

            // 5) Cancel (ECOM + optional LRP/EDI in service)
            bookingCancelService.cancelBookingRequest(
                    ebookNo,
                    body.getReason(),
                    loginNameFromJwt,
                    customerCodeFromJwt);

            return ResponseEntity.ok(new BookingCancelResponseV2(ebookNo, CANCEL_SUCCESS_MESSAGE));

        } catch (BookingNotFoundForCancelException e) {
            return notFound(e.getMessage(), instanceCancel);
        } catch (BookingCancellationConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ErrorMessage.conflict(e.getMessage(), e.getConflictCode(), instanceCancel));
        } catch (BookingCancelAccessDeniedException e) {
            return forbidden(e.getMessage(), instanceCancel);
        } catch (SecurityException e) {
            logger.error("Security exception during booking cancel", e);
            return unauthorized("Invalid JWT token or authentication failed", instanceCancel);
        } catch (IllegalStateException ex) {
            logger.error("Service unavailable during booking cancel", ex);
            return serviceUnavailable("A required database is currently unavailable", instanceCancel);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while cancelling the booking", e);
            return internalServerError("An unexpected error occurred while cancelling the booking", instanceCancel);
        }
    }

    // ==================== VGM (Verified Gross Mass) — same base path as other V2 booking operations ====================

    @GetMapping(
        path = "/{bookingId}/vgm",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Get VGM info for a booking",
        operationId = "getVgm",
        tags = { "Bookings" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Response object containing a VGM info.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = VGMInfo.class),
                examples = @ExampleObject(name = "Success",
                    value = BookingExamplesNew.VGM_V2_BOOKING_GET_PUT_JSON))),
        @ApiResponse(responseCode = "401", description = "Authentication failed (Missing or invalid Bearer token).",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "403", description = "The client does not have access rights to the content.",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "404", description = "The resource could not be found.",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "500", description = "An unexpected server-side error occurred.",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "503", description = "The service is currently not available.",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<?> getVGMDetails(
            HttpServletRequest request,
            @Parameter(
                description = "Booking id, might be a booking request id (e.g. BKON123) or a confirmed booking id (e.g. TSHG1234)",
                required = true)
            @PathVariable("bookingId") String bookingId) {
        String trimmed = bookingId != null ? bookingId.trim() : "";
        String instanceVgm = trimmed.isEmpty() ? INSTANCE_BOOKINGS : INSTANCE_BOOKINGS + "/" + trimmed + "/vgm";
        try {
            String token = extractJwtToken(request.getHeader("Authorization"));
            if (token == null) {
                return unauthorized("Missing or invalid Bearer token", instanceVgm);
            }
            String customerCode = customerContext.resolveCustomerCodeForBookingDetailRead(token);
            if (trimmed.isEmpty()) {
                return badRequest("Booking ID is required", INSTANCE_BOOKINGS);
            }
            instanceVgm = INSTANCE_BOOKINGS + "/" + trimmed + "/vgm";

            String custArg = customerCode != null ? customerCode.trim() : null;
            VGMInfo response = vgmRepository.getVGMDetails(trimmed, custArg);
            return ResponseEntity.ok(response);
        } catch (BookingNotFoundException e) {
            return notFound(e.getMessage() != null ? e.getMessage() : "Booking not found or void", instanceVgm);
        } catch (BookingVgmForbiddenException e) {
            return forbidden(e.getMessage() != null ? e.getMessage() : "Forbidden", instanceVgm);
        } catch (SecurityException e) {
            logger.error("Security exception during VGM read", e);
            return unauthorized(e.getMessage() != null ? e.getMessage() : "Unauthorized", instanceVgm);
        } catch (IllegalStateException ex) {
            logger.error("Service unavailable during VGM read", ex);
            return serviceUnavailable("Documentation or ECOM database is currently unavailable", instanceVgm);
        } catch (Exception e) {
            logger.error("Unexpected error while fetching VGM", e);
            return internalServerError("An unexpected error occurred while fetching VGM", instanceVgm);
        }
    }

    @PutMapping(
        path = "/{bookingId}/vgm",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Upsert VGM info for a booking",
        operationId = "upsertVgm",
        tags = { "Bookings" },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Payload to create/replace vgm information",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = VGMCreateRequest.class),
                examples = @ExampleObject(name = "Example",
                    value = BookingExamplesNew.VGM_V2_BOOKING_GET_PUT_JSON)))
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "VGM saved successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = VgmUpdateResponse.class),
                examples = @ExampleObject(name = "Success",
                    value = "{\"BookNo\":\"TSHGCGP26008949\",\"message\":\"VGM updated successfully\"}"))),
        @ApiResponse(responseCode = "400", description = "The server could not understand the request due to invalid syntax or parameters.",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "401", description = "Authentication failed (Missing or invalid Bearer token).",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "403", description = "The client does not have access rights to the content.",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "404", description = "The resource could not be found.",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "500", description = "An unexpected server-side error occurred.",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "503", description = "The service is currently not available.",
            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public ResponseEntity<?> saveVGMDetails(
            HttpServletRequest request,
            @Parameter(
                description = "Booking id, might be a booking request id (e.g. BKON123) or a confirmed booking id (e.g. TSHG1234)",
                required = true)
            @PathVariable("bookingId") String bookingId,
            @Valid @RequestBody VGMCreateRequest body) {
        String trimmed = bookingId != null ? bookingId.trim() : "";
        String instanceVgm = trimmed.isEmpty() ? INSTANCE_BOOKINGS : INSTANCE_BOOKINGS + "/" + trimmed + "/vgm";
        try {
            // Same JWT / customer pattern as PUT /api/v2/bookings/{bookingId} (no Spring Security principal).
            String token = extractJwtToken(request.getHeader("Authorization"));
            if (token == null) {
                return unauthorized("Missing or invalid Bearer token", instanceVgm);
            }
            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            if (customerCode == null || customerCode.trim().isEmpty()) {
                return unauthorized("Customer code missing in JWT", instanceVgm);
            }
            String loginUser = customerContext.extractLoginFromToken(token);
            if (loginUser == null || loginUser.trim().isEmpty()) {
                return unauthorized("Login name missing in JWT", instanceVgm);
            }
            if (trimmed.isEmpty()) {
                return badRequest("Booking ID is required", INSTANCE_BOOKINGS);
            }
            if (body == null) {
                return badRequest("Request body cannot be null", instanceVgm);
            }
            instanceVgm = INSTANCE_BOOKINGS + "/" + trimmed + "/vgm";

            VGMInfo mapped = mapToVgmInfo(body);
            vgmRepository.saveVGMDetails(trimmed, mapped, customerCode.trim(), loginUser.trim());
            return ResponseEntity.ok(new VgmUpdateResponse(trimmed, "VGM updated successfully"));
        } catch (BookingNotFoundException e) {
            return notFound(e.getMessage() != null ? e.getMessage() : "Booking not found or void", instanceVgm);
        } catch (BookingVgmForbiddenException e) {
            return forbidden(e.getMessage() != null ? e.getMessage() : "Forbidden", instanceVgm);
        } catch (ContainerNotFoundException e) {
            return invalidContainerVgm(
                e.getMessage() != null ? e.getMessage() : "Container not in booking", instanceVgm);
        } catch (IllegalArgumentException e) {
            return vgmValidationFailed(e.getMessage(), instanceVgm);
        } catch (SecurityException e) {
            logger.error("Security exception during VGM save", e);
            return unauthorized(e.getMessage() != null ? e.getMessage() : "Unauthorized", instanceVgm);
        } catch (IllegalStateException ex) {
            logger.error("Service unavailable during VGM save", ex);
            return serviceUnavailable("Documentation or ECOM database is currently unavailable", instanceVgm);
        } catch (Exception e) {
            IllegalArgumentException iae = findCause(e, IllegalArgumentException.class);
            if (iae != null) {
                // Some transactional/proxy layers wrap IllegalArgumentException; surface it as a 400 validation error.
                return vgmValidationFailed(iae.getMessage(), instanceVgm);
            }
            logger.error("Unexpected error while saving VGM", e);
            return internalServerError("An unexpected error occurred while saving VGM", instanceVgm);
        }
    }

    private ResponseEntity<ErrorMessage> vgmValidationFailed(String message, String instance) {
        String detail = (message == null || message.trim().isEmpty())
            ? "Booking data validation failed. Please review the provided data."
            : message.trim();

        ErrorMessage error = new ErrorMessage(detail, "Validation Failed", 400);
        error.setInstance(instance);
        error.setCode("VALIDATION_FAILED");
        error.setType(ErrorMessage.TYPE_VALIDATION_FAILED);

        String m = detail.toLowerCase(Locale.ROOT);
        String nestedCode;
        String pointer = "#/containers";
        if (m.contains("container count mismatch")) {
            nestedCode = "CONTAINER_COUNT_MISMATCH";
        } else if (m.contains("invalid equipmenttype")) {
            nestedCode = "INVALID_EQUIPMENT_TYPE";
        } else if (m.contains("count mismatch") && m.contains("equipmenttype")) {
            nestedCode = "EQUIPMENT_TYPE_COUNT_MISMATCH";
        } else if (m.contains("validation failed for one or more containers")) {
            nestedCode = "VGM_MULTI_CONTAINER_VALIDATION_FAILED";
        } else if (m.contains("invalid vgm weight") && m.contains("greater than tare")) {
            nestedCode = "VGM_BELOW_OR_EQUAL_TARE";
        } else if (m.contains("tare weight not available")) {
            nestedCode = "TARE_WEIGHT_UNAVAILABLE";
        } else if (m.contains("invalid tare weight")) {
            nestedCode = "INVALID_TARE_WEIGHT";
        } else if (m.contains("50000") && m.contains("kilos/50 tons")) {
            nestedCode = "VGM_EXCEEDS_MAX_WEIGHT";
        } else {
            nestedCode = "VGM_VALIDATION_FAILED";
        }
        error.addNestedError(detail, pointer, nestedCode);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private static <T extends Throwable> T findCause(Throwable ex, Class<T> clazz) {
        if (ex == null || clazz == null) {
            return null;
        }
        Throwable cur = ex;
        int guard = 0;
        while (cur != null && guard++ < 50) {
            if (clazz.isInstance(cur)) {
                return clazz.cast(cur);
            }
            cur = cur.getCause();
        }
        return null;
    }

    private VGMInfo mapToVgmInfo(VGMCreateRequest body) {
        if (body == null) {
            return null;
        }
        return VGMInfo.builder()
                .authorizedPerson(body.getAuthorizedPerson())
                .obtainedAt(body.getObtainedAt())
                .method(body.getMethod())
                .referenceNumber(body.getReferenceNumber())
                .orderNumber(body.getOrderNumber())
                .companyName(body.getCompanyName())
                .phone(body.getPhone())
                .email(body.getEmail())
                .address(mapToAddress(body.getAddress()))
                .containers(mapToContainerList(body.getContainers()))
                .build();
    }

    private Address mapToAddress(AddressRequest req) {
        if (req == null) {
            return null;
        }
        String countryCode = req.getCountryCode();
        if (countryCode != null && !countryCode.trim().isEmpty()) {
            countryCode = countryCode.trim().toUpperCase(Locale.ROOT);
        }
        return Address.builder()
                .lines(req.getLines())
                .locality(req.getLocality())
                .countryCode(countryCode)
                .postalCode(req.getPostalCode())
                .administrativeArea(req.getAdministrativeArea())
                .build();
    }

    private List<ContainerVGM> mapToContainerList(List<ContainerVGMRequest> containers) {
        if (containers == null) {
            return null;
        }
        List<ContainerVGM> out = new ArrayList<>();
        for (ContainerVGMRequest c : containers) {
            if (c == null) {
                continue;
            }
            out.add(ContainerVGM.builder()
                    .equipmentId(c.getEquipmentId())
                    .equipmentType(c.getEquipmentType())
                    .weight(c.getWeight())
                    .unit(c.getUnit())
                    .build());
        }
        return out;
    }

    private ResponseEntity<ErrorMessage> invalidContainerVgm(String message, String instance) {
        ErrorMessage body = ErrorMessage.badRequest(message, instance);
        body.setCode("INVALID_CONTAINER");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ==================== Helper Methods ====================

    /**
     * Resolves {@code ecombooking.ebookno} for cancel: BKON* uses path as ebookno; else match {@code extBookNo} + customer (same idea as GET by id).
     */
    private String resolveEcomEbookNoForCancel(String bookingId, String customerCode) {
        if (bookingId == null || bookingId.isEmpty()) {
            return null;
        }
        String id = bookingId.trim();
        if (id.toUpperCase().startsWith(BKON_PREFIX)) {
            return id;
        }
        return ecomBookingRepository.findByExtBookNoAndCustCode(id, customerCode)
                .map(EcomBooking::getEbookno)
                .orElse(null);
    }

    /**
     * Clear reefer settings on any container that is DC (Dry). Reefer details must be empty for dry containers.
     */
    private void clearReeferSettingsForDryContainers(BookingRequestUpsertPayloadV2 body) {
        if (body == null || body.getContainers() == null) return;
        for (BookingContainerV2 container : body.getContainers()) {
            if (container != null && container.getType() == EquipmentType.D) {
                container.setReeferSettings(null);
            }
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
     * Convert BookingSummaryResponseDto to PagedBookingListItemV2.
     * Port names from DTO (originName, deliveryName, polName, podName); POL/POD IDs from loadPortId/dischargePortId with fallback to origin/final.
     */
    /**
     * Converts to PagedBookingListItem so response matches OpenAPI PagedBookingListItem schema.
     * Required fields (bookingRequestId, originId, originName, deliveryId, deliveryName, polId, polName, podId, podName, status, createdBy, createdAt) are never null; use empty string when missing.
     */
    private PagedBookingListItemV2 convertToListItem(com.solverminds.booking.dto.BookingSummaryResponseDto summary) {
        PagedBookingListItemV2 item = new PagedBookingListItemV2();
        String empty = "";

        item.setId(summary.getBookingNumber());
        item.setBookingRequestId(nullToEmpty(summary.getOnlineBookNo()));
        item.setOriginId(nullToEmpty(summary.getOriginPortId()));
        item.setOriginName(firstNonEmpty(summary.getOriginName(), summary.getOriginPortId(), empty));
        item.setDeliveryId(nullToEmpty(summary.getFinalPortId()));
        item.setDeliveryName(firstNonEmpty(summary.getDeliveryName(), summary.getFinalPortId(), empty));
        item.setPolId(firstNonEmpty(summary.getLoadPortId(), summary.getOriginPortId(), empty));
        item.setPolName(firstNonEmpty(summary.getPolName(), summary.getLoadPortId(), empty));
        item.setPodId(firstNonEmpty(summary.getDischargePortId(), summary.getFinalPortId(), empty));
        item.setPodName(firstNonEmpty(summary.getPodName(), summary.getDischargePortId(), empty));
        item.setAgencyId(summary.getAgencyId());
        item.setAgencyRefNo(summary.getAgencyRefNo());
        item.setCreatedAt(summary.getCreatedDate());
        item.setCreatedBy(nullToEmpty(summary.getBookingCreatedUser()));
        item.setUpdatedAt(summary.getChangeDate());
        item.setTeus(summary.getTuesCount());
        OffsetDateTime etd = summary.getDepartureDate();
        item.setEtd(etd);
        OffsetDateTime eta = summary.getEta() != null
            ? summary.getEta().atOffset(ZoneOffset.UTC)
            : etd;
        item.setEta(eta);
        item.setConfirmedAt(summary.getConfirmedDate());
        item.setBlNumber(summary.getBlNumber());
        if (summary.getBlStatus() != null && !summary.getBlStatus().trim().isEmpty()) {
            try {
                item.setBlStatus(BlStatus.fromValue(summary.getBlStatus()));
            } catch (IllegalArgumentException e) {
                item.setBlStatus(null);
            }
        } else {
            item.setBlStatus(null);
        }

        item.setStatus(summary.getStatus() != null ? summary.getStatus() : BookingStatus.D);

        return item;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String firstNonEmpty(String first, String fallback, String defaultVal) {
        if (first != null && !first.trim().isEmpty()) return first;
        if (fallback != null && !fallback.trim().isEmpty()) return fallback;
        return defaultVal != null ? defaultVal : "";
    }

    /**
     * Check if item matches filter criteria
     */
    private boolean matchesFilter(PagedBookingListItemV2 item, BookingFilterV2 filter) {
        if (filter.getIdContains() != null && !filter.getIdContains().isEmpty()) {
            if (item.getId() == null || !item.getId().toLowerCase().contains(filter.getIdContains().toLowerCase())) {
                return false;
            }
        }
        if (filter.getStatusEquals() != null && !filter.getStatusEquals().isEmpty()) {
            if (item.getStatus() == null || !item.getStatus().getValue().equalsIgnoreCase(filter.getStatusEquals())) {
                return false;
            }
        }
        if (filter.getOriginIdContains() != null && !filter.getOriginIdContains().isEmpty()) {
            if (item.getOriginId() == null || !item.getOriginId().toLowerCase().contains(filter.getOriginIdContains().toLowerCase())) {
                return false;
            }
        }
        if (filter.getOriginNameContains() != null && !filter.getOriginNameContains().isEmpty()) {
            if (item.getOriginName() == null || !item.getOriginName().toLowerCase().contains(filter.getOriginNameContains().toLowerCase())) {
                return false;
            }
        }
        if (filter.getDeliveryIdContains() != null && !filter.getDeliveryIdContains().isEmpty()) {
            if (item.getDeliveryId() == null || !item.getDeliveryId().toLowerCase().contains(filter.getDeliveryIdContains().toLowerCase())) {
                return false;
            }
        }
        if (filter.getDeliveryNameContains() != null && !filter.getDeliveryNameContains().isEmpty()) {
            if (item.getDeliveryName() == null || !item.getDeliveryName().toLowerCase().contains(filter.getDeliveryNameContains().toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    // ==================== RFC 7807 Error Response Methods ====================

    private ResponseEntity<ErrorMessage> badRequest(String detail) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.badRequest(detail));
    }

    private ResponseEntity<ErrorMessage> badRequest(String detail, String instance) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.badRequest(detail, instance));
    }

    private ResponseEntity<ErrorMessage> unauthorized(String detail) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.unauthorized(detail));
    }

    private ResponseEntity<ErrorMessage> unauthorized(String detail, String instance) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.unauthorized(detail, instance));
    }

    private ResponseEntity<ErrorMessage> forbidden(String detail) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorMessage.forbidden(detail));
    }

    private ResponseEntity<ErrorMessage> forbidden(String detail, String instance) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorMessage.forbidden(detail, instance));
    }

    private ResponseEntity<ErrorMessage> notFound(String detail) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorMessage.notFound(detail));
    }

    private ResponseEntity<ErrorMessage> notFound(String detail, String instance) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorMessage.notFound(detail, instance));
    }

    private ResponseEntity<ErrorMessage> internalServerError(String detail) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorMessage.internalServerError(detail));
    }

    private ResponseEntity<ErrorMessage> internalServerError(String detail, String instance) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorMessage.internalServerError(detail, instance));
    }

    private ResponseEntity<ErrorMessage> serviceUnavailable(String detail) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ErrorMessage.serviceUnavailable(detail));
    }

    private ResponseEntity<ErrorMessage> serviceUnavailable(String detail, String instance) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ErrorMessage.serviceUnavailable(detail, instance));
    }

    private ResponseEntity<ErrorMessage> unprocessableEntity(String detail) {
        ErrorMessage error = new ErrorMessage();
        error.setStatus(422);
        error.setTitle("Unprocessable Entity");
        error.setDetail(detail);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    /**
     * Get human-readable status description.
     */
    private String getStatusDescription(String status) {
        if (status == null) return "Unknown";
        switch (status.toUpperCase()) {
            case "D": return "Draft";
            case "S": return "Submitted";
            case "T": return "Template";
            case "V": return "Rejected";
            case "C": return "Confirmed";
            case "P": return "In-Progress";
            case "A": return "Accepted";
            default: return "Unknown";
        }
    }
}

