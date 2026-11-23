package com.svm.ecom.rates.mapper;

import com.svm.ecom.rates.dto.estimate.RateEstimateCharge;
import com.svm.ecom.rates.dto.estimate.RateEstimateResponse;
import com.svm.ecom.rates.service.RateEstimateLookupCacheService;
import net.ecom.vo.ContractSurchargeCalculationResult;
import net.ecom.vo.ContractSurchargeResultVo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Maps surcharge engine result to Auto Rate estimate API response.
 * <p>
 * A-flag (CommonSurcharge) lines are emitted <em>one row per eqpid bucket</em> — no
 * {@code chargeCode|equipmentType} amount merge.
 * Charge display names come from cached Commercial {@code charges.chargename}.
 * <p>
 * O-flag contract lines still dedupe by {@code chargeCode|equipmentType} (LP by code only).
 */
@Component
public class RateEstimateResponseMapper {

    private static final String CURRENCY_USD = "USD";
    /** API money display — 2 dp (Booking-style currency; does not change engine math). */
    private static final int MONEY_SCALE = 2;

    private final RateEstimateLookupCacheService lookupCache;

    public RateEstimateResponseMapper(RateEstimateLookupCacheService lookupCache) {
        this.lookupCache = lookupCache;
    }

    /**
     * Flatten engine result: one response charge per (eqpid, chargeCode).
     * Does <strong>not</strong> sum amounts across equipment lines.
     */
    public RateEstimateResponse toResponse(ContractSurchargeCalculationResult result) {
        RateEstimateResponse response = new RateEstimateResponse();
        if (result == null || result.getChargesByPairId() == null || result.getChargesByPairId().isEmpty()) {
            return response;
        }

        List<RateEstimateCharge> charges = new ArrayList<>();
        for (Map.Entry<String, Map<String, ContractSurchargeResultVo>> pairEntry
                : result.getChargesByPairId().entrySet()) {
            Map<String, ContractSurchargeResultVo> chargeMap = pairEntry.getValue();
            if (chargeMap == null || chargeMap.isEmpty()) {
                continue;
            }
            for (ContractSurchargeResultVo charge : chargeMap.values()) {
                if (charge == null || charge.getChargeCode() == null
                        || charge.getChargeCode().trim().isEmpty()) {
                    continue;
                }
                charges.add(toCharge(charge));
            }
        }

        response.setCharges(charges);
        return response;
    }

    /**
     * Combines contract OFR-family charges ({@code AutoRateFlag=O}) with surcharge charges ({@code A}).
     * <ul>
     *   <li>Contract lines first (OFR, THL, …) when not also from surcharge — deduped by code|eqp</li>
     *   <li>Surcharge lines appended as-is (no re-merge — preserves per-eqp BAF/EIF/EPF)</li>
     * </ul>
     */
    public RateEstimateResponse mergeContractCharges(RateEstimateResponse response,
                                                     List<RateEstimateCharge> contractCharges) {
        if (response == null) {
            response = new RateEstimateResponse();
        }
        if (contractCharges == null || contractCharges.isEmpty()) {
            applyCachedNames(response.getCharges());
            applyMoneyScale(response.getCharges());
            return response;
        }

        Set<String> surchargeCodes = new HashSet<>();
        if (response.getCharges() != null) {
            for (RateEstimateCharge charge : response.getCharges()) {
                if (charge != null && !isBlank(charge.getChargeCode())) {
                    surchargeCodes.add(charge.getChargeCode().trim().toUpperCase());
                }
            }
        }

        List<RateEstimateCharge> out = new ArrayList<>();

        Map<String, RateEstimateCharge> contractByKey = new LinkedHashMap<>();
        for (RateEstimateCharge charge : contractCharges) {
            if (charge == null || isBlank(charge.getChargeCode())) {
                continue;
            }
            String code = charge.getChargeCode().trim();
            if (surchargeCodes.contains(code.toUpperCase())) {
                continue;
            }
            mergeContractOnly(contractByKey, charge);
        }
        out.addAll(contractByKey.values());

        if (response.getCharges() != null) {
            for (RateEstimateCharge charge : response.getCharges()) {
                if (charge == null || isBlank(charge.getChargeCode())) {
                    continue;
                }
                out.add(copyCharge(charge));
            }
        }

        applyCachedNames(out);
        applyMoneyScale(out);
        response.setCharges(out);
        return response;
    }

    /** Fill blank / "-" names from {@code charges} cache (code → chargename). */
    private void applyCachedNames(List<RateEstimateCharge> charges) {
        if (charges == null || charges.isEmpty() || lookupCache == null) {
            return;
        }
        for (RateEstimateCharge charge : charges) {
            if (charge == null || isBlank(charge.getChargeCode())) {
                continue;
            }
            charge.setChargeName(lookupCache.resolveChargeName(
                    charge.getChargeCode(), charge.getChargeName()));
        }
    }

