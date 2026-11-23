package com.svm.ecom.carrier.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Coastal port call type.
 * C = Commercial, P = Physical
 */
@Schema(description = "Call type: C – Commercial, P – Physical", enumAsRef = true)
public enum CallType {

    @Schema(description = "C – Commercial")
    C("C"),

    @Schema(description = "P – Physical")
    P("P");

    private final String value;

    CallType(String value) {
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

    @JsonCreator
    public static CallType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (CallType type : CallType.values()) {
            if (type.value.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid CallType value: " + value + ". Valid values are: C, P");
    }
}
