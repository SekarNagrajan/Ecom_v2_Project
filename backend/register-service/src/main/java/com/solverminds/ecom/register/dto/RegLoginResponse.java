package com.solverminds.ecom.register.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Schema(description = ModelDocs.RegLoginResponse.DESC)
public class RegLoginResponse {

    @Schema(description = ModelDocs.RegLoginResponse.ACCESS_TOKEN, example = ModelDocs.Common.EX_JWT_TOKEN)
    private String accessToken;
    
    @Schema(description = ModelDocs.RegLoginResponse.REFRESH_TOKEN, example = ModelDocs.Common.EX_REFRESH_TOKEN)
    private String refreshToken;
    
    @Schema(description = ModelDocs.RegLoginResponse.TOKEN_TYPE, example = ModelDocs.RegLoginResponse.EX_TOKEN_TYPE, defaultValue = "Bearer")
    private String tokenType = "Bearer";
    
    @Schema(description = ModelDocs.RegLoginResponse.EXPIRES_IN, example = ModelDocs.RegLoginResponse.EX_EXPIRES_IN)
    private int expiresInSeconds;
    
    @Schema(description = "Refresh token validity duration in seconds", example = "86400")
    private int refreshExpiresInSeconds;

    public RegLoginResponse(String accessToken, String refreshToken, int expiresInSeconds, int refreshExpiresInSeconds) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresInSeconds = expiresInSeconds;
        this.refreshExpiresInSeconds = refreshExpiresInSeconds;
        this.tokenType = "Bearer";
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public int getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(int expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    public int getRefreshExpiresInSeconds() {
        return refreshExpiresInSeconds;
    }

    public void setRefreshExpiresInSeconds(int refreshExpiresInSeconds) {
        this.refreshExpiresInSeconds = refreshExpiresInSeconds;
    }
}
