package com.svm.ecom.rates.dto.estimate;

import com.svm.ecom.common.enums.TemperatureUnit;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "RateEstimateReeferSettings", description = "Reefer temperature settings (conditional when container type is R)")
public class RateEstimateReeferSettings {

    @Schema(description = "Set temperature", example = "-18.00")
    private BigDecimal temperature;

    @Schema(description = "Temperature unit", example = "C")
    private TemperatureUnit temperatureUnit;

    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
    public TemperatureUnit getTemperatureUnit() { return temperatureUnit; }
    public void setTemperatureUnit(TemperatureUnit temperatureUnit) { this.temperatureUnit = temperatureUnit; }
}
