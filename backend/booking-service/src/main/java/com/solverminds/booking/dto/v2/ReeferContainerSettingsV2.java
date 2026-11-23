package com.solverminds.booking.dto.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.enums.TemperatureUnit;
import com.svm.ecom.common.enums.YesNoFlag;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;

/**
 * Settings of an operating reefer container for V2 API.
 */
@Schema(name = "ReeferContainerSettings", description = "Settings of an operating reefer container")
public class ReeferContainerSettingsV2 {

    @NotNull
    @Schema(description = "Reefer temperature", example = "1.5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double temperature;

    @Schema(description = "Temperature unit: C (Celsius) or F (Fahrenheit)", example = "C")
    private TemperatureUnit temperatureUnit;

    @JsonProperty("minTemp")
    @Schema(description = "Minimum temperature", example = "5")
    private Double minTemp;

    @JsonProperty("maxTemp")
    @Schema(description = "Maximum temperature", example = "10")
    private Double maxTemp;

    @Schema(description = "Ventilation setting (e.g. OPEN or numeric percentage)", example = "20")
    private String ventilation;

    @Schema(description = "Ventilation numeric value (response only; not persisted)", example = "25.0")
    private Double ventSet;

    @Schema(description = "Humidity percentage", example = "65")
    private Double humidity;

    @Schema(description = "Humidity flag: Y (Yes), N (No)", example = "Y")
    private YesNoFlag humidityFlag;

    @Schema(description = "Drains setting (e.g. O, C)", example = "O")
    private String drains;

    @JsonProperty("o2Value")
    @Schema(description = "O2 level percentage", example = "21")
    private Double o2Value;

    @JsonProperty("co2Value")
    @Schema(description = "CO2 level percentage", example = "5")
    private Double co2Value;

    public ReeferContainerSettingsV2() {
    }

    public ReeferContainerSettingsV2(Double temperature) {
        this.temperature = temperature;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public TemperatureUnit getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(TemperatureUnit temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
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

    public Double getVentSet() {
        return ventSet;
    }

    public void setVentSet(Double ventSet) {
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
