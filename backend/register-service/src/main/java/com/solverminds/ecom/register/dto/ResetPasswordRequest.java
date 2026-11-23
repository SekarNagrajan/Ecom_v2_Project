package com.solverminds.ecom.register.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Schema(description = ModelDocs.ResetPasswordRequest.DESC)
public class ResetPasswordRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Schema(description = ModelDocs.ResetPasswordRequest.EMAIL, example = ModelDocs.Common.EX_EMAIL, required = true)
    private String email;

    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Schema(description = ModelDocs.ResetPasswordRequest.NEW_PASSWORD, example = ModelDocs.Common.EX_PASSWORD, required = true)
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    @Size(min = 6, max = 100, message = "Confirm password must be between 6 and 100 characters")
    @Schema(description = ModelDocs.ResetPasswordRequest.CONFIRM_PASSWORD, example = ModelDocs.Common.EX_PASSWORD, required = true)
    private String confirmPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}

