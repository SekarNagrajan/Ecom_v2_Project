package com.svm.ecom.common.master.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * Customer detail DTO for caching customer code and name.
 * Used by CommonMasterDataCacheService for efficient customer lookups.
 */
public class CustomerDetail implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String custCode;
    private String custName;
    
    public CustomerDetail() {
    }
    
    public CustomerDetail(String custCode, String custName) {
        this.custCode = custCode;
        this.custName = custName;
    }
    
    public String getCustCode() {
        return custCode;
    }
    
    public void setCustCode(String custCode) {
        this.custCode = custCode;
    }
    
    public String getCustName() {
        return custName;
    }
    
    public void setCustName(String custName) {
        this.custName = custName;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerDetail that = (CustomerDetail) o;
        return Objects.equals(custCode, that.custCode) && Objects.equals(custName, that.custName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(custCode, custName);
    }
    
    @Override
    public String toString() {
        return "CustomerDetail{" +
                "custCode='" + custCode + '\'' +
                ", custName='" + custName + '\'' +
                '}';
    }
}