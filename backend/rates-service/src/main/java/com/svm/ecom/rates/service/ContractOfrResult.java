package com.svm.ecom.rates.service;

import com.svm.ecom.rates.dto.estimate.RateEstimateCharge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Result of Booking Contract OFR engine ({@code AutoRateFlag = "O"}).
 * <ul>
 *   <li>{@link #chargesForResponse} — grid lines (non-LP × qty; LP once with equipmentType=LP)</li>
 *   <li>{@link #ofrUnitByEquipmentType} — unit OFR per equipment code for Booking {@code getOFRByRef}
 *       (one amount per eqp type, not multiplied by qty)</li>
 * </ul>
 */
public final class ContractOfrResult {

    private final List<RateEstimateCharge> chargesForResponse;
    /** Equipment code (e.g. 20DC) → unit OFR amount for formula seed. */
    private final Map<String, Double> ofrUnitByEquipmentType;

    public ContractOfrResult(List<RateEstimateCharge> chargesForResponse,
                             Map<String, Double> ofrUnitByEquipmentType) {
        this.chargesForResponse = chargesForResponse != null
                ? chargesForResponse : new ArrayList<RateEstimateCharge>();
        this.ofrUnitByEquipmentType = ofrUnitByEquipmentType != null
                ? ofrUnitByEquipmentType : new LinkedHashMap<String, Double>();
    }

    public static ContractOfrResult empty() {
        return new ContractOfrResult(
                Collections.<RateEstimateCharge>emptyList(),
                Collections.<String, Double>emptyMap());
    }

    public List<RateEstimateCharge> getChargesForResponse() {
        return chargesForResponse;
    }

    /**
     * Unit OFR keyed by equipment type ({@code 20DC}, {@code 40OT}, …) for per-eqp
     * {@code getOFRByRef} seeding. Empty when no OFR was resolved.
     */
    public Map<String, Double> getOfrUnitByEquipmentType() {
        return ofrUnitByEquipmentType;
    }

    public boolean isEmpty() {
        return chargesForResponse.isEmpty();
    }
}
