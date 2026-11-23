package com.svm.ecom.rates.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

/**
 * Details about a customer contract (latest amendment)
 */
@Schema(name = "CustomerContract", description = "Details about a customer contract (latest amendment)")
public class CustomerContract {
    
    @Schema(description = "Contract identifier", example = "20250173", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;
    
    @Schema(description = "Contract name (only applicable for CMS)", example = "Hellmann Nov/ Dec 25")
    private String name;
    
    @Schema(description = "Amendment number", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer amendmentNumber;
    
    @Schema(description = "Agreement party details", requiredMode = Schema.RequiredMode.REQUIRED)
    private ContractCustomer agreementParty;
    
    @Schema(description = "List of named accounts", example = "[{\"code\":\"2023000722\",\"name\":\"ENUS LOGISTICS, S.L.\"}]")
    private List<ContractCustomer> namedAccounts;
    
    @Schema(description = "Contract valid from date (also known as effective date)", example = "2026-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate validFrom;
    
    @Schema(description = "Contract valid to date (also known as expiry date)", example = "2027-12-31", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate validTo;
    
    @Schema(description = "Contract effective from date", example = "2026-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate effectiveDate;
    
    @Schema(description = "Contract status code: A - Active, E - Expired", example = "A", requiredMode = Schema.RequiredMode.REQUIRED)
    private ContractStatus status;
    
    @Schema(description = "List of port pairs in the contract", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CustomerContractPortPair> portPairs;
    
    public CustomerContract() {}
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getAmendmentNumber() {
        return amendmentNumber;
    }
    
    public void setAmendmentNumber(Integer amendmentNumber) {
        this.amendmentNumber = amendmentNumber;
    }
    
    public ContractCustomer getAgreementParty() {
        return agreementParty;
    }
    
    public void setAgreementParty(ContractCustomer agreementParty) {
        this.agreementParty = agreementParty;
    }
    
    public List<ContractCustomer> getNamedAccounts() {
        return namedAccounts;
    }
    
    public void setNamedAccounts(List<ContractCustomer> namedAccounts) {
        this.namedAccounts = namedAccounts;
    }
    
    public LocalDate getValidFrom() {
        return validFrom;
    }
    
    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }
    
    public LocalDate getValidTo() {
        return validTo;
    }
    
    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }
    
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }
    
    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    public ContractStatus getStatus() {
        return status;
    }
    
    public void setStatus(ContractStatus status) {
        this.status = status;
    }
    
    public List<CustomerContractPortPair> getPortPairs() {
        return portPairs;
    }
    
    public void setPortPairs(List<CustomerContractPortPair> portPairs) {
        this.portPairs = portPairs;
    }
}