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
public class CommodityViewImpl implements CommodityView{ 
    private String commodityCode;
    private String commodityName;

    public CommodityViewImpl(String commodityCode, String commodityName) { 
        this.commodityCode = commodityCode;
        this.commodityName = commodityName;
    }

    @Override
    public String getCommodityCode() {
        return commodityCode;
    }

    @Override
    public String getCommodityName() {
        return commodityName;
    }
}
