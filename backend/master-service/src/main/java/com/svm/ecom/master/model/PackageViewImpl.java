package com.svm.ecom.master.model;

/**
 * Implementation class for PackageView interface
 * Used with native SQL queries
 */
public class PackageViewImpl implements PackageView {

    private String packageCode;
    private String description;

    public PackageViewImpl(String packageCode, String description) {
        this.packageCode = packageCode;
        this.description = description;
    }

    @Override
    public String getPackageCode() {
        return packageCode;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
