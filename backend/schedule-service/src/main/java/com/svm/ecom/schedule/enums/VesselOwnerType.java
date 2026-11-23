package com.svm.ecom.schedule.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Vessel ownership type enumeration.
 * Represents the ownership/operational arrangement of a vessel.
 */
@Schema(description = "Vessel Ownership Type: Chartered In, Chartered Out, Fully Operated, Partner Vessel, Re-Delivered, Slot Operated Vessel", enumAsRef = true)
public enum VesselOwnerType {
    
    @Schema(description = "Vessel chartered from another company")
    CHARTERED_IN("Chartered In"),
    
    @Schema(description = "Vessel chartered to another company")
    CHARTERED_OUT("Chartered Out"),
    
    @Schema(description = "Vessel fully owned and operated by the carrier")
    FULLY_OPERATED("Fully Operated"),
    
    @Schema(description = "Vessel operated by a partner company")
    PARTNER_VESSEL("Partner Vessel"),
    
    @Schema(description = "Vessel returned after charter period")
    RE_DELIVERED("Re-Delivered"),
    
    @Schema(description = "Slot charter arrangement on vessel")
    SLOT_OPERATED_VESSEL("Slot Operated Vessel");

    private final String value;
    
    VesselOwnerType(String value) {
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
     * Get the description of this vessel owner type.
     * @return Human-readable description
     */
    public String getDescription() {
        switch (this) {
            case CHARTERED_IN: return "Vessel chartered from another company";
            case CHARTERED_OUT: return "Vessel chartered to another company";
            case FULLY_OPERATED: return "Vessel fully owned and operated by the carrier";
            case PARTNER_VESSEL: return "Vessel operated by a partner company";
            case RE_DELIVERED: return "Vessel returned after charter period";
            case SLOT_OPERATED_VESSEL: return "Slot charter arrangement on vessel";
            default: return value;
        }
    }

    /**
     * Create enum from string value.
     * Handles null and empty strings by returning null.
     * Accepts various formats: exact match, case-insensitive, or with underscores.
     * 
     * @param value The string value to convert
     * @return The corresponding VesselOwnerType enum, or null if input is null/empty
     * @throws IllegalArgumentException if value doesn't match any known type
     */
    @JsonCreator
    public static VesselOwnerType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String trimmedValue = value.trim();
        
        for (VesselOwnerType type : VesselOwnerType.values()) {
            // Check exact match
            if (type.value.equalsIgnoreCase(trimmedValue)) {
                return type;
            }
            // Check enum name (e.g., CHARTERED_IN)
            if (type.name().equalsIgnoreCase(trimmedValue)) {
                return type;
            }
            // Check with underscores replaced by spaces
            if (type.name().replace("_", " ").equalsIgnoreCase(trimmedValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid VesselOwnerType value: " + value + 
            ". Valid values are: Chartered In, Chartered Out, Fully Operated, Partner Vessel, Re-Delivered, Slot Operated Vessel");
    }
}
