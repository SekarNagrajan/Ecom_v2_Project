package com.solverminds.booking.dto.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * A paginated list of bookings for V2 API.
 * OpenAPI schema name "PagedBookingList" for client contract alignment.
 */
@Schema(name = "PagedBookingList", description = "A paginated list of bookings")
public class PagedBookingListV2 {

    @Schema(description = "Booking list items")
    private List<PagedBookingListItemV2> items;

    @Schema(description = "Pagination metadata")
    private PaginationMeta meta;

    public PagedBookingListV2() {
    }

    public PagedBookingListV2(List<PagedBookingListItemV2> items, PaginationMeta meta) {
        this.items = items;
        this.meta = meta;
    }

    public List<PagedBookingListItemV2> getItems() {
        return items;
    }

    public void setItems(List<PagedBookingListItemV2> items) {
        this.items = items;
    }

    public PaginationMeta getMeta() {
        return meta;
    }

    public void setMeta(PaginationMeta meta) {
        this.meta = meta;
    }
}

