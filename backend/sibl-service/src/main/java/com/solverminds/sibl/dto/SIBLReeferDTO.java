package com.solverminds.sibl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.enums.TemperatureUnit;
import com.svm.ecom.common.enums.YesNoFlag;
import com.solverminds.sibl.util.DtoUtil;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Reefer settings nested under container when equipment is Reefer and NOT Non-Operating Reefer
 * Condition: equipmentType = "R" AND nor != "Y"
 */
@Schema(name = "SIBLReeferDTO", description = "Reefer container settings (required when container is reefer and not NOR)")
public class SIBLReeferDTO {

    @JsonProperty("setTemp")
    @Schema(description = "Set temperature", example = "5")
    private Double tempSet;

    @JsonProperty("tempUnit")
    @Schema(description = "Temperature unit: C (Celsius), F (Fahrenheit)")
    private TemperatureUnit tempSetUnit;

    @JsonProperty("minTemp")
    @Schema(description = "Minimum temperature", example = "2")
    private Double tempMin;

    @JsonProperty("maxTemp")
    @Schema(description = "Maximum temperature", example = "8")
    private Double tempMax;

    @Schema(description = "Ventilation setting (e.g. OPEN or numeric)", example = "OPEN")
    private String ventilation;

    @Schema(
        example = "0",
        description = "1 = Ventilation value present, 0 = Ventilation value not present."
    )
    private Integer ventSet;

    @Schema(description = "Humidity percentage", example = "60")
    private Double humidity;

    @Schema(description = "Humidity flag: Y (Yes), N (No)")
    private YesNoFlag humidityFlag;

    @JsonProperty("o2Value")
    @Schema(description = "O2 level percentage", example = "21")
    private Double o2Level;

    @JsonProperty("co2Value")
    @Schema(description = "CO2 level percentage", example = "5")
    private Double co2Level;

    @Schema(
        example = "O",
        description = "Drains setting. Allowed values: O = Open, C = Closed",
        allowableValues = {"O", "C"}
    )
    private String drains;


    // ===== Constructors =====
    public SIBLReeferDTO() {}

    // ===== Getters & Setters (temperature: convertEmptyToNull for SI view response) =====
    public Double getTempSet() { return DtoUtil.nullIfEmpty(tempSet); }
    public void setTempSet(Double tempSet) { this.tempSet = tempSet; }

    public TemperatureUnit getTempSetUnit() { return tempSetUnit; }
    public void setTempSetUnit(TemperatureUnit tempSetUnit) { this.tempSetUnit = tempSetUnit; }

    public Double getTempMin() { return DtoUtil.nullIfEmpty(tempMin); }
    public void setTempMin(Double tempMin) { this.tempMin = tempMin; }

    public Double getTempMax() { return DtoUtil.nullIfEmpty(tempMax); }
    public void setTempMax(Double tempMax) { this.tempMax = tempMax; }

    public String getVentilation() { return DtoUtil.emptyIfNull(ventilation); }
    public void setVentilation(String ventilation) { this.ventilation = ventilation; }

    public Integer getVentSet() { return DtoUtil.emptyIntIfNull(ventSet); }
    public void setVentSet(Integer ventSet) { this.ventSet = ventSet; }

    public Double getHumidity() { return DtoUtil.emptyDoubleIfNull(humidity); }
    public void setHumidity(Double humidity) { this.humidity = humidity; }

    public YesNoFlag getHumidityFlag() { return humidityFlag; }
    public void setHumidityFlag(YesNoFlag humidityFlag) { this.humidityFlag = humidityFlag; }

    public String getDrains() { return DtoUtil.emptyIfNull(drains); }
    public void setDrains(String drains) { this.drains = drains; }

    public Double getO2Level() { return DtoUtil.emptyDoubleIfNull(o2Level); }
    public void setO2Level(Double o2Level) { this.o2Level = o2Level; }

    public Double getCo2Level() { return DtoUtil.emptyDoubleIfNull(co2Level); }
    public void setCo2Level(Double co2Level) { this.co2Level = co2Level; }
}
