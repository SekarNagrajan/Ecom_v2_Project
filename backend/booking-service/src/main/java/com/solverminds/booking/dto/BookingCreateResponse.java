package com.solverminds.booking.dto;

import com.svm.ecom.common.enums.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Minimal response for booking creation operations.
 */
@Schema(description = "Booking creation response")
public class BookingCreateResponse {
    
    @Schema(description = "Online booking number", example = "BKON1234")
    private String onlineBookNo;
    @Schema(description = "Booking status code (BookingStatus): D – ECOM Draft, S – Submitted, P – In-Progress, C – Confirmed, L – Cancelled, V – Rejected, T – Template, I – Void. Returns enum code only.", example = "D", implementation = BookingStatus.class)
    private BookingStatus status;
    @Schema(description = "Response message", example = "Booking created successfully")
    private String message;

    public BookingCreateResponse() {
    }

    public BookingCreateResponse(String onlineBookNo, BookingStatus status, String message) {
        this.onlineBookNo = onlineBookNo;
        this.status = status;
        this.message = message;
    }

    public String getOnlineBookNo() {
        return onlineBookNo;
    }

    public void setOnlineBookNo(String onlineBookNo) {
        this.onlineBookNo = onlineBookNo;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