    /** Round currency fields to 2 decimal places for API output only. */
    private void applyMoneyScale(List<RateEstimateCharge> charges) {
        if (charges == null || charges.isEmpty()) {
            return;
        }
        for (RateEstimateCharge charge : charges) {
            if (charge == null) {
                continue;
            }
            charge.setAmount(formatMoney(charge.getAmount()));
            charge.setAmountUSD(formatMoney(charge.getAmountUSD()));
        }
    }

    private void mergeContractOnly(Map<String, RateEstimateCharge> byKey, RateEstimateCharge source) {
        String code = source.getChargeCode().trim();
        String eqp = source.getEquipmentType() == null ? "" : source.getEquipmentType().trim();
        String key = chargeKey(code, eqp);
        RateEstimateCharge existing = byKey.get(key);
        if (existing == null) {
            byKey.put(key, copyCharge(source));
            return;
        }
        existing.setAmount(sumAmount(existing.getAmount(), source.getAmount()));
        existing.setAmountUSD(sumAmount(existing.getAmountUSD(), source.getAmountUSD()));
        if (isBlank(existing.getCurrency()) && !isBlank(source.getCurrency())) {
            existing.setCurrency(source.getCurrency().trim());
        }
        if (isBlank(existing.getEquipmentType()) && !isBlank(eqp)) {
            existing.setEquipmentType(eqp);
        }
        existing.setChargeName(lookupCache.resolveChargeName(
                code, preferName(existing.getChargeName(), source.getChargeName())));
    }

    private static String preferName(String a, String b) {
        if (a != null && !a.trim().isEmpty() && !"-".equals(a.trim())) {
            return a.trim();
        }
        if (b != null && !b.trim().isEmpty() && !"-".equals(b.trim())) {
            return b.trim();
        }
        return a != null ? a : b;
    }

    private RateEstimateCharge copyCharge(RateEstimateCharge source) {
        RateEstimateCharge copy = new RateEstimateCharge();
        String code = source.getChargeCode() == null ? "" : source.getChargeCode().trim();
        copy.setChargeCode(code);
        copy.setChargeName(lookupCache.resolveChargeName(code, source.getChargeName()));
        copy.setAmount(formatMoney(source.getAmount()));
        copy.setCurrency(isBlank(source.getCurrency()) ? CURRENCY_USD : source.getCurrency().trim());
        copy.setAmountUSD(formatMoney(source.getAmountUSD()));
        copy.setEquipmentType(source.getEquipmentType() == null ? "" : source.getEquipmentType().trim());
        return copy;
    }

    private static String chargeKey(String code, String equipmentType) {
        if (equipmentType == null || equipmentType.trim().isEmpty()) {
            return code;
        }
        if ("LP".equalsIgnoreCase(equipmentType.trim())) {
            return code;
        }
        return code + "|" + equipmentType.trim();
    }

    private RateEstimateCharge toCharge(ContractSurchargeResultVo source) {
        RateEstimateCharge charge = new RateEstimateCharge();
        String code = source.getChargeCode().trim();
        charge.setChargeCode(code);
        charge.setChargeName(lookupCache.resolveChargeName(code, null));
        charge.setAmount(formatMoney(resolveLocalAmount(source)));
        charge.setCurrency(resolveCurrency(source));
        charge.setAmountUSD(formatMoney(source.getAmountUsd()));
        charge.setEquipmentType(source.getEqpType() == null ? "" : source.getEqpType().trim());
        return charge;
    }

    private String resolveLocalAmount(ContractSurchargeResultVo source) {
        if (!isBlank(source.getAmount())) {
            return source.getAmount().trim();
        }
        return nullToZero(source.getAmountUsd());
    }

    private String resolveCurrency(ContractSurchargeResultVo source) {
        if (!isBlank(source.getCurrency())) {
            return source.getCurrency().trim();
        }
        return CURRENCY_USD;
    }

    private String sumAmount(String left, String right) {
        return formatMoney(parseAmount(left).add(parseAmount(right)).toPlainString());
    }

    /**
     * Currency display: always 2 decimal places (HALF_UP).
     * Example: {@code 251.3966522216797} → {@code 251.40}.
     */
    private String formatMoney(String value) {
        return parseAmount(value).setScale(MONEY_SCALE, RoundingMode.HALF_UP).toPlainString();
    }

    private BigDecimal parseAmount(String value) {
        if (isBlank(value)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private String nullToZero(String value) {
        return isBlank(value) ? "0" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
