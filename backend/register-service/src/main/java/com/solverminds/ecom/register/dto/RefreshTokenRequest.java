package com.solverminds.ecom.register.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Schema(description = ModelDocs.RefreshTokenRequest.DESC)
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    @Schema(description = ModelDocs.RefreshTokenRequest.REFRESH_TOKEN, example = ModelDocs.Common.EX_REFRESH_TOKEN, required = true)
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
