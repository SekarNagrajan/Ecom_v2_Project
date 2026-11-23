package com.svm.ecom.master.api;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenAPI documentation schema for package data
 * Used only for API documentation purposes
 */
@Schema(description = "Package master data")
public class PackageDoc {

    @Schema(description = "packagecode", example = "BOTL", required = true)
    private String packageCode;

    @Schema(description = "description", example = "BOTTLE", required = true)
    private String description;

    // Constructors
    public PackageDoc() {
    }

    public PackageDoc(String packageCode, String description) {
        this.packageCode = packageCode;
        this.description = description;
    }

    // Getters and setters
    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
