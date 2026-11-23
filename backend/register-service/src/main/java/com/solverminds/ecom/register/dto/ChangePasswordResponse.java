package com.solverminds.ecom.register.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Schema(description = ModelDocs.ChangePasswordResponse.DESC)
public class ChangePasswordResponse {

    @Schema(description = ModelDocs.ChangePasswordResponse.MESSAGE, example = ModelDocs.ChangePasswordResponse.EX_MESSAGE)
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
