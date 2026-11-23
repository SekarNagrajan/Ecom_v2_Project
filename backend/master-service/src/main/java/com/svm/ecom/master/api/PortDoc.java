package com.svm.ecom.master.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.master.enums.PortType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenAPI documentation schema for port data
 * Used only for API documentation purposes
 */
@Schema(description = "Port master data")
public class PortDoc {

    @Schema(description = "Port code (UN/LOCODE)", example = "NLRTM", required = true)
    private String portCode;

    @Schema(description = "Port name", example = "ROTTERDAM", required = true)
    private String portName;

    @Schema(description = "Port type (P=Port, I=Inland)", example = "P")
    private PortType portType;

    @Schema(description = "Latitude coordinate", example = "51.9475")
    private Double latitude;

    @Schema(description = "Longitude coordinate", example = "4.1347")
    private Double longitude;

    @Schema(description = "UTC timezone offset", example = "+01:00")
    private String timezoneOffset;

    @Schema(description = "Country code (ISO 3166-1 alpha-2)", example = "NL")
    private String countryCode;

    @JsonProperty("isEur1875")
    @Schema(description = "Flag indicating EUR 1875 eligibility (true if eur = 'Y', otherwise false)", example = "true")
    private boolean eur1875;

    public PortDoc() {}

    public PortDoc(String portCode, String portName, PortType portType,
                   Double latitude, Double longitude, String timezoneOffset, String countryCode, boolean eur1875) {
        this.portCode = portCode;
        this.portName = portName;
        this.portType = portType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezoneOffset = timezoneOffset;
        this.countryCode = countryCode;
        this.eur1875 = eur1875;
    }

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

    public PortType getPortType() {
        return portType;
    }

    public void setPortType(PortType portType) {
        this.portType = portType;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(String timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @JsonProperty("isEur1875")
    public boolean isEur1875() {
        return eur1875;
    }

    public void setEur1875(boolean eur1875) {
        this.eur1875 = eur1875;
    }
}
