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
@Schema(description = "HS master data")
public class HSDoc {
      
    @Schema(description = "HS code", example = "10001", required = true)
    private String hsCode;

    @Schema(description = "HS name", example = "Animal Feeds", required = true)
    private String hsName;

    @Schema(description = "Commodity group", example = "01", required = true)
    private String commodityGroup;

    // Constructors
    public HSDoc() {
    }

    public HSDoc(String hsCode, String hsName) {
        this.hsCode = hsCode;
        this.hsName = hsName;
        this.commodityGroup = commodityGroup;
    }

    // Getters and setters
    public String getHsCode(){
        return hsCode;   
    }

    public void setHsCode(String hsCode){
        this.hsCode = hsCode;
    }

    public String getHsName(){
        return hsName;
    }

    public void setHsName(String hsName){
        this.hsName = hsName; 
    }

    public String getCommodityGroup(){
        return commodityGroup;
    }

    public void setCommodityGroup(String commodityGroup){
        this.commodityGroup = commodityGroup;
    }
}
