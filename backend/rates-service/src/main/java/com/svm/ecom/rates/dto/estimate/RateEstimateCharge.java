package com.svm.ecom.rates.dto.estimate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RateEstimateCharge", description = "Calculated surcharge line")
public class RateEstimateCharge {

    @Schema(description = "Charge code returned by surcharge engine", example = "BAF")
    private String chargeCode;

    @Schema(description = "Charge display name from Commercial charges.chargename", example = "Bunker Adjustment Factor")
    private String chargeName;

    // example quoted so swagger-core keeps a String ("150.00") instead of coercing to number 150.0
    @Schema(description = "Charge amount in billing currency, 2 decimal places", example = "\"150.00\"")
    private String amount;

    @Schema(description = "Billing currency code from surcharge engine (falls back to USD)", example = "USD")
    private String currency;

    @Schema(description = "Charge amount in USD, 2 decimal places (HALF_UP)", example = "\"251.40\"")
    private String amountUSD;

    @Schema(description = "Equipment / charge basis type from surcharge engine (eqpType)", example = "40HR")
    private String equipmentType;

    public String getChargeCode() { return chargeCode; }
    public void setChargeCode(String chargeCode) { this.chargeCode = chargeCode; }
    public String getChargeName() { return chargeName; }
    public void setChargeName(String chargeName) { this.chargeName = chargeName; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getAmountUSD() { return amountUSD; }
    public void setAmountUSD(String amountUSD) { this.amountUSD = amountUSD; }
    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }
}
