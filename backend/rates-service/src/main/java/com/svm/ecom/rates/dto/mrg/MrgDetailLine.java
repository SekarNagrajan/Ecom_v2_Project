package com.svm.ecom.rates.dto.mrg;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * One MRG rate line (row of {@code MRGDetail}). Field names / order follow IAD TSHG-2935 section 3.4.
 */
@JsonPropertyOrder({
        "fromTca", "fromCountry", "origin", "pol", "toTca", "toCountry", "pod", "delivery",
        "eqpType", "transportService", "currency",
        "intRateLoc", "intRateUsd", "extRateLoc", "extRateUsd",
        "discreType", "lumpsumLoc", "lumpsumUsd", "formula", "remarks", "commodity",
        "soc", "inclusive", "subjectTo", "dg",
        "expDetention", "impDetention", "expDemurrage", "impDemurrage"
})
@Schema(name = "MrgDetailLine", description = "MRG rate detail line")
public class MrgDetailLine {

    @Schema(description = "From TCA", example = "FEST", nullable = true)
    private String fromTca;
    @Schema(description = "From Country", example = "CN", nullable = true)
    private String fromCountry;
    @Schema(description = "Origin", example = "CNDCB", nullable = true)
    private String origin;
    @Schema(description = "Port of Loading", example = "CNDCB", nullable = true)
    private String pol;
    @Schema(description = "To TCA", example = "WMED", nullable = true)
    private String toTca;
    @Schema(description = "To Country", example = "SI", nullable = true)
    private String toCountry;
    @Schema(description = "Port of Discharge", example = "SIKOP", nullable = true)
    private String pod;
    @Schema(description = "Delivery", example = "SIKOP", nullable = true)
    private String delivery;
    @Schema(description = "Equipment Type", example = "20DC", nullable = true)
    private String eqpType;
    @Schema(description = "Transport service", example = "CY/CY", nullable = true)
    private String transportService;
    @Schema(description = "Currency", example = "USD", nullable = true)
    private String currency;
    // example quoted so swagger-core keeps a String ("500.00") instead of coercing to number 500.0
    @Schema(description = "Internal Rate, local currency (2 decimals)", example = "\"500.00\"", nullable = true)
    private String intRateLoc;
    @Schema(description = "Internal Rate in USD (2 decimals)", example = "\"500.00\"", nullable = true)
    private String intRateUsd;
    @Schema(description = "External Rate, local currency (2 decimals)", example = "\"1000.00\"", nullable = true)
    private String extRateLoc;
    @Schema(description = "External Rate in USD (2 decimals)", example = "\"1000.00\"", nullable = true)
    private String extRateUsd;
    @Schema(description = "Discretionary Type. N - N/A; L - Lumpsum; F - Formula",
            allowableValues = {"N", "L", "F"}, example = "N")
    private String discreType;
    @Schema(description = "LumpSum, local currency (2 decimals)", example = "\"100.00\"", nullable = true)
    private String lumpsumLoc;
    @Schema(description = "LumpSum in USD (2 decimals)", example = "\"100.00\"", nullable = true)
    private String lumpsumUsd;
    @Schema(description = "Formula logic", example = "")
    private String formula;
    @Schema(description = "Remarks", example = "")
    private String remarks;
    @Schema(description = "Commodity", example = "")
    private String commodity;
    @Schema(description = "Shipper owned container", example = "false")
    private boolean soc;
    @ArraySchema(
            arraySchema = @Schema(description = "Charge codes included in the rate", example = "[\"BAF\"]"),
            schema = @Schema(type = "string", description = "Charge code", example = "BAF"))
    private List<String> inclusive = new ArrayList<String>();
    @ArraySchema(
            arraySchema = @Schema(description = "Charge codes the rate is subject to", example = "[\"EPS\", \"ADL\", \"ADD\"]"),
            schema = @Schema(type = "string", description = "Charge code", example = "EPS"))
    private List<String> subjectTo = new ArrayList<String>();
    @Schema(description = "Dangerous Goods", example = "false")
    private boolean dg;
    @Schema(description = "Export Detention free days", example = "7", nullable = true)
    private Integer expDetention;
    @Schema(description = "Import Detention free days", example = "14", nullable = true)
    private Integer impDetention;
    @Schema(description = "Export Demurrage free days", example = "10", nullable = true)
    private Integer expDemurrage;
    @Schema(description = "Import Demurrage free days", example = "20", nullable = true)
    private Integer impDemurrage;

