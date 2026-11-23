package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Yes/No flag enumeration.
 * Y = Yes, N = No
 */
@Schema(description = "Yes/No flag: Y – Yes, N – No", enumAsRef = true)
public enum YesNoFlag {

    @Schema(description = "Y – Yes")
    Y("Y"),

    @Schema(description = "N – No")
    N("N");

    private final String value;

    YesNoFlag(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getDescription() {
        return this == Y ? "Yes" : "No";
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     */
    @JsonCreator
    public static YesNoFlag fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (YesNoFlag flag : YesNoFlag.values()) {
            if (flag.value.equalsIgnoreCase(value)) {
                return flag;
            }
        }
        throw new IllegalArgumentException("Invalid YesNoFlag value: " + value + ". Valid values are: Y, N");
    }
}

