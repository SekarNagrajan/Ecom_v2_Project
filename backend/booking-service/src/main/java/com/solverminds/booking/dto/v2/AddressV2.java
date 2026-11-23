package com.solverminds.booking.dto.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * A physical postal address capable of supporting international formats.
 * Adheres to a hybrid of ISO 19160-4 and simplistic rendering requirements.
 */
@Schema(name = "Address", description = "A physical postal address capable of supporting international formats")
public class AddressV2 {

    @NotEmpty
    @Schema(description = "The street address, including building number, street name, apartment, suite, or unit. Ordered from specific to general.",
            example = "[\"Floor 4, Unit 12\", \"123 Innovation Drive\"]")
    private List<String> lines;

    @NotBlank
    @Schema(description = "The primary locality. Depending on the country, this is the City, Town, or Village.",
            example = "San Francisco")
    private String locality;

    @NotBlank
    @Size(max = 15)
    @Pattern(regexp = "^[A-Za-z0-9][A-Za-z0-9\\s\\-]*$")
    @Schema(description = "The postal or zip code. Alphanumeric to support global formats.",
            example = "94103", maxLength = 15)
    private String postalCode;

    @NotBlank
    @Size(min = 2, max = 2)
    @Pattern(regexp = "^[A-Z]{2}$")
    @Schema(description = "The ISO 3166-1 alpha-2 country code.",
            example = "US", minLength = 2, maxLength = 2)
    private String countryCode;

    public AddressV2() {
    }

    public AddressV2(List<String> lines, String locality, String postalCode, String countryCode) {
        this.lines = lines;
        this.locality = locality;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
    }

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

