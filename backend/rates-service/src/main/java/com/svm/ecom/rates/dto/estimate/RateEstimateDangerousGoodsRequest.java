package com.svm.ecom.rates.dto.estimate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RateEstimateDangerousGoodsRequest", description = "Dangerous goods information (conditional)")
public class RateEstimateDangerousGoodsRequest {

    @Schema(description = "UN number", example = "1203")
    private String unNo;

    @Schema(description = "IMO class code used as DGClass by surcharge engine (defaults to 99 when empty)",
            example = "3")
    private String classCode;

    public String getUnNo() { return unNo; }
    public void setUnNo(String unNo) { this.unNo = unNo; }
    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }
}
