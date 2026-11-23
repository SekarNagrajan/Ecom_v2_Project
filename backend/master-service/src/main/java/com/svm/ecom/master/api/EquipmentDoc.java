package com.svm.ecom.master.api;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenAPI documentation schema for equipment data
 * Used only for API documentation purposes
 */
@Schema(description = "Equipment master data")
public class EquipmentDoc {

    @Schema(description = "Equipment type", example = "20DC", required = true)
    private String equipmentType;

    @Schema(description = "Equipment Description", example = "STANDARD 20DC CONTAINER", required = true)
    private String equipmentDescription;

    // Constructors
    public EquipmentDoc() {
    }

    public EquipmentDoc(String equipmentType, String equipmentDescription) {
        this.equipmentType = equipmentType;
        this.equipmentDescription = equipmentDescription;
    }

    // Getters and setters
    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getEquipmentDescription() {
        return equipmentDescription;
    }

    public void setEquipmentDescription(String equipmentDescription) {
        this.equipmentDescription = equipmentDescription;
    }

}
