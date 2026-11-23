package com.svm.ecom.master.dto.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * IMDG entry response for GET /api/v2/master/imdg-entries.
 * Matches OpenAPI schema "ImdgEntry". Blank optional fields are omitted.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ImdgEntry", description = "IMDG details for a UN number (and optional variant)",
        example = "{\"unNo\":\"2779\",\"variant\":\"~\",\"imdgAmendment\":\"42\",\"imoClass\":\"6.1\","
                + "\"packagingGroup\":\"II\",\"ems\":\"F-A,S-A\",\"state\":\"SOLID\","
                + "\"properShippingName\":\"SUBSTITUTED NITROPHENOL PESTICIDE, SOLID, TOXIC\","
                + "\"technicalName\":\"DINITRO-ORTHO-CRESOL\","
                + "\"stowageInstruction\":\"SW2 Clear of living quarters\",\"imoStow\":\"On Deck Only\","
                + "\"segregation\":\"ACIDS\"}")
public class ImdgEntryResponse {

    @Schema(description = "United Nations number", example = "2779", requiredMode = Schema.RequiredMode.REQUIRED)
    private String unNo;

    @Schema(description = "IMDG variant for this UN number", example = "~")
    private String variant;

    @Schema(description = "Current IMDG amendment number", example = "42")
    private String imdgAmendment;

    @Schema(description = "Primary IMO hazard class", example = "6.1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String imoClass;

    @Schema(description = "First subsidiary hazard class", example = "3")
    private String subRisk1;

    @Schema(description = "Second subsidiary hazard class", example = "8")
    private String subRisk2;

    @Schema(description = "Packing group", example = "II", allowableValues = {"I", "II", "III"})
    private String packagingGroup;

    @Schema(description = "Emergency Schedule (EMS) codes", example = "F-A,S-A")
    private String ems;

    @Schema(description = "Physical state", example = "SOLID", allowableValues = {"SOLID", "LIQUID", "GAS"})
    private String state;

    @Schema(description = "Proper shipping name", example = "SUBSTITUTED NITROPHENOL PESTICIDE, SOLID, TOXIC")
    private String properShippingName;

    @Schema(description = "Technical name", example = "DINITRO-ORTHO-CRESOL")
    private String technicalName;

    @Schema(description = "Stowage instruction text", example = "SW2 Clear of living quarters")
    private String stowageInstruction;

    @Schema(description = "IMO stowage category text", example = "On Deck Only")
    private String imoStow;

    @Schema(description = "Segregation group text when available from master", example = "ACIDS")
    private String segregation;

    public String getUnNo() {
        return unNo;
    }

    public void setUnNo(String unNo) {
        this.unNo = unNo;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getImdgAmendment() {
        return imdgAmendment;
    }

    public void setImdgAmendment(String imdgAmendment) {
        this.imdgAmendment = imdgAmendment;
    }

    public String getImoClass() {
        return imoClass;
    }

    public void setImoClass(String imoClass) {
        this.imoClass = imoClass;
    }

    public String getSubRisk1() {
        return subRisk1;
    }

    public void setSubRisk1(String subRisk1) {
        this.subRisk1 = subRisk1;
    }

    public String getSubRisk2() {
        return subRisk2;
    }

    public void setSubRisk2(String subRisk2) {
        this.subRisk2 = subRisk2;
    }

    public String getPackagingGroup() {
        return packagingGroup;
    }

    public void setPackagingGroup(String packagingGroup) {
        this.packagingGroup = packagingGroup;
    }

    public String getEms() {
        return ems;
    }

    public void setEms(String ems) {
        this.ems = ems;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProperShippingName() {
        return properShippingName;
    }

    public void setProperShippingName(String properShippingName) {
        this.properShippingName = properShippingName;
    }

    public String getTechnicalName() {
        return technicalName;
    }

    public void setTechnicalName(String technicalName) {
        this.technicalName = technicalName;
    }

    public String getStowageInstruction() {
        return stowageInstruction;
    }

    public void setStowageInstruction(String stowageInstruction) {
        this.stowageInstruction = stowageInstruction;
    }

    public String getImoStow() {
        return imoStow;
    }

    public void setImoStow(String imoStow) {
        this.imoStow = imoStow;
    }

    public String getSegregation() {
        return segregation;
    }

    public void setSegregation(String segregation) {
        this.segregation = segregation;
    }
}
