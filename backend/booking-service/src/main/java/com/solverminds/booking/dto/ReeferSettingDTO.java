package com.solverminds.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.svm.ecom.common.enums.TemperatureUnit;
import com.svm.ecom.common.enums.YesNoFlag;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

/**
 * Reefer (refrigerated container) settings nested under a container.
 * Linkage keys (cargoNo, containerNo, itemNo) are populated server-side from the parent container.
 */
@Schema(description = "Reefer settings for a container item")
public class ReeferSettingDTO {

    @JsonIgnore
    @Schema(hidden = true)
    private Integer cargoNo;

    @JsonIgnore
    @Schema(hidden = true)
    private String containerNo;

    @JsonIgnore
    @Schema(hidden = true)
    private Integer itemNo;

    @NotNull(message = "Reefer cargo requires temperature settings")
    @Schema(description = "Set temperature for reefer container", example = "8", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer setTemp;

    @Schema(description = "Temperature unit - C (Celsius) or F (Fahrenheit)", example = "C")
    private TemperatureUnit tempUnit;

    @Schema(description = "Minimum temperature", example = "5")
    private Double minTemp;

    @Schema(description = "Maximum temperature", example = "10")
    private Double maxTemp;

    @Schema(description = "Ventilation setting (e.g. OPEN or numeric percentage)", example = "20")
    private String ventilation;

    @Schema(example = "0", description = "1 = Ventilation value present, 0 = Ventilation value not present.")
    private Integer ventSet;

    @Schema(description = "Humidity percentage", example = "65")
    private Double humidity;

    @Schema(description = "Humidity flag: Y (Yes), N (No)", example = "Y")
    private YesNoFlag humidityFlag;

    @Schema(description = "Drains setting (e.g. O, C)", example = "O")
    private String drains;

    @Schema(description = "O2 level percentage", example = "21")
    private Double o2Value;

    @Schema(description = "CO2 level percentage", example = "5")
    private Double co2Value;

    public ReeferSettingDTO() {
    }

    @JsonIgnore
    @Schema(hidden = true)
    public Integer getCargoNo() {
        return cargoNo;
    }

    @Schema(hidden = true)
    public void setCargoNo(Integer cargoNo) {
        this.cargoNo = cargoNo;
    }

    @JsonIgnore
    @Schema(hidden = true)
    public String getContainerNo() {
        return containerNo;
    }

    @Schema(hidden = true)
    public void setContainerNo(String containerNo) {
        this.containerNo = containerNo;
    }

    @JsonIgnore
    @Schema(hidden = true)
    public Integer getItemNo() {
        return itemNo;
    }

    @Schema(hidden = true)
    public void setItemNo(Integer itemNo) {
        this.itemNo = itemNo;
    }

    public Integer getSetTemp() {
        return setTemp;
    }

    public void setSetTemp(Integer setTemp) {
        this.setTemp = setTemp;
    }

    public TemperatureUnit getTempUnit() {
        return tempUnit;
    }

    public void setTempUnit(TemperatureUnit tempUnit) {
        this.tempUnit = tempUnit;
    }

    public Double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(Double minTemp) {
        this.minTemp = minTemp;
    }

    public Double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(Double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getVentilation() {
        return ventilation;
    }

    public void setVentilation(String ventilation) {
        this.ventilation = ventilation;
    }

    public Integer getVentSet() {
        return ventSet;
    }

    public void setVentSet(Integer ventSet) {
        this.ventSet = ventSet;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public YesNoFlag getHumidityFlag() {
        return humidityFlag;
    }

    public void setHumidityFlag(YesNoFlag humidityFlag) {
        this.humidityFlag = humidityFlag;
    }

    public String getDrains() {
        return drains;
    }

    public void setDrains(String drains) {
        this.drains = drains;
    }

    public Double getO2Value() {
        return o2Value;
    }

    public void setO2Value(Double o2Value) {
        this.o2Value = o2Value;
    }

    public Double getCo2Value() {
        return co2Value;
    }

    public void setCo2Value(Double co2Value) {
        this.co2Value = co2Value;
    }
}
