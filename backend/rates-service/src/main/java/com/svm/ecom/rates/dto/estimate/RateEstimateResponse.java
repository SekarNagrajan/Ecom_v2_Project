package com.svm.ecom.rates.dto.estimate;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(name = "RateEstimateResponse", description = "Auto Rate surcharge estimate response")
public class RateEstimateResponse {

    @Schema(description = "Calculated surcharge charges")
    private List<RateEstimateCharge> charges = new ArrayList<>();

    public RateEstimateResponse() {
    }

    public RateEstimateResponse(List<RateEstimateCharge> charges) {
        this.charges = charges != null ? charges : new ArrayList<>();
    }

    public List<RateEstimateCharge> getCharges() { return charges; }
    public void setCharges(List<RateEstimateCharge> charges) {
        this.charges = charges != null ? charges : new ArrayList<>();
    }
}
