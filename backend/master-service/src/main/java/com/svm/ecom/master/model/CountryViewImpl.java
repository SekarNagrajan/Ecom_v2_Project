package com.svm.ecom.master.model;

/**
 * Implementation class for CountryView interface
 * Used with native SQL queries
 */
public class CountryViewImpl implements CountryView {

    private String countryCode;
    private String countryName;

    public CountryViewImpl(String countryCode, String countryName) {
        this.countryCode = countryCode;
        this.countryName = countryName;
    }

    @Override
    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String getCountryName() {
        return countryName;
    }

}
