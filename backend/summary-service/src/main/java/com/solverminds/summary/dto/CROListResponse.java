package com.solverminds.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.api.SummaryListResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CROListResponse", description = "List wrapper for CustomerReleaseOrderDto")
public class CROListResponse extends SummaryListResponse<CustomerReleaseOrderDto> {

    public CROListResponse() { super(); }

    public CROListResponse(List<CustomerReleaseOrderDto> items) { super(items); }

    // Expose a domain-specific JSON property name instead of "items"
    @ArraySchema(arraySchema = @Schema(description = "List of CRO records"),
                 schema = @Schema(implementation = CustomerReleaseOrderDto.class))
    @JsonProperty("cros")
    public List<CustomerReleaseOrderDto> getCros() {
        return super.getItems();
    }

    @JsonProperty("cros")
    public void setCros(List<CustomerReleaseOrderDto> value) {
        super.setItems(value);
    }
}
