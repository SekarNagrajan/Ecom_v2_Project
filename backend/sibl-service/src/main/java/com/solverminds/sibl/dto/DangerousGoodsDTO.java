package com.solverminds.sibl.dto;

import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dangerous goods line")
public class DangerousGoodsDTO {
    @NotNull private Integer cargoNo;
    @NotBlank private String containerNo;
    @NotNull private Integer itemNo;
    private String marinePollutant;   // Y/N
    private String unNo;
    private Integer flashPt;
    private String flashPtUnit;       // C/F
    private String classCode;         // e.g., "9"

    public DangerousGoodsDTO() {}

    public Integer getCargoNo() { return cargoNo; }
    public void setCargoNo(Integer cargoNo) { this.cargoNo = cargoNo; }
    public String getContainerNo() { return containerNo; }
    public void setContainerNo(String containerNo) { this.containerNo = containerNo; }
    public Integer getItemNo() { return itemNo; }
    public void setItemNo(Integer itemNo) { this.itemNo = itemNo; }
    public String getMarinePollutant() { return marinePollutant; }
    public void setMarinePollutant(String marinePollutant) { this.marinePollutant = marinePollutant; }
    public String getUnNo() { return unNo; }
    public void setUnNo(String unNo) { this.unNo = unNo; }
    public Integer getFlashPt() { return flashPt; }
    public void setFlashPt(Integer flashPt) { this.flashPt = flashPt; }
    public String getFlashPtUnit() { return flashPtUnit; }
    public void setFlashPtUnit(String flashPtUnit) { this.flashPtUnit = flashPtUnit; }
    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }
}
