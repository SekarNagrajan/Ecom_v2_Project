package com.solverminds.booking.dto.v2.vgm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * VGM weighing method (SOLAS).
 */
@Schema(
        name = "VgmMethod",
        description = "VGM Method: SM1 , SM2 ",
        enumAsRef = true,
        example = "SM1")
public enum VgmMethod {

    @Schema(description = "SM1 ")
    SM1("SM1"),

    @Schema(description = "SM2")
    SM2("SM2");

    private final String value;

    VgmMethod(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static VgmMethod fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (VgmMethod method : values()) {
            if (method.value.equalsIgnoreCase(value.trim())) {
                return method;
            }
        }
        throw new IllegalArgumentException(
                "Invalid VgmMethod value: " + value + ". Valid values are: SM1, SM2");
    }
}
