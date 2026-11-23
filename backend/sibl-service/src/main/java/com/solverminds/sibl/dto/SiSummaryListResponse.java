package com.solverminds.sibl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.api.SummaryListResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "SiSummaryListResponse", description = "List wrapper for SiSummaryDto")
public class SiSummaryListResponse extends SummaryListResponse<SiSummaryDto> {

    public SiSummaryListResponse() { super(); }

    public SiSummaryListResponse(List<SiSummaryDto> items) { super(items); }

    // Expose a domain-specific JSON property name instead of "items"
    @ArraySchema(arraySchema = @Schema(description = "List of SI summary items"),
                 schema = @Schema(implementation = SiSummaryDto.class))
    @JsonProperty("lstSiSummary")
    public List<SiSummaryDto> getLstSiSummary() {
        return super.getItems();
    }

    @JsonProperty("lstSiSummary")
    public void setLstSiSummary(List<SiSummaryDto> value) {
        super.setItems(value);
    }
}
