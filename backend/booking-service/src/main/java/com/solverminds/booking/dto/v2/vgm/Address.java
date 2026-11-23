package com.solverminds.booking.dto.v2.vgm;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "Address",
        description = "A physical postal address capable of supporting international formats. "
                + "Adheres to a hybrid of ISO 19160-4 and simplistic rendering requirements.")
public class Address {

    @ArraySchema(
            schema = @Schema(description = "The street address, including building number, street name, apartment, suite, or unit. "
                    + "Ordered from specific to general."))
    private List<String> lines;

    @Schema(
            description = "The primary locality. Depending on the country, this is the City, Town, or Village.")
    private String locality;

    @Size(min = 2, max = 2, message = "countryCode must be a 2-letter ISO code")
    @Pattern(
            regexp = VgmSchemaConstants.COUNTRY_CODE_PATTERN,
            message = "countryCode must be a 2-letter ISO 3166-1 alpha-2 code (uppercase)")
    @Schema(
            description = VgmSchemaConstants.COUNTRY_CODE_DESCRIPTION,
            example = "US",
            minLength = 2,
            maxLength = 2,
            pattern = VgmSchemaConstants.COUNTRY_CODE_PATTERN)
    private String countryCode;

    @Size(max = 20, message = "postalCode must not exceed 20 characters")
    @Pattern(
            regexp = "^$|^[A-Za-z0-9][A-Za-z0-9\\s\\-]*$",
            message = "postalCode contains invalid characters")
    @Schema(
            description = "The postal or zip code. Alphanumeric to support global formats (e.g., UK, Canada).",
            maxLength = 20,
            pattern = "^[A-Za-z0-9][A-Za-z0-9\\s\\-]*$")
    private String postalCode;

    @Schema(description = "The top-level administrative subdivision of the country. E.g., State, Province, Prefecture, Region, or Canton.")
    private String administrativeArea;
}
