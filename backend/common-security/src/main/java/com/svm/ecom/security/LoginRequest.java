package com.svm.ecom.security;

import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Request object for user authentication
 */
@Schema(description = ModelDocs.LoginRequest.DESC)
public class LoginRequest {

    @Schema(
        description = ModelDocs.LoginRequest.USERNAME, 
        example = ModelDocs.LoginRequest.EX_USERNAME, 
        required = true
    )
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(
        description = ModelDocs.LoginRequest.PASSWORD, 
        example = ModelDocs.Common.EX_PASSWORD, 
        required = true
    )
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    /**
     * Default constructor
     */
    public LoginRequest() {}

    /**
     * Constructor with username and password
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}