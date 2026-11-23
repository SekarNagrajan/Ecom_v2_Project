package com.solverminds.sibl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.enums.LengthUnit;
import com.solverminds.sibl.util.DtoUtil;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OOG (Out of Gauge) dimensions nested under container when oog = "Y" or under commodity for item-level OOG.
 */
@Schema(name = "SIBLOogDTO", description = "Out of Gauge dimensions (required when container/commodity is OOG)")
public class SIBLOogDTO {

    @JsonProperty("itemLeft")
    @Schema(description = "Left overhang in ft/m", example = "0.2")
    private Double leftFt;

    @JsonProperty("itemRight")
    @Schema(description = "Right overhang in ft/m", example = "0.2")
    private Double rightFt;

    @JsonProperty("itemForward")
    @Schema(description = "Forward overhang in ft/m", example = "0.5")
    private Double forward;

    @JsonProperty("itemAft")
    @Schema(description = "Aft overhang in ft/m", example = "0.4")
    private Double aft;

    @JsonProperty("itemTop")
    @Schema(description = "Top overhang in ft/m", example = "0.3")
    private Double topFt;

    @Schema(description = "Dimension unit: F (Feet), M (Meters)")
    private LengthUnit unit;

    // ===== Constructors =====
    public SIBLOogDTO() {}

    // ===== Getters & Setters (null empty/0 for SI view response) =====
    public Double getLeftFt() { return DtoUtil.emptyDoubleIfNull(leftFt); }
    public void setLeftFt(Double leftFt) { this.leftFt = leftFt; }

    public Double getRightFt() { return DtoUtil.emptyDoubleIfNull(rightFt); }
    public void setRightFt(Double rightFt) { this.rightFt = rightFt; }

    public Double getForward() { return DtoUtil.emptyDoubleIfNull(forward); }
    public void setForward(Double forward) { this.forward = forward; }

    public Double getAft() { return DtoUtil.emptyDoubleIfNull(aft); }
    public void setAft(Double aft) { this.aft = aft; }

    public Double getTopFt() { return DtoUtil.emptyDoubleIfNull(topFt); }
    public void setTopFt(Double topFt) { this.topFt = topFt; }

    public LengthUnit getUnit() { return unit; }
    public void setUnit(LengthUnit unit) { this.unit = unit; }
}