    public String getFromTca() { return fromTca; }
    public void setFromTca(String fromTca) { this.fromTca = fromTca; }
    public String getFromCountry() { return fromCountry; }
    public void setFromCountry(String fromCountry) { this.fromCountry = fromCountry; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getPol() { return pol; }
    public void setPol(String pol) { this.pol = pol; }
    public String getToTca() { return toTca; }
    public void setToTca(String toTca) { this.toTca = toTca; }
    public String getToCountry() { return toCountry; }
    public void setToCountry(String toCountry) { this.toCountry = toCountry; }
    public String getPod() { return pod; }
    public void setPod(String pod) { this.pod = pod; }
    public String getDelivery() { return delivery; }
    public void setDelivery(String delivery) { this.delivery = delivery; }
    public String getEqpType() { return eqpType; }
    public void setEqpType(String eqpType) { this.eqpType = eqpType; }
    public String getTransportService() { return transportService; }
    public void setTransportService(String transportService) { this.transportService = transportService; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getIntRateLoc() { return intRateLoc; }
    public void setIntRateLoc(String intRateLoc) { this.intRateLoc = intRateLoc; }
    public String getIntRateUsd() { return intRateUsd; }
    public void setIntRateUsd(String intRateUsd) { this.intRateUsd = intRateUsd; }
    public String getExtRateLoc() { return extRateLoc; }
    public void setExtRateLoc(String extRateLoc) { this.extRateLoc = extRateLoc; }
    public String getExtRateUsd() { return extRateUsd; }
    public void setExtRateUsd(String extRateUsd) { this.extRateUsd = extRateUsd; }
    public String getDiscreType() { return discreType; }
    public void setDiscreType(String discreType) { this.discreType = discreType; }
    public String getLumpsumLoc() { return lumpsumLoc; }
    public void setLumpsumLoc(String lumpsumLoc) { this.lumpsumLoc = lumpsumLoc; }
    public String getLumpsumUsd() { return lumpsumUsd; }
    public void setLumpsumUsd(String lumpsumUsd) { this.lumpsumUsd = lumpsumUsd; }
    public String getFormula() { return formula; }
    public void setFormula(String formula) { this.formula = formula; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getCommodity() { return commodity; }
    public void setCommodity(String commodity) { this.commodity = commodity; }
    public boolean isSoc() { return soc; }
    public void setSoc(boolean soc) { this.soc = soc; }
    public List<String> getInclusive() { return inclusive; }
    public void setInclusive(List<String> inclusive) { this.inclusive = inclusive; }
    public List<String> getSubjectTo() { return subjectTo; }
    public void setSubjectTo(List<String> subjectTo) { this.subjectTo = subjectTo; }
    public boolean isDg() { return dg; }
    public void setDg(boolean dg) { this.dg = dg; }
    public Integer getExpDetention() { return expDetention; }
    public void setExpDetention(Integer expDetention) { this.expDetention = expDetention; }
    public Integer getImpDetention() { return impDetention; }
    public void setImpDetention(Integer impDetention) { this.impDetention = impDetention; }
    public Integer getExpDemurrage() { return expDemurrage; }
    public void setExpDemurrage(Integer expDemurrage) { this.expDemurrage = expDemurrage; }
    public Integer getImpDemurrage() { return impDemurrage; }
    public void setImpDemurrage(Integer impDemurrage) { this.impDemurrage = impDemurrage; }
}
