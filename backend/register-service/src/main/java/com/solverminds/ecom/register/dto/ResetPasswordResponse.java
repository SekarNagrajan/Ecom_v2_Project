package com.solverminds.ecom.register.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Schema(description = ModelDocs.ResetPasswordResponse.DESC)
public class ResetPasswordResponse {

    @Schema(description = ModelDocs.ResetPasswordResponse.MESSAGE, example = ModelDocs.ResetPasswordResponse.EX_MESSAGE)
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

