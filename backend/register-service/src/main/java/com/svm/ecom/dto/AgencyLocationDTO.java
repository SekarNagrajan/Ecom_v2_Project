package com.svm.ecom.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AgencyLocation", description = "Agency location with port and agency information")
public class AgencyLocationDTO {
    
    @Schema(description = "Port code", example = "INNSA")
    private String portCode;
    
    @Schema(description = "Port name", example = "Nhava Sheva")
    private String portName;
    
    @Schema(description = "Agency code", example = "INDMUM")
    private String agencyCode;

    // Default constructor
    public AgencyLocationDTO() {
    }

    // Constructor with all fields
    public AgencyLocationDTO(String portCode, String portName, String agencyCode) {
        this.portCode = portCode;
        this.portName = portName;
        this.agencyCode = agencyCode;
    }

    // Getters and Setters
    public String getPortCode() {
        return portCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    @Override
    public String toString() {
        return "AgencyLocationDTO{" +
                "portCode='" + portCode + '\'' +
                ", portName='" + portName + '\'' +
                ", agencyCode='" + agencyCode + '\'' +
                '}';
    }
}
