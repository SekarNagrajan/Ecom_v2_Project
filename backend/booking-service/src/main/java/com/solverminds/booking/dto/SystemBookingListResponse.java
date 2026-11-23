package com.solverminds.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(name = "SystemBookingListResponse",
        description = "Keyset-paginated system-wide booking summary list")
public class SystemBookingListResponse {

    private List<SystemBookingSummaryResponseDto> bookings = new ArrayList<SystemBookingSummaryResponseDto>();
    private String nextCursor;
    private boolean hasNext;

    public SystemBookingListResponse() {
    }

    public SystemBookingListResponse(List<SystemBookingSummaryResponseDto> bookings,
                                     String nextCursor,
                                     boolean hasNext) {
        this.bookings = bookings != null ? bookings : new ArrayList<SystemBookingSummaryResponseDto>();
        this.nextCursor = nextCursor;
        this.hasNext = hasNext;
    }

    @ArraySchema(arraySchema = @Schema(description = "Page of booking summaries"),
            schema = @Schema(implementation = SystemBookingSummaryResponseDto.class))
    @JsonProperty("bookings")
    public List<SystemBookingSummaryResponseDto> getBookings() {
        return bookings;
    }

    public void setBookings(List<SystemBookingSummaryResponseDto> bookings) {
        this.bookings = bookings;
    }

    @JsonInclude(JsonInclude.Include.ALWAYS)
    @Schema(description = "Opaque tamper-evident cursor for the next page; null when hasNext is false",
            nullable = true)
    @JsonProperty("nextCursor")
    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }

    @Schema(description = "True when another page is available")
    @JsonProperty("hasNext")
    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
}
