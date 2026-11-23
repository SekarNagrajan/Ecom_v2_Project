package com.solverminds.ecom.register.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Schema(description = "Quick registration payload for provisional e-com users")
public class QuickRegistrationRequest {

    @NotBlank
    @Email
    @Schema(description = "User email (login id)", example = "user@example.com", required = true)
    private String emailId;

    @NotBlank
    @Size(min = 8)
    @Schema(description = "Password (min 8 characters)", example = "password123", required = true)
    private String password;

    @Schema(description = "Preferred timezone", example = "+05:30 (GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi")
    private String preferredTimezone;

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPreferredTimezone() {
        return preferredTimezone;
    }

    public void setPreferredTimezone(String preferredTimezone) {
        this.preferredTimezone = preferredTimezone;
    }
}
