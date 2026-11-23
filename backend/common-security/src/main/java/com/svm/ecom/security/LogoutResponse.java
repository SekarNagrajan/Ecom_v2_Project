package com.svm.ecom.security;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response object for logout API
 */
@Schema(description = "Response object for user logout")
public class LogoutResponse {

    @Schema(description = "Status code", example = "200")
    private String code;

    @Schema(description = "Status message", example = "Successfully logged out")
    private String message;

    public LogoutResponse() {}

    public LogoutResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "LogoutResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
