package com.solverminds.booking.dto.v2;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Success payload for {@code POST /api/v2/bookings/{bookingId}/cancel}.
 */
@Schema(description = "Booking cancellation succeeded")
public class BookingCancelResponseV2 {

    @Schema(description = "E-com booking number (ebookno / online book no)", example = "BKON1234", requiredMode = Schema.RequiredMode.REQUIRED)
    private String onlineBookNo;

    @Schema(description = "Outcome message", example = "Submitted booking is canceled.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    public BookingCancelResponseV2() {
    }

    public BookingCancelResponseV2(String onlineBookNo, String message) {
        this.onlineBookNo = onlineBookNo;
        this.message = message;
    }

    public String getOnlineBookNo() {
        return onlineBookNo;
    }

    public void setOnlineBookNo(String onlineBookNo) {
        this.onlineBookNo = onlineBookNo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
