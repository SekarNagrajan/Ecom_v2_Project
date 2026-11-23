package com.solverminds.ecom.register.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Schema(description = ModelDocs.RegLoginRequest.DESC)
public class RegLoginRequest {

    @NotBlank(message = "Username is required")
    @Schema(description = ModelDocs.RegLoginRequest.EMAIL, example = ModelDocs.Common.EX_EMAIL, required = true)
    private String username; // EmailID

    @NotBlank(message = "Password is required")
    @Schema(description = ModelDocs.RegLoginRequest.PASSWORD, example = ModelDocs.Common.EX_PASSWORD, required = true)
    private String password;

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
}
