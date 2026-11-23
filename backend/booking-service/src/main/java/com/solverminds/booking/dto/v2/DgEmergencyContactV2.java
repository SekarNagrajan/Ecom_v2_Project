package com.solverminds.booking.dto.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Emergency contact for dangerous goods (PII).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "DgEmergencyContact", description = "Emergency contact details for dangerous goods")
public class DgEmergencyContactV2 {

    @Schema(description = "Contact person name", example = "MME MARTINE BRANCA")
    private String contactName;

    @Schema(description = "Telephone country code (digits)", example = "33")
    private String countryCode;

    @Schema(description = "Telephone area code (digits)", example = "1")
    private String areaCode;

    @Schema(description = "Telephone local number (digits)", example = "23456789")
    private String localNumber;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getLocalNumber() {
        return localNumber;
    }

    public void setLocalNumber(String localNumber) {
        this.localNumber = localNumber;
    }
}
