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
@Schema(description = "DG master data")
public class DGDoc {
    @Schema(description = "UNNO", example = "0039", required = true)
    private String unNo;

    @Schema(description = "class", example = "1.2G", required = true)
    private String classCode;  

    @Schema(description = "Shipping Name", example = "BOMBS, PHOTO-FLASH", required = true)
    private String shippingName;

    // Constructors
    
    public DGDoc(String unNo, String classCode, String shippingName) {
            this.unNo = unNo;
            this.classCode = classCode;
            this.shippingName = shippingName;
    }

    // Getters and setters
    public String getUnNo(){
        return unNo;      
    }

    public void setUnNo(String unNo){
        this.unNo = unNo;
    }

    public String getClassCode(){
        return classCode;
    }
    
    public void setClassCode(String classCode){
        this.classCode = classCode;
    }

    public String getShippingName(){
        return shippingName;
    }

    public void setShippingName(String shippingName){
        this.shippingName = shippingName;
    }
}
  