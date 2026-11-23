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
public class HSViewImpl implements HSView {
    private String hsCode;
    private String hsName;
    private String commodityGroup;

    public HSViewImpl(String hsCode, String hsName, String commodityGroup) {
        this.hsCode = hsCode;
        this.hsName = hsName;
        this.commodityGroup = commodityGroup;
    }

    @Override
    public String getHsCode() {
        return hsCode;
    }

    @Override
    public String getHsName() {
        return hsName;
    }

    @Override
    public String getCommodityGroup() {
        return commodityGroup;
    }
    
    public void setCommodityGroup(String commodityGroup) {
        this.commodityGroup = commodityGroup;
    }
}
