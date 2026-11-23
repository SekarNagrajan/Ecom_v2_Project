package com.solverminds.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.enums.Bound;
import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;

/**
 * DTO for Customer Release Order (CRO) data from usp_ecomcrodetails stored procedure
 */
@Schema(description = ModelDocs.CustomerReleaseOrderDto.DESC)
public class CustomerReleaseOrderDto {
    
    @Schema(description = ModelDocs.CustomerReleaseOrderDto.BOOKING_NUMBER, example = ModelDocs.CustomerReleaseOrderDto.EX_BOOKING_NUMBER)
    @JsonProperty("bookingNumber")
    private String flsBookingNo;
    
    @Schema(description = ModelDocs.CustomerReleaseOrderDto.SERVICE_CODE, example = ModelDocs.CustomerReleaseOrderDto.EX_SERVICE_CODE)
    @JsonProperty("serviceCode")
    private String serviceCode;
    
    @Schema(description = ModelDocs.CustomerReleaseOrderDto.VESSEL_CODE, example = ModelDocs.CustomerReleaseOrderDto.EX_VESSEL_CODE)
    @JsonProperty("vesselCode")
    private String vslCode;
    
    @Schema(description = ModelDocs.CustomerReleaseOrderDto.VOYAGE_CODE, example = ModelDocs.CustomerReleaseOrderDto.EX_VOYAGE_CODE)
    @JsonProperty("voyageCode")
    private String voyCode;
    
    @Schema(description = ModelDocs.CustomerReleaseOrderDto.BOUND, example = ModelDocs.CustomerReleaseOrderDto.EX_BOUND)
    @JsonProperty("bound")
    private Bound bound;
    
    @Schema(description = ModelDocs.CustomerReleaseOrderDto.TERMINAL, example = ModelDocs.CustomerReleaseOrderDto.EX_TERMINAL)
    @JsonProperty("terminal")
    private String terminal;
    
    @Schema(description = ModelDocs.CustomerReleaseOrderDto.ORIGIN_PORT_ID, example = ModelDocs.CustomerReleaseOrderDto.EX_ORIGIN_PORT_ID)
    @JsonProperty("originPortId")
    private String origin;
    
    @Schema(description = ModelDocs.CustomerReleaseOrderDto.FINAL_PORT_ID, example = ModelDocs.CustomerReleaseOrderDto.EX_FINAL_PORT_ID)
    @JsonProperty("finalPortId")
    private String delivery;
    
    @Schema(description = ModelDocs.CustomerReleaseOrderDto.LOAD_PORT_ID, example = ModelDocs.CustomerReleaseOrderDto.EX_LOAD_PORT_ID)
    @JsonProperty("loadPortId")
    private String pol;
    
    @Schema(description = ModelDocs.CustomerReleaseOrderDto.DISCHARGE_PORT_ID, example = ModelDocs.CustomerReleaseOrderDto.EX_DISCHARGE_PORT_ID)
    @JsonProperty("dischargePortId")
    private String pod;
    
    @Schema(description = ModelDocs.CustomerReleaseOrderDto.EQUIPMENT_TYPE, example = ModelDocs.CustomerReleaseOrderDto.EX_EQUIPMENT_TYPE)
    @JsonProperty("equipmentType")
    private String equipmentType;
    
    @Schema(description = ModelDocs.CustomerReleaseOrderDto.BOOKING_QTY, example = ModelDocs.CustomerReleaseOrderDto.EX_BOOKING_QTY)
    @JsonProperty("bookingQty")
    private Integer bkgQty;
    
    @Schema(description = ModelDocs.CustomerReleaseOrderDto.RELEASED_QTY, example = ModelDocs.CustomerReleaseOrderDto.EX_RELEASED_QTY)
    @JsonProperty("releasedQty")
    private Integer relQty;
    
    @Schema(description = ModelDocs.CustomerReleaseOrderDto.AGENCY_CODE, example = ModelDocs.CustomerReleaseOrderDto.EX_AGENCY_CODE)
    @JsonProperty("agencyCode")
    private String agencyCode;

    // Default constructor
    public CustomerReleaseOrderDto() {}

    // All-args constructor
    public CustomerReleaseOrderDto(String flsBookingNo, String serviceCode, String vslCode, 
                                   String voyCode, Bound bound, String terminal, String origin, 
                                   String delivery, String pol, String pod, String equipmentType, 
                                   Integer bkgQty, Integer relQty, String agencyCode) {
        this.flsBookingNo = flsBookingNo;
        this.serviceCode = serviceCode;
        this.vslCode = vslCode;
        this.voyCode = voyCode;
        this.bound = bound;
        this.terminal = terminal;
        this.origin = origin;
        this.delivery = delivery;
        this.pol = pol;
        this.pod = pod;
        this.equipmentType = equipmentType;
        this.bkgQty = bkgQty;
        this.relQty = relQty;
        this.agencyCode = agencyCode;
    }

    // Getters and Setters
    public String getFlsBookingNo() {
        return flsBookingNo;
    }

    public void setFlsBookingNo(String flsBookingNo) {
        this.flsBookingNo = flsBookingNo;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getVslCode() {
        return vslCode;
    }

    public void setVslCode(String vslCode) {
        this.vslCode = vslCode;
    }

    public String getVoyCode() {
        return voyCode;
    }

    public void setVoyCode(String voyCode) {
        this.voyCode = voyCode;
    }

    public Bound getBound() {
        return bound;
    }

    public void setBound(Bound bound) {
        this.bound = bound;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getPol() {
        return pol;
    }

    public void setPol(String pol) {
        this.pol = pol;
    }

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public Integer getBkgQty() {
        return bkgQty;
    }

    public void setBkgQty(Integer bkgQty) {
        this.bkgQty = bkgQty;
    }

    public Integer getRelQty() {
        return relQty;
    }

    public void setRelQty(Integer relQty) {
        this.relQty = relQty;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    @Override
    public String toString() {
        return "CustomerReleaseOrderDto{" +
                "flsBookingNo='" + flsBookingNo + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", vslCode='" + vslCode + '\'' +
                ", voyCode='" + voyCode + '\'' +
                ", bound='" + bound + '\'' +
                ", terminal='" + terminal + '\'' +
                ", origin='" + origin + '\'' +
                ", delivery='" + delivery + '\'' +
                ", pol='" + pol + '\'' +
                ", pod='" + pod + '\'' +
                ", equipmentType='" + equipmentType + '\'' +
                ", bkgQty=" + bkgQty +
                ", relQty=" + relQty +
                ", agencyCode='" + agencyCode + '\'' +
                '}';
    }
}
