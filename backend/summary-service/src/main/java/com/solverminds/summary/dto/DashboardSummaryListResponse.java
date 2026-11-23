package com.solverminds.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.api.SummaryListResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "DashboardSummaryListResponse", description = "List wrapper for DashboardSummaryDto")
public class DashboardSummaryListResponse extends SummaryListResponse<DashboardSummaryDto> {

    public DashboardSummaryListResponse() { super(); }

    public DashboardSummaryListResponse(List<DashboardSummaryDto> items) { super(items); }

    // Expose a domain-specific JSON property name instead of "items"
    @ArraySchema(arraySchema = @Schema(description = "List of dashboard summary items"),
                 schema = @Schema(implementation = DashboardSummaryDto.class))
    @JsonProperty("dashboardSummaries")
    public List<DashboardSummaryDto> getDashboardSummaries() {
        return super.getItems();
    }

    @JsonProperty("dashboardSummaries")
    public void setDashboardSummaries(List<DashboardSummaryDto> value) {
        super.setItems(value);
    }
}
