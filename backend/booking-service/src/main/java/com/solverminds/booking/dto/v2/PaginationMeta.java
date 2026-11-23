package com.solverminds.booking.dto.v2;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Standard pagination metadata for V2 API.
 * Uses offset-based pagination (not cursor-based).
 */
@Schema(description = "Standard pagination metadata")
public class PaginationMeta {

    @Schema(description = "Total number of records matching filters", example = "145", minimum = "0")
    private int total;

    @Schema(description = "Number of records skipped (offset)", example = "20", minimum = "0")
    private int offset;

    @Schema(description = "Number of records returned (limit)", example = "20", minimum = "1")
    private int limit;

    public PaginationMeta() {
    }

    public PaginationMeta(int total, int offset, int limit) {
        this.total = total;
        this.offset = offset;
        this.limit = limit;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
