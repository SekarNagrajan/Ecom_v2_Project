package com.svm.ecom.master.dto.v2;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "Address", description = "Postal address for an agency.")
public class AddressResponse {

    @ArraySchema(
            arraySchema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED),
            schema = @Schema(description = "Address line", example = "Floor 4"),
            minItems = 1
    )
    private List<String> lines;

    @Schema(description = "City or locality", example = "Mumbai", requiredMode = Schema.RequiredMode.REQUIRED)
    private String locality;

    @Schema(description = "ISO 3166-1 alpha-2 country code", example = "IN",
            requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 2, pattern = "^[A-Z]{2}$")
    private String countryCode;

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
