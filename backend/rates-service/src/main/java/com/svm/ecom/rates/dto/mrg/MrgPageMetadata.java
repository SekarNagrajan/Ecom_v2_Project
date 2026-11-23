package com.svm.ecom.rates.dto.mrg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({"nextCursor", "hasNext", "limit"})
@Schema(name = "MrgPageMetadata", description = "Cursor pagination envelope")
public class MrgPageMetadata {

    @JsonInclude(JsonInclude.Include.ALWAYS)
    @Schema(description = "Opaque cursor to fetch the next page. Null when there is no next page. "
            + "Clients must not parse or modify it.", example = "abcdef123", nullable = true)
    private String nextCursor;
    @Schema(description = "Indicates whether more records are available after this page", example = "true")
    private boolean hasNext;
    @Schema(description = "Maximum number of items per page", example = "20")
    private int limit;

    public MrgPageMetadata() {
    }

    public MrgPageMetadata(String nextCursor, boolean hasNext, int limit) {
        this.nextCursor = nextCursor;
        this.hasNext = hasNext;
        this.limit = limit;
    }

    public String getNextCursor() { return nextCursor; }
    public void setNextCursor(String nextCursor) { this.nextCursor = nextCursor; }
    public boolean isHasNext() { return hasNext; }
    public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
}
