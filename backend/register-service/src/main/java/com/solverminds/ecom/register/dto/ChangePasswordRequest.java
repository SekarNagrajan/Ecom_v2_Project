package com.solverminds.ecom.register.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Schema(description = ModelDocs.ChangePasswordRequest.DESC)
public class ChangePasswordRequest {

    @Schema(description = ModelDocs.ChangePasswordRequest.CURRENT_PASSWORD, example = ModelDocs.Common.EX_PASSWORD, required = true)
    @NotBlank(message = "Current password is required")
    @Size(min = 1, max = 100, message = "Current password must be between 1 and 100 characters")
    private String currentPassword;

    @Schema(description = ModelDocs.ChangePasswordRequest.NEW_PASSWORD, example = ModelDocs.Common.EX_PASSWORD, required = true)
    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 100, message = "New password must be between 6 and 100 characters")
    private String newPassword;

    @Schema(description = ModelDocs.ChangePasswordRequest.CONFIRM_PASSWORD, example = ModelDocs.Common.EX_PASSWORD, required = true)
    @NotBlank(message = "Confirm new password is required")
    @Size(min = 6, max = 100, message = "Confirm new password must be between 6 and 100 characters")
    private String confirmNewPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
