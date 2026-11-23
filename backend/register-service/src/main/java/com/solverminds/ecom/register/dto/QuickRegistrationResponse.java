package com.solverminds.ecom.register.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;

@Schema(description = "Quick registration response envelope")
public class QuickRegistrationResponse {

    @Schema(description = "Success indicator", example = "true")
    private boolean success;

    @Schema(description = "Response message", example = "Quick registration successful")
    private String message;

    @Schema(description = "Generated registration number", example = "REG12345")
    private String regNo;

    @Schema(description = ModelDocs.RegistrationResponse.USER_ID, example = ModelDocs.Common.EX_USER_ID)
    private String userId;

    public QuickRegistrationResponse() {
    }

    public static QuickRegistrationResponse of(boolean success, String message, String regNo, String userId) {
        QuickRegistrationResponse response = new QuickRegistrationResponse();
        response.setSuccess(success);
        response.setMessage(message);
        response.setRegNo(regNo);
        response.setUserId(userId);
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
