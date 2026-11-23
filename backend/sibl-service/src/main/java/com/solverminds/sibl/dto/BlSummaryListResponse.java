package com.solverminds.sibl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.api.SummaryListResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "BlSummaryListResponse", description = "List wrapper for BlSummaryDto")
public class BlSummaryListResponse extends SummaryListResponse<BlSummaryDto> {

    public BlSummaryListResponse() { super(); }

    public BlSummaryListResponse(List<BlSummaryDto> items) { super(items); }

    // Expose a domain-specific JSON property name instead of "items"
    @ArraySchema(arraySchema = @Schema(description = "List of BL summary items"),
                 schema = @Schema(implementation = BlSummaryDto.class))
    @JsonProperty("lstBlSummary")
    public List<BlSummaryDto> getLstBlSummary() {
        return super.getItems();
    }

    @JsonProperty("lstBlSummary")
    public void setLstBlSummary(List<BlSummaryDto> value) {
        super.setItems(value);
    }
}
