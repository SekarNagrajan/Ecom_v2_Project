package com.solverminds.booking.dto.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;

@Schema(name = "BookingRequestCancellationPayload")
public class BookingCancelRequestDto {

    @NotBlank(message = "Cancellation reason is required")
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
