package com.solverminds.booking.validation;

import com.svm.ecom.dto.ErrorResponse;
import com.svm.ecom.error.ErrorCodes;
import com.svm.ecom.error.ErrorReason;

import java.time.LocalDateTime;

/**
 * Standard {@link ErrorResponse} payloads for GET /api/v1/booking/view,
 * matching {@code BookingController} helpers (statusCode, errors[], field, value).
 */
public final class BookingViewErrorResponses {

    static final String NOT_FOUND_MESSAGE = "Booking not found for given parameters";

    static final String INVALID_BOOKING_NUMBER_MESSAGE =

    "LRP booking number is not linked with the provided online booking number.";

    private BookingViewErrorResponses() {
    }

    public static ErrorResponse notFound(String field, String value) {
        ErrorResponse errorResponse = new ErrorResponse(404, "Not Found", LocalDateTime.now());
        errorResponse.addError(
                ErrorCodes.NOT_FOUND,
                field,
                value,
                ErrorReason.NOT_FOUND,
                NOT_FOUND_MESSAGE);
        return errorResponse;
    }

    public static ErrorResponse invalidBookingNumber(String bookingNumber) {
        ErrorResponse errorResponse = new ErrorResponse(422, "Unprocessable Entity", LocalDateTime.now());
        errorResponse.addError(
                ErrorCodes.VALIDATION_GENERIC,
                "bookingNumber",
                bookingNumber,
                ErrorReason.VALIDATION_ERROR,
                INVALID_BOOKING_NUMBER_MESSAGE);
        return errorResponse;
    }

    public static ErrorResponse statusMismatch(String onlineBookNo, String ecomStatus) {
        String statusDesc = BookingStatusDescriptions.describe(ecomStatus);
        String message = "Booking " + onlineBookNo + " has status '" + statusDesc
                + "'. Use Ecom booking number only for bookings with status Draft, Submitted, Template, or Rejected.";
        ErrorResponse errorResponse = new ErrorResponse(422, "Unprocessable Entity", LocalDateTime.now());
        errorResponse.addError(
                ErrorCodes.BUSINESS_RULE_VIOLATION,
                "onlineBookNo",
                onlineBookNo,
                ErrorReason.BUSINESS_RULE,
                message);
        return errorResponse;
    }
}
