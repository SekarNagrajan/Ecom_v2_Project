package com.solverminds.booking.validation;

import com.solverminds.booking.dto.v2.ErrorMessage;
import com.solverminds.booking.entity.EcomBooking;
import com.solverminds.booking.repo.EcomBookingRepository;
import com.svm.ecom.dto.ErrorResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Reusable ECOM booking status validation that mirrors the V2 check performed in
 * {@code BookingControllerV2.handleBkonBooking} / {@code buildStatusMismatchError}.
 *
 * <p>An ECOM (BKON*) booking is editable / referenceable only while it is in one of the
 * active ECOM statuses (D, S, T, V — Draft, Submitted, Template, Rejected). When the
 * booking exists with any other status (e.g. P, A, C), this service produces the same
 * 422 {@link ErrorMessage} payload that V2 returns, so V1 endpoints can enforce the
 * identical behavior without duplicating the rule.</p>
 *
 * <p>This service intentionally exposes only the status check; lookup / 404 / 403 paths
 * remain the caller's responsibility (preserving each controller's existing flow).</p>
 */
@Service
public class BookingStatusValidationService {

    /** Same set as {@code BookingControllerV2.ACTIVE_ECOM_STATUSES}. */
    private static final Set<String> ACTIVE_ECOM_STATUSES =
            new HashSet<>(Arrays.asList("D", "S", "T", "V", "I"));

    private static final String ACCEPTED_STATUS = "A";

    private final EcomBookingRepository ecomBookingRepository;

    public BookingStatusValidationService(EcomBookingRepository ecomBookingRepository) {
        this.ecomBookingRepository = ecomBookingRepository;
    }

    /**
     * Validate that the booking referenced by {@code onlineBookNo} is in an active ECOM
     * status (D, S, T, V). Returns an empty Optional when:
     * <ul>
     *   <li>{@code onlineBookNo} is null/blank (nothing to check — e.g. POST create without a body id)</li>
     *   <li>the booking does not exist (lookup / 404 handling is left to the caller)</li>
     *   <li>the current status is one of D, S, T, V</li>
     * </ul>
     * When the booking exists with any other status, returns the same V2-format 422
     * {@link ErrorMessage} returned by {@code BookingControllerV2}.
     *
     * @param onlineBookNo The ECOM booking number (e.g. {@code BKON706}).
     * @return Optional containing the 422 {@link ErrorMessage} when status is not active; empty otherwise.
     */
    public Optional<ErrorMessage> validateEcomBookingStatus(String onlineBookNo) {
        if (onlineBookNo == null || onlineBookNo.trim().isEmpty()) {
            return Optional.empty();
        }
        Optional<EcomBooking> existing = ecomBookingRepository.findByEbookno(onlineBookNo.trim());
        if (!existing.isPresent()) {
            return Optional.empty();
        }
        String status = existing.get().getEbookStatus();
        if (isActiveEcomStatus(status)) {
            return Optional.empty();
        }
        return Optional.of(buildStatusMismatchError(onlineBookNo.trim(), status));
    }

    /**
     * Same predicate as {@code BookingControllerV2.isActiveEcomStatus(String)}: true when
     * the status is one of D (Draft), S (Submitted), T (Template), V (Rejected).
     */
    public boolean isActiveEcomStatus(String status) {
        return status != null && ACTIVE_ECOM_STATUSES.contains(status.toUpperCase());
    }

    /**
     * True when the ECOM booking has status A (Accepted) — eligible for LRP redirect on view when
     * {@code bookingNumber} is supplied.
     */
    public boolean isAcceptedStatus(String status) {
        return status != null && ACCEPTED_STATUS.equalsIgnoreCase(status.trim());
    }

    /**
     * When both {@code onlineBookNo} and {@code bookingNumber} are supplied on view, the booking
     * number must match {@code ecombooking.extBookNo} (case-insensitive).
     *
     * @param extBookNo Linked LRP booking number from {@code ecombooking}
     * @param bookingNumber Requested booking number
     * @return 422 {@link ErrorResponse} when {@code bookingNumber} does not match; empty when valid
     */
    public Optional<ErrorResponse> validateBookingNumberMatchesExtBookNo(String extBookNo, String bookingNumber) {
        if (bookingNumber == null || bookingNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        if (extBookNo == null || extBookNo.trim().isEmpty()
                || !bookingNumber.trim().equalsIgnoreCase(extBookNo.trim())) {
            return Optional.of(BookingViewErrorResponses.invalidBookingNumber(bookingNumber.trim()));
        }
        return Optional.empty();
    }

    /**
     * 422 for GET /api/v1/booking/view when ECOM status does not allow the requested lookup path.
     */
    public ErrorResponse buildViewStatusMismatchError(String onlineBookNo, String status) {
        return BookingViewErrorResponses.statusMismatch(onlineBookNo, status);
    }

    /**
     * Builds the same 422 ECOM "status mismatch" {@link ErrorMessage} produced by
     * {@code BookingControllerV2.buildStatusMismatchError(..., "ECOM")} +
     * {@code BookingControllerV2.unprocessableEntity(detail)}. Fields {@code type},
     * {@code instance}, {@code code} remain null and {@code errors} is an empty list,
     * matching the V2 wire format.
     */
    public ErrorMessage buildStatusMismatchError(String bookingId, String status) {
        String statusDesc = getStatusDescription(status);
        String detail = "Booking " + bookingId + " has status '" + statusDesc
                + "'. Use Ecom booking number only for bookings with status Draft, Submitted, Template, or Rejected.";
        ErrorMessage error = new ErrorMessage();
        error.setStatus(422);
        error.setTitle("Unprocessable Entity");
        error.setDetail(detail);
        return error;
    }

    /**
     * Same status-code → human description mapping used by
     * {@code BookingControllerV2.getStatusDescription(String)}.
     */
    private String getStatusDescription(String status) {
        return BookingStatusDescriptions.describe(status);
    }
}
