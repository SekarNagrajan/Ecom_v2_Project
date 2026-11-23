package com.solverminds.booking.dto;

import java.util.List;

/**
 * Result of paged booking summary from usp_getecombookingsummary_paged.
 * Used only by V2 GET /api/v2/bookings.
 */
public class PagedBookingSummaryResult {

    private final int totalCount;
    private final List<BookingSummaryResponseDto> items;

    public PagedBookingSummaryResult(int totalCount, List<BookingSummaryResponseDto> items) {
        this.totalCount = totalCount;
        this.items = items != null ? items : java.util.Collections.emptyList();
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<BookingSummaryResponseDto> getItems() {
        return items;
    }
}
