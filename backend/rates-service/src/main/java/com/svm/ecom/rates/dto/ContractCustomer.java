package com.svm.ecom.rates.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Details about a customer involved in a contract
 */
@Schema(name = "ContractCustomer", description = "Details about a customer involved in a contract")
public class ContractCustomer {
    
    @Schema(description = "Customer code", example = "2024003615", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;
    
    @Schema(description = "Customer name", example = "SND SQUARE INTERNATIONAL LTD.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    public ContractCustomer() {}
    
    public ContractCustomer(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}