/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.svm.ecom.master.api;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 *
 * @author jayashree.muthukumar
 */
@Schema(description = "Commodity master data")
public class CommodityDoc {
    @Schema(description = "Commodity code", example = "91", required = true)
    private String commodityCode;

    @Schema(description = "Commodity name", example = "Commodity for Coffee or Tea", required = true)
    private String commodityName;

    // Constructors
    public CommodityDoc() {
    }

    public CommodityDoc(String commodityCode, String commodityName) {
        this.commodityCode = commodityCode;
        this.commodityName = commodityName;
    }

    // Getters and setters
    public String getCommodityCode(){
        return commodityCode;   
    }

    public void setCommodityCode(String commodityCode){
        this.commodityCode = commodityCode;
    }

    public String getCommodityName(){
        return commodityName;
    }

    public void setCommodityName(String commodityName){
        this.commodityName = commodityName;
    }
}  
 
 