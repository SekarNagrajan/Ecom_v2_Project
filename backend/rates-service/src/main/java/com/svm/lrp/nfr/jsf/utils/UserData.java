package com.svm.lrp.nfr.jsf.utils;

/**
 * Minimal stub of Booking NFR {@code UserData}.
 * <p>
 * {@code CommonSurcharge} declares a {@code UserData mData} field, so the JVM
 * requires this type on the classpath when the client class is loaded.
 * Estimate API does not use JSF session context; getters return empty defaults.
 */
public class UserData {

    private String userCode = "";
    private String agencyCode = "";
    private String branchCode = "";
    private String carrierCode = "";
    private String locationCode = "";
    private String countryCode = "";
    private String corpAgency = "";
    private String systemType = "";
    private String userType = "";
    private String dateFormat = "dd/MM/yyyy";
    private String dateTimeFormat = "dd/MM/yyyy HH:mm";

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCorpAgency() {
        return corpAgency;
    }

    public void setCorpAgency(String corpAgency) {
        this.corpAgency = corpAgency;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }
}
