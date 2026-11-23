package com.svm.ecom.master.dto.v2;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Address for v2 Customer Master API. Matches client request standard (ISO 19160-4 style).
 */
@Schema(name = "Address", description = "A physical postal address capable of supporting international formats. Adheres to a hybrid of ISO 19160-4 and simplistic rendering requirements. Required: countryCode, lines, locality. Optional: postalCode.",
        example = "{\"lines\":[\"Floor 4, Unit 12\",\"123 Innovation Drive\"],\"locality\":\"Neckarsulm\",\"countryCode\":\"DE\"}")
public class AddressDto {

    @ArraySchema(
            minItems = 1,
            schema = @Schema(type = "string"),
            arraySchema = @Schema(description = "The street address, including building number, street name, apartment, suite, or unit. Ordered from specific to general.", example = "[\"Floor 4, Unit 12\",\"123 Innovation Drive\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    )
    private List<String> lines;
    @Schema(description = "The primary locality. Depending on the country, this is the City, Town, or Village.", example = "Neckarsulm", requiredMode = Schema.RequiredMode.REQUIRED)
    private String locality;
    @Schema(description = "The postal or zip code. Alphanumeric to support global formats (e.g., UK, Canada). Optional.", example = "74172", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 20, pattern = "^[A-Za-z0-9][A-Za-z0-9\\s\\-]*$")
    private String postalCode;
    @Schema(description = "The ISO 3166-1 alpha-2 country code.", example = "DE", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 2, pattern = "^[A-Z]{2}$")
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
