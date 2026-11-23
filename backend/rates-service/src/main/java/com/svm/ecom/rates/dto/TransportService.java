package com.svm.ecom.rates.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transport service type", enumAsRef = true)
public enum TransportService {

    DOOR_DOOR("DOOR / DOOR"),
    DOOR_CY("DOOR / CY"),
    FI_CY("FI / CY"),
    FI_FO("FI / FO"),
    CY_DOOR("CY / DOOR"),
    CY_CY("CY / CY"),
    CY_FO("CY / FO");

    private final String code;

    TransportService(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public static TransportService fromCode(String code) {
        if (code == null) return null;
        String normalised = code.trim().replaceAll("\\s+", " ").toUpperCase();
        for (TransportService ts : values()) {
            if (ts.code.replaceAll("\\s+", " ").equalsIgnoreCase(normalised)) {
                return ts;
            }
        }
        return null;
    }
}
