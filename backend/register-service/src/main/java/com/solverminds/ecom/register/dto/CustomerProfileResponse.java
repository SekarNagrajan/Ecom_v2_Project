package com.solverminds.ecom.register.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.svm.ecom.constants.DateFormatConstants;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Customer profile information response")
public class CustomerProfileResponse {
    
    @Schema(description = "Username", example = "john.doe")
    private String username;
    
    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "First name", example = "John")
    private String firstName;
    
    @Schema(description = "Last name", example = "Doe")
    private String lastName;
    
    @Schema(description = "Phone number", example = "+1-234-567-8900")
    private String phoneNumber;
    
    @Schema(description = "Customer status", example = "ACTIVE")
    private String customerStatus;
    
    @Schema(type = "string", format = "date-time", description = "Date when customer profile was created (UTC with Z suffix)", example = "2023-11-23T14:30:00Z")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime createdDate;
    
    @Schema(type = "string", format = "date-time", description = "Date of last login (UTC with Z suffix)", example = "2023-11-23T14:30:00Z")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime lastLoginDate;

    public CustomerProfileResponse() {
    }

    public CustomerProfileResponse(String username, String email, String firstName, String lastName, 
                                 String phoneNumber, String customerStatus, LocalDateTime createdDate, LocalDateTime lastLoginDate) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.customerStatus = customerStatus;
        this.createdDate = createdDate;
        this.lastLoginDate = lastLoginDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCustomerStatus() {
        return customerStatus;
    }

    public void setCustomerStatus(String customerStatus) {
        this.customerStatus = customerStatus;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
}
