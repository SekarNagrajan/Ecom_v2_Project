package com.svm.ecom.master.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DG packaging usage type.
 * Documentation DB {@code inoutpackagecode.type} stores o / i / b; API exposes OUTER / INNER / BOTH.
 */
@Schema(description = "DG packaging type: OUTER, INNER, or BOTH",
        enumAsRef = true, example = "OUTER")
public enum DgPackagingType {

    @Schema(description = "Outer packaging")
    OUTER("O"),

    @Schema(description = "Inner packaging")
    INNER("I"),

    @Schema(description = "Usable as outer or inner packaging")
    BOTH("B");

    private final String dbValue;

    DgPackagingType(String dbValue) {
        this.dbValue = dbValue;
    }

    @JsonValue
    public String getValue() {
        return name();
    }

    public String getDbValue() {
        return dbValue;
    }

    @Override
    public String toString() {
        return name();
    }

    @JsonCreator
    public static DgPackagingType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String v = value.trim();
        for (DgPackagingType type : values()) {
            if (type.name().equalsIgnoreCase(v) || type.dbValue.equalsIgnoreCase(v)) {
                return type;
            }
        }
        throw new IllegalArgumentException(
                "Invalid DgPackagingType value: " + value + ". Valid values are: OUTER, INNER, BOTH (or O, I, B)");
    }
}
