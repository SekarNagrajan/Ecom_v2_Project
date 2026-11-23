package com.svm.ecom.master.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Customer status from Masters DB (customermaster.custposition).
 * API returns code only (e.g. "VAL", "PRO").
 */
@Schema(description = "Customer status: PRO (Provisional), VAL (Validated)")
public enum CustomerStatus {
    @Schema(description = "Provisional")
    PRO("Provisional"),
    @Schema(description = "Validated")
    VAL("Validated");

    private final String value;

    CustomerStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonValue
    public String getCode() {
        return name();
    }

    public static CustomerStatus fromCode(String code) {
        if (code == null) return null;
        for (CustomerStatus s : values()) {
            if (s.name().equalsIgnoreCase(code)) return s;
        }
        return null;
    }
}
