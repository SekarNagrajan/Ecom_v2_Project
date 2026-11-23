package com.solverminds.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.api.SummaryListResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "ArrivalNoticeListResponse", description = "List wrapper for ArrivalNoticeDto")
public class ArrivalNoticeListResponse extends SummaryListResponse<ArrivalNoticeDto> {

    public ArrivalNoticeListResponse() { super(); }

    public ArrivalNoticeListResponse(List<ArrivalNoticeDto> items) { super(items); }

    // Expose a domain-specific JSON property name instead of "items"
    @ArraySchema(arraySchema = @Schema(description = "List of arrival notices"),
                 schema = @Schema(implementation = ArrivalNoticeDto.class))
    @JsonProperty("arrivalNotices")
    public List<ArrivalNoticeDto> getArrivalNotices() {
        return super.getItems();
    }

    @JsonProperty("arrivalNotices")
    public void setArrivalNotices(List<ArrivalNoticeDto> value) {
        super.setItems(value);
    }
}
