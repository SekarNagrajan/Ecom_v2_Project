package com.svm.ecom.rates.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(
    name = "CustomerContractPortPair",
    description = "Details about a port pair in a customer contract",
    example = "{\"origin\":\"CNNGB\",\"pol\":\"CNNGB\",\"pod\":\"ESBCN\",\"delivery\":\"ESBCN\","
            + "\"equipmentType\":\"40HC\",\"transportService\":\"CY / CY\",\"serviceCode\":\"PAX\","
            + "\"isDG\":false,\"dgDetails\":\"0000~99\",\"isOOG\":false,\"oogDetails\":\"MM~~0.00*0.00*695.00*695.00*1724.00~12\","
            + "\"isSOC\":false,\"isNOR\":false,\"ofr\":\"A\","
            + "\"oceanFreight\":6109.53,\"baseRate\":6109.53,\"totalFreight\":7671.27,"
            + "\"quantity\":1.0,\"currency\":\"EUR\","
            + "\"remarks\":\"Incl. BAF/SDD, subj. locals both ends + EPF\","
            + "\"commodityClass\":\"1\",\"validFrom\":\"2026-04-26\",\"validTo\":\"2027-05-31\","
            + "\"status\":\"A\",\"exportContainerRent\":14,\"importContainerRent\":15,"
            + "\"exportTerminalStorage\":7,\"importTerminalStorage\":4}"
)
public class CustomerContractPortPair {

    @Schema(description = "Origin port code", example = "CNNGB")
    private String origin;

    @Schema(description = "Port of Loading code", example = "CNNGB", requiredMode = Schema.RequiredMode.REQUIRED)
    private String pol;

    @Schema(description = "Port of Discharge code", example = "ESBCN", requiredMode = Schema.RequiredMode.REQUIRED)
    private String pod;

    @Schema(description = "Delivery port code", example = "ESBCN")
    private String delivery;

    @Schema(description = "Equipment type identifier code, e.g. 40HC", example = "40HC", requiredMode = Schema.RequiredMode.REQUIRED)
    private String equipmentType;

    @Schema(description = "Transport service type", example = "CY / CY")
    private TransportService transportService;

    @Schema(description = "Service code for the port pair", example = "PAX")
    private String serviceCode;

    @Schema(description = "Dangerous goods flag", example = "false")
    private Boolean isDG;

    @Schema(description = "DG details", example = "0000~99")
    private String dgDetails;

    @Schema(description = "Out-of-gauge flag", example = "false")
    private Boolean isOOG;

    @Schema(description = "OOG details", example = "MM~~0.00*0.00*695.00*695.00*1724.00~12")
    private String oogDetails;

    @Schema(description = "Shipper-owned container flag", example = "false")
    private Boolean isSOC;

    @Schema(description = "Non-operating reefer flag", example = "false")
    private Boolean isNOR;

    @Schema(description = "Ocean Freight Rate type: O – Ocean Freight Rate, A – All In", example = "A")
    private OfrType ofr;

    @Schema(description = "Ocean freight amount", example = "6109.53")
    private BigDecimal oceanFreight;

    @Schema(description = "Base rate for the port pair", example = "6109.53")
    private BigDecimal baseRate;

    @Schema(description = "Total freight amount", example = "7671.27")
    private BigDecimal totalFreight;

    @Schema(description = "CBM / quantity for the port pair", example = "1.0")
    private BigDecimal quantity;

    @Schema(description = "Currency code for the port pair (e.g. ISO 4217)", example = "EUR")
    private String currency;

    @Schema(description = "Remarks for the port pair", example = "Incl. BAF/SDD, subj. locals both ends + EPF")
    private String remarks;

    @Schema(description = "Commodity class", example = "1")
    private String commodityClass;

    @Schema(description = "Port pair valid from date", example = "2026-04-26")
    private LocalDate validFrom;

    @Schema(description = "Port pair valid to date", example = "2027-05-31")
    private LocalDate validTo;

    @Schema(description = "Port pair status: A - Active, E - Expired", example = "A")
    private ContractStatus status;

    @Schema(description = "Export container rent surcharge in days", example = "14")
    private Integer exportContainerRent;

    @Schema(description = "Import container rent surcharge in days", example = "15")
    private Integer importContainerRent;

    @Schema(description = "Export terminal storage surcharge in days", example = "7")
    private Integer exportTerminalStorage;

    @Schema(description = "Import terminal storage surcharge in days", example = "4")
    private Integer importTerminalStorage;

    public CustomerContractPortPair() {}

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getPol() { return pol; }
    public void setPol(String pol) { this.pol = pol; }

    public String getPod() { return pod; }
    public void setPod(String pod) { this.pod = pod; }

    public String getDelivery() { return delivery; }
    public void setDelivery(String delivery) { this.delivery = delivery; }

    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }

    public TransportService getTransportService() { return transportService; }
    public void setTransportService(TransportService transportService) { this.transportService = transportService; }

    public String getServiceCode() { return serviceCode; }
    public void setServiceCode(String serviceCode) { this.serviceCode = serviceCode; }

    public Boolean getIsDG() { return isDG; }
    public void setIsDG(Boolean isDG) { this.isDG = isDG; }

    public String getDgDetails() { return dgDetails; }
    public void setDgDetails(String dgDetails) { this.dgDetails = dgDetails; }

    public Boolean getIsOOG() { return isOOG; }
    public void setIsOOG(Boolean isOOG) { this.isOOG = isOOG; }

    public String getOogDetails() { return oogDetails; }
    public void setOogDetails(String oogDetails) { this.oogDetails = oogDetails; }

    public Boolean getIsSOC() { return isSOC; }
    public void setIsSOC(Boolean isSOC) { this.isSOC = isSOC; }

    public Boolean getIsNOR() { return isNOR; }
    public void setIsNOR(Boolean isNOR) { this.isNOR = isNOR; }

    public OfrType getOfr() { return ofr; }
    public void setOfr(OfrType ofr) { this.ofr = ofr; }

    public BigDecimal getOceanFreight() { return oceanFreight; }
    public void setOceanFreight(BigDecimal oceanFreight) { this.oceanFreight = oceanFreight; }

    public BigDecimal getBaseRate() { return baseRate; }
    public void setBaseRate(BigDecimal baseRate) { this.baseRate = baseRate; }

    public BigDecimal getTotalFreight() { return totalFreight; }
    public void setTotalFreight(BigDecimal totalFreight) { this.totalFreight = totalFreight; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getCommodityClass() { return commodityClass; }
    public void setCommodityClass(String commodityClass) { this.commodityClass = commodityClass; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }

    public ContractStatus getStatus() { return status; }
    public void setStatus(ContractStatus status) { this.status = status; }

    public Integer getExportContainerRent() { return exportContainerRent; }
    public void setExportContainerRent(Integer exportContainerRent) { this.exportContainerRent = exportContainerRent; }

    public Integer getImportContainerRent() { return importContainerRent; }
    public void setImportContainerRent(Integer importContainerRent) { this.importContainerRent = importContainerRent; }

    public Integer getExportTerminalStorage() { return exportTerminalStorage; }
    public void setExportTerminalStorage(Integer exportTerminalStorage) { this.exportTerminalStorage = exportTerminalStorage; }

    public Integer getImportTerminalStorage() { return importTerminalStorage; }
    public void setImportTerminalStorage(Integer importTerminalStorage) { this.importTerminalStorage = importTerminalStorage; }
}
