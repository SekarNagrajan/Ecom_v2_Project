/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.svm.ecom.master.model;

/**
 *
 * @author jayashree.muthukumar
 */
public class DGViewImpl implements DGView {
    private String unNo;
    private String classCode;
    private String shippingName;

    public DGViewImpl(String unNo, String classCode, String shippingName) {
        this.unNo = unNo;
        this.classCode = classCode;
        this.shippingName = shippingName;
    }

    @Override
    public String getUnNo() {
        return unNo;
    }

    @Override
    public String getClassCode() {
        return classCode;
    }   

    @Override
    public String getShippingName() {
        return shippingName;
    }           
 
}