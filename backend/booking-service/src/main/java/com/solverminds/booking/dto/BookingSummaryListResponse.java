package com.solverminds.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.api.SummaryListResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "BookingSummaryListResponse", description = "List wrapper for BookingSummaryResponseDto")
public class BookingSummaryListResponse extends SummaryListResponse<BookingSummaryResponseDto> {

    public BookingSummaryListResponse() { super(); }

    public BookingSummaryListResponse(List<BookingSummaryResponseDto> items) { super(items); }

    // Expose a domain-specific JSON property name instead of "items"
    @ArraySchema(arraySchema = @Schema(description = "List of booking summary items"),
                 schema = @Schema(implementation = BookingSummaryResponseDto.class))
    @JsonProperty("bookings")
    public List<BookingSummaryResponseDto> getBookings() {
        return super.getItems();
    }

    @JsonProperty("bookings")
    public void setBookings(List<BookingSummaryResponseDto> value) {
        super.setItems(value);
    }
}
