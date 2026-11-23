package com.solverminds.ecom.register.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;

@Schema(description = ModelDocs.RegistrationResponse.DESC)
public class RegistrationResponse {

    @Schema(description = "Registration success indicator", example = "true")
    private boolean success;
    
    @Schema(description = "Response message", example = "Registration completed successfully")
    private String message;
    
    @Schema(description = "Generated registration number", example = "REG12345")
    private String regNo;

    @Schema(description = ModelDocs.RegistrationResponse.USER_ID, example = ModelDocs.Common.EX_USER_ID)
    private String userId;

    // Default constructor
    public RegistrationResponse() {
    }

    // Constructor for success response
    public RegistrationResponse(boolean success, String message, String regNo) {
        this.success = success;
        this.message = message;
        this.regNo = regNo;
    }

    // Constructor for success response with userId
    public RegistrationResponse(boolean success, String message, String regNo, String userId) {
        this.success = success;
        this.message = message;
        this.regNo = regNo;
        this.userId = userId;
    }

    // Constructor for error response
    public RegistrationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters and Setters
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

    // Factory method for consistent response creation
    public static RegistrationResponse of(boolean success, String message, String regNo, String userId) {
        RegistrationResponse r = new RegistrationResponse();
        r.setSuccess(success);
        r.setMessage(message);
        r.setRegNo(regNo);
        r.setUserId(userId);
        return r;
    }
}
