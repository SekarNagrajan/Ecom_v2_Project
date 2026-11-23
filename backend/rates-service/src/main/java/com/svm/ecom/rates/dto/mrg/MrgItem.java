package com.svm.ecom.rates.dto.mrg;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"id", "description", "validFrom", "validTo", "currency", "details"})
@Schema(name = "MrgItem", description = "MRG (Minimum Rate Guideline) header with all its rate detail lines")
public class MrgItem {

    @Schema(description = "MRG number (MRGMaster.mrgCode)", example = "M26080900002")
    private String id;
    @Schema(description = "Reference description (MRGMaster.reference)", example = "TEST-001")
    private String description;
    @Schema(description = "Validity start - earliest detail fromDate (UTC)",
            type = "string", format = "date-time", example = "2026-07-17T00:00:00Z", nullable = true)
    private String validFrom;
    @Schema(description = "Validity end - latest detail toDate (UTC)",
            type = "string", format = "date-time", example = "2026-07-31T00:00:00Z", nullable = true)
    private String validTo;
    @Schema(description = "Currency (currency of the MRG detail lines)", example = "USD", nullable = true)
    private String currency;
    @ArraySchema(arraySchema = @Schema(description = "Rate detail lines"),
            schema = @Schema(implementation = MrgDetailLine.class))
    private List<MrgDetailLine> details = new ArrayList<MrgDetailLine>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getValidFrom() { return validFrom; }
    public void setValidFrom(String validFrom) { this.validFrom = validFrom; }
    public String getValidTo() { return validTo; }
    public void setValidTo(String validTo) { this.validTo = validTo; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public List<MrgDetailLine> getDetails() { return details; }
    public void setDetails(List<MrgDetailLine> details) { this.details = details; }
}
