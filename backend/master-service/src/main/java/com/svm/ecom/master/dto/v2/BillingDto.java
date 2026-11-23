package com.svm.ecom.master.dto.v2;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Billing address and contact. Same documentation style as client request standard.
 */
@Schema(name = "Billing", description = "Represents billing address and contact details for the customer.",
        example = "{\"name\":\"LIDL EMB KG\",\"phone\":\"+49-7132-942220\",\"email\":\"test@lidl.com\",\"mobile\":\"+49-7132-942220\",\"lines\":[\"Floor 4, Unit 12\",\"123 Innovation Drive\"],\"locality\":\"Neckarsulm\",\"postalCode\":\"74172\",\"countryCode\":\"DE\"}")
public class BillingDto {

    @Schema(description = "Name of the billing addressee.", example = "LIDL EMB KG")
    private String name;
    @Schema(description = "Telephone number for billing contact.", example = "+49-7132-942220")
    private String phone;
    @Schema(description = "Email address for billing contact.", example = "test@lidl.com")
    private String email;
    @Schema(description = "Mobile number for billing contact.", example = "+49-7132-942220")
    private String mobile;
    @ArraySchema(
            minItems = 1,
            schema = @Schema(type = "string"),
            arraySchema = @Schema(description = "Street address lines for billing. Ordered from specific to general.", example = "[\"Floor 4, Unit 12\",\"123 Innovation Drive\"]")
    )
    private List<String> lines;
    @Schema(description = "The primary locality (city, town, or village) for billing.", example = "Neckarsulm")
    private String locality;
    @Schema(description = "The postal or zip code for billing address.", example = "74172")
    private String postalCode;
    @Schema(description = "The ISO 3166-1 alpha-2 country code for billing address.", example = "DE")
    private String countryCode;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public List<String> getLines() { return lines; }
    public void setLines(List<String> lines) { this.lines = lines; }
    public String getLocality() { return locality; }
    public void setLocality(String locality) { this.locality = locality; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
}
