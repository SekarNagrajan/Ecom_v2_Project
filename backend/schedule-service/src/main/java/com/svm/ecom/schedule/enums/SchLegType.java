package com.svm.ecom.schedule.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Leg type enumeration for schedule routes.
 * Indicates whether a route consists of a single leg or multiple legs (with transshipments).
 */
@Schema(description = "Leg Type: Single (direct route), Multiple (route with transshipments)", enumAsRef = true)
public enum SchLegType {
    
    @Schema(description = "Direct route with no transshipments")
    SINGLE("Single"),
    
    @Schema(description = "Route with one or more transshipments")
    MULTIPLE("Multiple");

    private final String value;
    
    SchLegType(String value) {
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

    /**
     * Get human-readable description of the leg type.
     * @return Description of what this leg type means
     */
    public String getDescription() {
        return this == SINGLE 
            ? "Direct route with no transshipments" 
            : "Route with one or more transshipments";
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     * 
     * @param value The string value to convert (Single or Multiple)
     * @return The corresponding SchLegType enum, or null if input is null/empty
     * @throws IllegalArgumentException if value is not Single or Multiple
     */
    @JsonCreator
    public static SchLegType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String trimmedValue = value.trim();
        for (SchLegType type : SchLegType.values()) {
            if (type.value.equalsIgnoreCase(trimmedValue)) {
                return type;
            }
            if (type.name().equalsIgnoreCase(trimmedValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid SchLegType value: " + value + 
            ". Valid values are: Single (direct route), Multiple (with transshipments)");
    }
}

