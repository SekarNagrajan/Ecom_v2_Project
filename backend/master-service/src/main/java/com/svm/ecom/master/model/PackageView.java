package com.svm.ecom.master.model;

/**
 * Interface-based projection for package master data
 * Used with Spring Data JPA native queries
 */
public interface PackageView {

    String getPackageCode();

    String getDescription();

}