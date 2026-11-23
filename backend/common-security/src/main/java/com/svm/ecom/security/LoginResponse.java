package com.svm.ecom.security;

import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;

/**
 * Data Transfer Object for login response.
 * Contains JWT access token.
 */
@Schema(description = ModelDocs.LoginResponse.DESC)
public class LoginResponse {

    @Schema(
        description = ModelDocs.LoginResponse.TOKEN, 
        required = true, 
        example = ModelDocs.Common.EX_JWT_TOKEN
    )
    private String token;

    // Refresh token field commented out for future use
    // @Schema(
    //     description = "JWT refresh token for obtaining new access tokens", 
    //     required = true, 
    //     example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    // )
    // private String refreshToken;

    /**
     * Default constructor for Jackson serialization
     */
    public LoginResponse() {
    }

    /**
     * Constructor with token parameter
     * 
     * @param token the access token
     */
    public LoginResponse(String token) {
        this.token = token;
    }

    // Constructor with refresh token commented out for future use
    // public LoginResponse(String token, String refreshToken) {
    //     this.token = token;
    //     this.refreshToken = refreshToken;
    // }

    /**
     * Get the access token
     * 
     * @return the access token
     */
    public String getToken() {
        return token;
    }

    /**
     * Set the access token
     * 
     * @param token the access token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    // Refresh token getters and setters commented out for future use
    // /**
    //  * Get the refresh token
    //  * 
    //  * @return the refresh token
    //  */
    // public String getRefreshToken() {
    //     return refreshToken;
    // }

    // /**
    //  * Set the refresh token
    //  * 
    //  * @param refreshToken the refresh token to set
    //  */
    // public void setRefreshToken(String refreshToken) {
    //     this.refreshToken = refreshToken;
    // }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='[PROTECTED]'" +
                '}';
    }
}