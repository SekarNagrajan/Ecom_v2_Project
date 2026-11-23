package com.solverminds.ecom.register.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Email;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User profile information")
public class ProfileDto {

    @Schema(description = "Unique user identifier", example = "USR001")
    private String userId;

    @Schema(description = "User's full name", example = "John Doe")
    private String name;

    @Email(message = "Please provide a valid email address")
    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Company code/identifier", example = "ACME")
    private String company;

    @Schema(description = "Detailed company information")
    private CompanyDto companyDetails;

    @Schema(description = "List of customer contacts")
    private List<CustomerContactDto> customerContacts;

    @Schema(description = "User roles", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private List<String> roles;

    @Schema(description = "User's timezone", example = "Asia/Kolkata")
    private String timezone;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public CompanyDto getCompanyDetails() {
        return companyDetails;
    }

    public void setCompanyDetails(CompanyDto companyDetails) {
        this.companyDetails = companyDetails;
    }

    public List<CustomerContactDto> getCustomerContacts() {
        return customerContacts;
    }

    public void setCustomerContacts(List<CustomerContactDto> customerContacts) {
        this.customerContacts = customerContacts;
    }
}
