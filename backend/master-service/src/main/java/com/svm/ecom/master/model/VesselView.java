package com.svm.ecom.master.model;

/**
 * Interface-based projection for vessel master data
 * Used with Spring Data JPA native queries
 */
public interface VesselView {
    String getVesselCode();
    String getVesselName();
    String getImoNumber();

    String getBuiltYear();
    String getBuiltMonth();
    String getStatus();
    Integer getGrossTonnage();
    Double getLoa();
    Double getBreadth();
    Double getDraft();
    Double getDeadWeight();
    Integer getReeferPlugs();
    Integer getCapacityTeu();
    String getFlag();
    String getCallSign();
}
