package com.solverminds.booking.dto.v2.vgm;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Success payload for {@code PUT /api/v2/bookings/{bookingId}/vgm} (implementation returns HTTP 200).
 */
@Schema(name = "VgmUpdateResponse", description = "VGM upsert succeeded")
public class VgmUpdateResponse {

    @Schema(description = "Booking number (path booking id)", example = "TSHGCGP26008949", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("BookNo")
    private String bookNo;

    @Schema(description = "Outcome message", example = "VGM updated successfully", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    public VgmUpdateResponse() {
    }

    public VgmUpdateResponse(String bookNo, String message) {
        this.bookNo = bookNo;
        this.message = message;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
