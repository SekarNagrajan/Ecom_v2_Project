package com.solverminds.booking.api;

import com.solverminds.booking.dto.v2.ErrorMessage;
import com.solverminds.booking.dto.v2.vgm.VgmUpdateResponse;
import com.solverminds.booking.dto.v3.vgm.VgmInfoV3;
import com.solverminds.booking.exception.BookingNotFoundException;
import com.solverminds.booking.exception.BookingVgmForbiddenException;
import com.solverminds.booking.exception.ContainerNotFoundException;
import com.solverminds.booking.security.CustomerContext;
import com.solverminds.booking.service.VgmServiceV3;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;

/**
 * V3 Booking API — VGM only. v2 GET/PUT {@code /api/v2/bookings/{bookingId}/vgm} is unchanged.
 */
@RestController
@RequestMapping("/api/v3/bookings")
@Tag(name = "Bookings V3", description = "V3 booking operations. VGM fields are maintained per container.")
@SecurityRequirement(name = "bearerAuth")
public class BookingControllerV3 {

    private static final Logger logger = LoggerFactory.getLogger(BookingControllerV3.class);
    private static final String INSTANCE_BOOKINGS = "/api/v3/bookings";

    private final CustomerContext customerContext;
    private final VgmServiceV3 vgmServiceV3;

    public BookingControllerV3(CustomerContext customerContext, VgmServiceV3 vgmServiceV3) {
        this.customerContext = customerContext;
        this.vgmServiceV3 = vgmServiceV3;
    }

    @GetMapping(
        path = "/{bookingId}/vgm",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Get VGM info for a booking",
        operationId = "getVgmV3",
        tags = { "Bookings V3" }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Response object containing VGM info per container.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = VgmInfoV3.class),
                examples = @ExampleObject(name = "Success",
                    value = BookingExamplesNew.VGM_V3_BOOKING_GET_PUT_JSON))),
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
            VgmInfoV3 response = vgmServiceV3.getVGMDetails(trimmed, custArg);
            return ResponseEntity.ok(response);
        } catch (BookingNotFoundException e) {
            return notFound(e.getMessage() != null ? e.getMessage() : "Booking not found or void", instanceVgm);
        } catch (BookingVgmForbiddenException e) {
            return forbidden(e.getMessage() != null ? e.getMessage() : "Forbidden", instanceVgm);
        } catch (SecurityException e) {
            logger.error("Security exception during VGM v3 read", e);
            return unauthorized(e.getMessage() != null ? e.getMessage() : "Unauthorized", instanceVgm);
        } catch (IllegalStateException ex) {
            logger.error("Service unavailable during VGM v3 read", ex);
            return serviceUnavailable("Documentation or ECOM database is currently unavailable", instanceVgm);
        } catch (Exception e) {
            logger.error("Unexpected error while fetching VGM v3", e);
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
        operationId = "upsertVgmV3",
        tags = { "Bookings V3" },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Payload to create/replace per-container VGM information",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = VgmInfoV3.class),
                examples = @ExampleObject(name = "Example",
                    value = BookingExamplesNew.VGM_V3_BOOKING_GET_PUT_JSON)))
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
            @Valid @RequestBody VgmInfoV3 body) {
        String trimmed = bookingId != null ? bookingId.trim() : "";
        String instanceVgm = trimmed.isEmpty() ? INSTANCE_BOOKINGS : INSTANCE_BOOKINGS + "/" + trimmed + "/vgm";
        try {
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

            vgmServiceV3.saveVGMDetails(trimmed, body, customerCode.trim(), loginUser.trim());
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
            logger.error("Security exception during VGM v3 save", e);
            return unauthorized(e.getMessage() != null ? e.getMessage() : "Unauthorized", instanceVgm);
        } catch (IllegalStateException ex) {
            logger.error("Service unavailable during VGM v3 save", ex);
            return serviceUnavailable("Documentation or ECOM database is currently unavailable", instanceVgm);
        } catch (Exception e) {
            IllegalArgumentException iae = findCause(e, IllegalArgumentException.class);
            if (iae != null) {
                return vgmValidationFailed(iae.getMessage(), instanceVgm);
            }
            logger.error("Unexpected error while saving VGM v3", e);
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

    private ResponseEntity<ErrorMessage> invalidContainerVgm(String message, String instance) {
        ErrorMessage body = ErrorMessage.badRequest(message, instance);
        body.setCode("INVALID_CONTAINER");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
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

    private String extractJwtToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private ResponseEntity<ErrorMessage> badRequest(String detail, String instance) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.badRequest(detail, instance));
    }

    private ResponseEntity<ErrorMessage> unauthorized(String detail, String instance) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.unauthorized(detail, instance));
    }

    private ResponseEntity<ErrorMessage> forbidden(String detail, String instance) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorMessage.forbidden(detail, instance));
    }

    private ResponseEntity<ErrorMessage> notFound(String detail, String instance) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorMessage.notFound(detail, instance));
    }

    private ResponseEntity<ErrorMessage> internalServerError(String detail, String instance) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorMessage.internalServerError(detail, instance));
    }

    private ResponseEntity<ErrorMessage> serviceUnavailable(String detail, String instance) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ErrorMessage.serviceUnavailable(detail, instance));
    }
}
