package com.svm.ecom.master.dto.v2;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ENS (Export Notification System) declarant. Same documentation style as client request standard.
 */
@Schema(name = "Ens", description = "Represents ENS (Export Notification System) declarant details for the customer.",
        example = "{\"declarantName\":\"COMMODITY SUPPLIES AG\",\"email\":\"Lidl@commodity.ch\",\"eoriId\":\"DE1172522\",\"defaultDeclarant\":true}")
public class EnsDto {

    @Schema(description = "Name of the declarant.", example = "COMMODITY SUPPLIES AG")
    private String declarantName;
    @Schema(description = "Email address of the declarant.", example = "Lidl@commodity.ch")
    private String email;
    @Schema(description = "EORI ID of the declarant.", example = "DE1172522")
    private String eoriId;
    @Schema(description = "Indicates whether this is the default declarant.", example = "true")
    private Boolean defaultDeclarant;

    public String getDeclarantName() { return declarantName; }
    public void setDeclarantName(String declarantName) { this.declarantName = declarantName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEoriId() { return eoriId; }
    public void setEoriId(String eoriId) { this.eoriId = eoriId; }
    public Boolean getDefaultDeclarant() { return defaultDeclarant; }
    public void setDefaultDeclarant(Boolean defaultDeclarant) { this.defaultDeclarant = defaultDeclarant; }
}
