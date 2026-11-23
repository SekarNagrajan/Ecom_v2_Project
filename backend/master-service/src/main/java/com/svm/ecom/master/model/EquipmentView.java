package com.svm.ecom.master.model;

/**
 * Interface-based projection for equipment master data
 * Used with Spring Data JPA native queries
 */
public interface EquipmentView {
    String getEquipmentType();

    String getEquipmentDescription();
}
