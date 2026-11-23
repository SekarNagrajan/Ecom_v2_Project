package com.svm.ecom.rates.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Contract Status: A – Active, E – Expired, D – Draft, W – Work In Progress", enumAsRef = true)
public enum ContractStatus {

    @Schema(description = "A – Active")
    A("A"),

    @Schema(description = "E – Expired")
    E("E"),

    @Schema(description = "D – Draft")
    D("D"),

    @Schema(description = "W – Work In Progress")
    W("W");

    private final String code;

    ContractStatus(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public static ContractStatus fromCode(String code) {
        if ("E".equals(code)) return E;
        if ("D".equals(code)) return D;
        return A;
    }

    /**
     * Resolves status with WIP logic: DB status 'D' on a contract whose number
     * starts with "WIP" is mapped to WorkInProgress (W) instead of Draft (D).
     */
    public static ContractStatus fromCode(String code, String contractNo) {
        if ("E".equals(code)) return E;
        if ("D".equals(code)) {
            if (contractNo != null && contractNo.toUpperCase().startsWith("WIP")) {
                return W;
            }
            return D;
        }
        return A;
    }
}
