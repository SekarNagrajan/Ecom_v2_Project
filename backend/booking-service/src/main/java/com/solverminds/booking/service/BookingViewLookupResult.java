package com.solverminds.booking.service;

import com.solverminds.booking.dto.BookingRequestDTO;
import com.svm.ecom.dto.ErrorResponse;

import java.util.Optional;

/**
 * Outcome of {@link BookingViewService#findBookingForView}: success (200), unprocessable (422), or not found (404).
 */
public final class BookingViewLookupResult {

    private final Optional<BookingRequestDTO> booking;
    private final Optional<ErrorResponse> error;
    private final String notFoundField;
    private final String notFoundValue;

    private BookingViewLookupResult(
            Optional<BookingRequestDTO> booking,
            Optional<ErrorResponse> error,
            String notFoundField,
            String notFoundValue) {
        this.booking = booking;
        this.error = error;
        this.notFoundField = notFoundField;
        this.notFoundValue = notFoundValue;
    }

    public static BookingViewLookupResult success(BookingRequestDTO dto) {
        return new BookingViewLookupResult(Optional.ofNullable(dto), Optional.empty(), null, null);
    }

    public static BookingViewLookupResult unprocessable(ErrorResponse error) {
        return new BookingViewLookupResult(Optional.empty(), Optional.of(error), null, null);
    }

    public static BookingViewLookupResult notFound() {
        return notFound(null, null);
    }

    public static BookingViewLookupResult notFound(String field, String value) {
        return new BookingViewLookupResult(Optional.empty(), Optional.empty(), field, value);
    }

    public Optional<BookingRequestDTO> getBooking() {
        return booking;
    }

    public Optional<ErrorResponse> getError() {
        return error;
    }

    public boolean hasNotFoundContext() {
        return notFoundField != null && !notFoundField.trim().isEmpty();
    }

    public String getNotFoundField() {
        return notFoundField;
    }

    public String getNotFoundValue() {
        return notFoundValue;
    }
}
