package com.solverminds.sibl.dto;

import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Reefer settings nested under container")
public class ReeferSettingDTO {
    @NotNull private Integer cargoNo;
    @NotBlank private String containerNo;
    @NotNull private Integer itemNo;
    @NotNull private Integer setTemp;

    public ReeferSettingDTO() {}

    public Integer getCargoNo() { return cargoNo; }
    public void setCargoNo(Integer cargoNo) { this.cargoNo = cargoNo; }
    public String getContainerNo() { return containerNo; }
    public void setContainerNo(String containerNo) { this.containerNo = containerNo; }
    public Integer getItemNo() { return itemNo; }
    public void setItemNo(Integer itemNo) { this.itemNo = itemNo; }
    public Integer getSetTemp() { return setTemp; }
    public void setSetTemp(Integer setTemp) { this.setTemp = setTemp; }
}
