package com.svm.ecom.master.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.master.enums.PortType;

/**
 * Interface-based projection for port master data
 * Used with Spring Data JPA native queries
 */
public interface PortView {
    String getPortCode();
    String getPortName();
    PortType getPortType();
    Double getLatitude();
    Double getLongitude();
    String getTimezoneOffset();
    String getCountryCode();
    @JsonProperty("isEur1875")
    boolean isEur1875();
}
