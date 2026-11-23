package com.svm.ecom.master.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Commodity type classification.
 * API returns the numeric code (e.g. "23" for General Cargo), same pattern as CustomerType.
 */
@Schema(description = "Commodity type: 11 – HazardousCargo, 13 – LiquidCargo, 14 – TemperatureControlledCargo, 17 – Diplomatic, 18 – Military, 19 – Obnoxious, 20 – OutOfGauge, 22 – FrozenCargo, 23 – GeneralCargo, 6 – Vehicles, 7 – RollOnRollOff, 8 – PalletizedNonContainerized",
        enumAsRef = true, example = "23")
public enum CommodityType {

    @Schema(description = "11 – Hazardous Cargo")
    HazardousCargo("11"),

    @Schema(description = "13 – Liquid Cargo")
    LiquidCargo("13"),

    @Schema(description = "14 – Temperature Controlled Cargo")
    TemperatureControlledCargo("14"),

    @Schema(description = "17 – Diplomatic")
    Diplomatic("17"),

    @Schema(description = "18 – Military")
    Military("18"),

    @Schema(description = "19 – Obnoxious")
    Obnoxious("19"),

    @Schema(description = "20 – Out Of Gauge")
    OutOfGauge("20"),

    @Schema(description = "22 – Frozen Cargo")
    FrozenCargo("22"),

    @Schema(description = "23 – General Cargo")
    GeneralCargo("23"),

    @Schema(description = "6 – Vehicles")
    Vehicles("6"),

    @Schema(description = "7 – Roll On Roll Off")
    RollOnRollOff("7"),

    @Schema(description = "8 – Palletized Non-Containerized")
    PalletizedNonContainerized("8");

    private final String value;

    CommodityType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getDescription() {
        switch (this) {
            case HazardousCargo: return "Hazardous Cargo";
            case LiquidCargo: return "Liquid Cargo";
            case TemperatureControlledCargo: return "Temperature Controlled Cargo";
            case Diplomatic: return "Diplomatic";
            case Military: return "Military";
            case Obnoxious: return "Obnoxious";
            case OutOfGauge: return "Out Of Gauge";
            case FrozenCargo: return "Frozen Cargo";
            case GeneralCargo: return "General Cargo";
            case Vehicles: return "Vehicles";
            case RollOnRollOff: return "Roll On Roll Off";
            case PalletizedNonContainerized: return "Palletized Non-Containerized";
            default: return value;
        }
    }

    /**
     * Create enum from string value (DB code).
     * Handles null by returning null.
     */
    @JsonCreator
    public static CommodityType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (CommodityType type : CommodityType.values()) {
            if (type.value.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid CommodityType value: " + value +
            ". Valid values are: 11, 13, 14, 17, 18, 19, 20, 22, 23, 6, 7, 8");
    }
}
