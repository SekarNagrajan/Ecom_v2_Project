package com.svm.ecom.master.model;

/**
 * Implementation class for EquipmentView interface
 * Used with native SQL queries
 */
public class EquipmentViewImpl implements EquipmentView {

    private String equipmentType;
    private String equipmentDescription;

    public EquipmentViewImpl(String equipmentType, String equipmentDescription) {
        this.equipmentType = equipmentType;
        this.equipmentDescription = equipmentDescription;
    }

    @Override
    public String getEquipmentType() {
        return equipmentType;
    }

    @Override
    public String getEquipmentDescription() {
        return equipmentDescription;
    }

}
