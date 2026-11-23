package com.svm.ecom.master.api;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenAPI documentation schema for country data
 * Used only for API documentation purposes
 */
@Schema(description = "Country master data")
public class CountryDoc {

    @Schema(description = "Country code", example = "IN", required = true)
    private String countryCode;

    @Schema(description = "Country name", example = "India", required = true)
    private String countryName;

    // Constructors
    public CountryDoc() {
    }

    public CountryDoc(String countryCode, String countryName) {
        this.countryCode = countryCode;
        this.countryName = countryName;
    }

    // Getters and setters
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

}
