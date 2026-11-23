package com.solverminds.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.common.enums.EcomPrintStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;

import java.time.LocalDate;
import java.time.OffsetDateTime;
/**
 * DTO for Delivery Order data
 */
@Schema(description = ModelDocs.DeliveryOrderDto.DESC)
public class DeliveryOrderDto {
    
    @Schema(description = ModelDocs.DeliveryOrderDto.PRINT_STATUS, example = ModelDocs.DeliveryOrderDto.EX_PRINT_STATUS)
    @JsonProperty("printStatus")
    private String printStatus;
    
    @Schema(description = ModelDocs.DeliveryOrderDto.ECOM_PRINT_STATUS, example = ModelDocs.DeliveryOrderDto.EX_ECOM_PRINT_STATUS)
    @JsonProperty("ecomPrintStatus")
    private EcomPrintStatus ecomPrintStatus;
    
    @Schema(description = ModelDocs.DeliveryOrderDto.DEL_ORD_NO, example = ModelDocs.DeliveryOrderDto.EX_DEL_ORD_NO)
    @JsonProperty("delOrdNo")
    private String delOrdNo;
    
    @Schema(type = "string", format = "date",description = ModelDocs.DeliveryOrderDto.DO_VALIDITY_DATE, example = ModelDocs.DeliveryOrderDto.EX_DO_VALIDITY_DATE)
    @JsonProperty("doValidityDate")    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")   
    private LocalDate doValidityDate;
    
    @Schema(type = "string", format = "date",description = ModelDocs.DeliveryOrderDto.DEL_ORD_DATE, example = ModelDocs.DeliveryOrderDto.EX_DEL_ORD_DATE)
    @JsonProperty("delOrdDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")   
    private LocalDate delOrdDate;
    
    @Schema(description = ModelDocs.DeliveryOrderDto.BL_NUMBER, example = ModelDocs.DeliveryOrderDto.EX_BL_NUMBER)
    @JsonProperty("blNumber")
    private String blNumber;
    
    @Schema(description = ModelDocs.DeliveryOrderDto.VESSEL, example = ModelDocs.DeliveryOrderDto.EX_VESSEL)
    @JsonProperty("vessel")
    private String vessel;
    
    @Schema(description = ModelDocs.DeliveryOrderDto.VOYAGE, example = ModelDocs.DeliveryOrderDto.EX_VOYAGE)
    @JsonProperty("voyage")
    private String voyage;
    
    @Schema(description = ModelDocs.DeliveryOrderDto.BOUND, example = ModelDocs.DeliveryOrderDto.EX_BOUND)
    @JsonProperty("bound")
    private Bound bound;
    
    @Schema(description = ModelDocs.DeliveryOrderDto.LOAD_PORT, example = ModelDocs.DeliveryOrderDto.EX_LOAD_PORT)
    @JsonProperty("loadPort")
    private String loadPort;
    
    @Schema(description = ModelDocs.DeliveryOrderDto.DISCHARGE_PORT, example = ModelDocs.DeliveryOrderDto.EX_DISCHARGE_PORT)
    @JsonProperty("dischargePort")
    private String dischargePort;
    
    @Schema(description = ModelDocs.DeliveryOrderDto.TERMINAL, example = ModelDocs.DeliveryOrderDto.EX_TERMINAL)
    @JsonProperty("terminal")
    private String terminal;
    
    @Schema(description = "Arrival date at discharge port in port local time (ISO 8601 with offset)", example = "2025-09-26T02:00:00+04:00")
    @JsonProperty("arrDate")
    private OffsetDateTime arrDate;

    // Default constructor
    public DeliveryOrderDto() {}

    // All-args constructor
    public DeliveryOrderDto(String printStatus, EcomPrintStatus ecomPrintStatus, String delOrdNo,
                            LocalDate doValidityDate, LocalDate delOrdDate, String blNumber,
                            String vessel, String voyage, Bound bound, String loadPort,
                            String dischargePort, String terminal, OffsetDateTime arrDate) {
        this.printStatus = printStatus;
        this.ecomPrintStatus = ecomPrintStatus;
        this.delOrdNo = delOrdNo;
        this.doValidityDate = doValidityDate;
        this.delOrdDate = delOrdDate;
        this.blNumber = blNumber;
        this.vessel = vessel;
        this.voyage = voyage;
        this.bound = bound;
        this.loadPort = loadPort;
        this.dischargePort = dischargePort;
        this.terminal = terminal;
        this.arrDate = arrDate;
    }

    // Getters and Setters
    public String getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(String printStatus) {
        this.printStatus = printStatus;
    }

    public EcomPrintStatus getEcomPrintStatus() {
        return ecomPrintStatus;
    }

    public void setEcomPrintStatus(EcomPrintStatus ecomPrintStatus) {
        this.ecomPrintStatus = ecomPrintStatus;
    }

    public String getDelOrdNo() {
        return delOrdNo;
    }

    public void setDelOrdNo(String delOrdNo) {
        this.delOrdNo = delOrdNo;
    }

    public LocalDate getDoValidityDate() {
        return doValidityDate;
    }

    public void setDoValidityDate(LocalDate doValidityDate) {
        this.doValidityDate = doValidityDate;
    }

    public LocalDate getDelOrdDate() {
        return delOrdDate;
    }

    public void setDelOrdDate(LocalDate delOrdDate) {
        this.delOrdDate = delOrdDate;
    }

    public String getBlNumber() {
        return blNumber;
    }

    public void setBlNumber(String blNumber) {
        this.blNumber = blNumber;
    }

    public String getVessel() {
        return vessel;
    }

    public void setVessel(String vessel) {
        this.vessel = vessel;
    }

    public String getVoyage() {
        return voyage;
    }

    public void setVoyage(String voyage) {
        this.voyage = voyage;
    }

    public Bound getBound() {
        return bound;
    }

    public void setBound(Bound bound) {
        this.bound = bound;
    }

    public String getLoadPort() {
        return loadPort;
    }

    public void setLoadPort(String loadPort) {
        this.loadPort = loadPort;
    }

    public String getDischargePort() {
        return dischargePort;
    }

    public void setDischargePort(String dischargePort) {
        this.dischargePort = dischargePort;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public OffsetDateTime getArrDate() {
        return arrDate;
    }

    public void setArrDate(OffsetDateTime arrDate) {
        this.arrDate = arrDate;
    }

    @Override
    public String toString() {
        return "DeliveryOrderDto{" +
                "printStatus='" + printStatus + '\'' +
                ", ecomPrintStatus='" + ecomPrintStatus + '\'' +
                ", delOrdNo='" + delOrdNo + '\'' +
                ", doValidityDate=" + doValidityDate +
                ", delOrdDate=" + delOrdDate +
                ", blNumber='" + blNumber + '\'' +
                ", vessel='" + vessel + '\'' +
                ", voyage='" + voyage + '\'' +
                ", bound='" + bound + '\'' +
                ", loadPort='" + loadPort + '\'' +
                ", dischargePort='" + dischargePort + '\'' +
                ", terminal='" + terminal + '\'' +
                ", arrDate=" + arrDate +
                '}';
    }
}
