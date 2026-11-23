package com.solverminds.booking.service;

import com.solverminds.booking.dto.BookingRequestDTO;
import com.solverminds.booking.entity.EcomBooking;
import com.solverminds.booking.repo.EcomBookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Update-time validation for {@code ecombooking} links between {@code ebookno} and {@code extBookNo}.
 */
@Service
public class BookingUpdateLinkedNumberValidationService {

    static final String LINKED_BOOKING_NUMBER_MESSAGE =
            "The provided booking number is already linked to an online booking number.";

    static final String BOOKING_NUMBER_MISMATCH_MESSAGE =
            "Booking number is not linked to the online booking number.";

    static final String ONLINE_BOOK_NO_MISMATCH_MESSAGE =
            "Online booking number is not linked to the booking number.";

    private final EcomBookingRepository ecomBookingRepository;

    public BookingUpdateLinkedNumberValidationService(EcomBookingRepository ecomBookingRepository) {
        this.ecomBookingRepository = ecomBookingRepository;
    }

    /**
     * When {@code body.onlineBookNo} is blank and a booking number is present (body or LRP path),
     * reject if {@code ecombooking.extBookNo} is already linked to an {@code ebookno}.
     *
     * @return field/value/message for {@code badRequest(...)} when validation fails
     */
    public Optional<ValidationError> validateBlankOnlineBookNoWithBookingNumber(
            String pathBookingId, BookingRequestDTO body, boolean isBkonPath) {
        if (body == null || !isBlank(body.getOnlineBookNo())) {
            return Optional.empty();
        }

        String bookingNumber = resolveBookingNumber(body.getBookingNumber(), pathBookingId, isBkonPath);
        if (bookingNumber == null) {
            return Optional.empty();
        }

        List<EcomBooking> linked = ecomBookingRepository.findByExtBookNo(bookingNumber);
        if (linked.isEmpty()) {
            return Optional.empty();
        }

        String rawOnlineBookNo = body.getOnlineBookNo() != null ? body.getOnlineBookNo() : "";
        return Optional.of(new ValidationError("onlineBookNo", rawOnlineBookNo, LINKED_BOOKING_NUMBER_MESSAGE));
    }

    /**
     * When {@code ecombooking} links {@code ebookno} to {@code extBookNo}, the request must supply
     * matching {@code onlineBookNo} and {@code bookingNumber} (case-insensitive). Skipped when no
     * link exists in the database.
     */
    public Optional<ValidationError> validateOnlineBookNoBookingNumberRelationship(
            String pathBookingId, BookingRequestDTO body, boolean isBkonPath) {
        if (body == null) {
            return Optional.empty();
        }

        String onlineBookNo = resolveOnlineBookNo(body.getOnlineBookNo(), pathBookingId, isBkonPath);
        if (onlineBookNo != null && body.getBookingNumber() != null) {
            Optional<ValidationError> forwardError = validateForwardRelationship(onlineBookNo, body.getBookingNumber());
            if (forwardError.isPresent()) {
                return forwardError;
            }
        }

        String bookingNumber = resolveBookingNumber(body.getBookingNumber(), pathBookingId, isBkonPath);
        if (bookingNumber != null) {
            return validateReverseRelationship(bookingNumber, body.getOnlineBookNo());
        }

        return Optional.empty();
    }

    private Optional<ValidationError> validateForwardRelationship(String onlineBookNo, String bodyBookingNumber) {
        Optional<EcomBooking> row = ecomBookingRepository.findByEbookno(onlineBookNo);
        if (!row.isPresent()) {
            return Optional.empty();
        }

        String extBookNo = row.get().getExtBookNo();
        if (isBlank(extBookNo)) {
            return Optional.empty();
        }

        if (isBlank(bodyBookingNumber) || !bodyBookingNumber.trim().equalsIgnoreCase(extBookNo.trim())) {
            String rawBookingNumber = bodyBookingNumber != null ? bodyBookingNumber : "";
            return Optional.of(new ValidationError("bookingNumber", rawBookingNumber, BOOKING_NUMBER_MISMATCH_MESSAGE));
        }

        return Optional.empty();
    }

    private Optional<ValidationError> validateReverseRelationship(String bookingNumber, String bodyOnlineBookNo) {
        List<EcomBooking> linked = ecomBookingRepository.findByExtBookNo(bookingNumber);
        if (linked.isEmpty()) {
            return Optional.empty();
        }

        String dbOnlineBookNo = linked.get(0).getEbookno();
        if (isBlank(dbOnlineBookNo)) {
            return Optional.empty();
        }

        if (isBlank(bodyOnlineBookNo) || !bodyOnlineBookNo.trim().equalsIgnoreCase(dbOnlineBookNo.trim())) {
            String rawOnlineBookNo = bodyOnlineBookNo != null ? bodyOnlineBookNo : "";
            return Optional.of(new ValidationError("onlineBookNo", rawOnlineBookNo, ONLINE_BOOK_NO_MISMATCH_MESSAGE));
        }

        return Optional.empty();
    }

    private static String resolveOnlineBookNo(String bodyOnlineBookNo, String pathBookingId, boolean isBkonPath) {
        if (!isBlank(bodyOnlineBookNo)) {
            return bodyOnlineBookNo.trim();
        }
        if (isBkonPath && !isBlank(pathBookingId)) {
            return pathBookingId.trim();
        }
        return null;
    }

    private static String resolveBookingNumber(String bodyBookingNumber, String pathBookingId, boolean isBkonPath) {
        if (!isBlank(bodyBookingNumber)) {
            return bodyBookingNumber.trim();
        }
        if (!isBkonPath && !isBlank(pathBookingId)) {
            return pathBookingId.trim();
        }
        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static final class ValidationError {
        private final String field;
        private final String value;
        private final String message;

        public ValidationError(String field, String value, String message) {
            this.field = field;
            this.value = value;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getValue() {
            return value;
        }

        public String getMessage() {
            return message;
        }
    }
}
