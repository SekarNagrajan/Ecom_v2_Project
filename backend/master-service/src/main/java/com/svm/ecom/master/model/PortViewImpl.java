package com.svm.ecom.master.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.master.enums.PortType;

/**
 * Implementation class for PortView interface
 * Used with native SQL queries
 */
public class PortViewImpl implements PortView {
    
    private String portCode;
    private String portName;
    private PortType portType;
    private Double latitude;
    private Double longitude;
    private String timezoneOffset;
    private String countryCode;
    private boolean eur1875;
    
    public PortViewImpl(String portCode, String portName, String portTypeValue,
                        Double latitude, Double longitude, String timezoneOffset, String countryCode, String eur) {
        this.portCode = portCode;
        this.portName = portName;
        this.portType = safePortType(portTypeValue);
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezoneOffset = formatTimezoneOffset(timezoneOffset);
        this.countryCode = countryCode;
        this.eur1875 = isEurFlagEnabled(eur);
    }
    
    @Override
    public String getPortCode() {
        return portCode;
    }
    
    @Override
    public String getPortName() {
        return portName;
    }
    
    @Override
    public PortType getPortType() {
        return portType;
    }

    @Override
    public Double getLatitude() {
        return latitude;
    }

    @Override
    public Double getLongitude() {
        return longitude;
    }

    @Override
    public String getTimezoneOffset() {
        return timezoneOffset;
    }

    @Override
    public String getCountryCode() {
        return countryCode;
    }

    @Override
    @JsonProperty("isEur1875")
    public boolean isEur1875() {
        return eur1875;
    }

    /**
     * Safely convert String to PortType enum
     */
    private PortType safePortType(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return PortType.fromValue(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert raw numeric offset (e.g. "3", "8.0", "-5.5", "5.75") to UTC format (e.g. "+03:00", "+08:00", "-05:30", "+05:45")
     */
    private String formatTimezoneOffset(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            double offset = Double.parseDouble(value.trim());
            String sign = offset >= 0 ? "+" : "-";
            double abs = Math.abs(offset);
            int hours = (int) abs;
            int minutes = (int) Math.round((abs - hours) * 60);
            return String.format("%s%02d:%02d", sign, hours, minutes);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    /**
     * Convert EUR flag ('Y'/'N'/NULL) to boolean API value.
     */
    private boolean isEurFlagEnabled(String eur) {
        return eur != null && "Y".equalsIgnoreCase(eur.trim());
    }
}
