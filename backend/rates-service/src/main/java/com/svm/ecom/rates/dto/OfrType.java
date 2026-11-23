package com.svm.ecom.rates.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ocean Freight Rate type: O – Ocean Freight Rate, A – All In", enumAsRef = true)
public enum OfrType {

    @Schema(description = "O – Ocean Freight Rate")
    O("O"),

    @Schema(description = "A – All In")
    A("A");

    private final String code;

    OfrType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public static OfrType fromCode(String code) {
        if ("A".equalsIgnoreCase(code)) return A;
        if ("O".equalsIgnoreCase(code)) return O;
        return null;
    }
}
