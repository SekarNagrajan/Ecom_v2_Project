package com.svm.ecom.master.model;

/**
 * Interface-based projection for country master data
 * Used with Spring Data JPA native queries
 */
public interface CountryView {
    String getCountryCode();

    String getCountryName();
}
