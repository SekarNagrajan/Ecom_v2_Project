package com.solverminds.booking.util;

import com.solverminds.booking.dto.BookingRequestDTO;
import com.solverminds.booking.dto.CommodityDTO;
import com.solverminds.booking.dto.ContainerDTO;
import com.solverminds.booking.entity.EcomBookingCommodity;
import com.svm.ecom.common.enums.WeightUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * When commodities are present for a container line, aligns container {@code cargoWeight} with SI behaviour:
 * {@code cargoWeight = SUM(itemWeight)} keyed by {@code cargoNo|containerNo}, normalized to kg.
 */
public final class BookingCargoWeightNormalizer {

    private BookingCargoWeightNormalizer() {}

    /**
     * Mutates {@code dto} containers in place when matching commodities exist with at least one non-null {@code itemWeight}.
     */
    public static void apply(BookingRequestDTO dto) {
        if (dto == null) {
            return;
        }
        List<ContainerDTO> containers = dto.getContainers();
        List<CommodityDTO> commodities = dto.getCommodities();
        if (containers == null || containers.isEmpty() || commodities == null || commodities.isEmpty()) {
            return;
        }
        Map<String, Integer> derivedKg = computeDerivedCargoWeightByContainer(commodities);
        if (derivedKg.isEmpty()) {
            return;
        }
        for (ContainerDTO container : containers) {
            if (container == null) {
                continue;
            }
            String key = containerKey(container.getCargoNo(), container.getContainerNo());
            Integer sumKg = derivedKg.get(key);
            if (sumKg == null) {
                continue;
            }
            container.setCargoWeight(sumKg);
            container.setWeightUnit(WeightUnit.K);
            Float tare = container.getTareWeight();
            if (tare != null) {
                container.setGrossWeight(tare + sumKg);
            }
        }
    }

    /**
     * Compute derived cargoWeight per container: SUM(itemWeight) of all commodities for that container, normalized to kg.
     * Key format: {@code cargoNo|containerNo} (same as SI create).
     */
    public static Map<String, Integer> computeDerivedCargoWeightByContainer(List<CommodityDTO> commodities) {
        Map<String, Integer> sumByKey = new HashMap<>();
        if (commodities == null || commodities.isEmpty()) {
            return sumByKey;
        }
        for (CommodityDTO c : commodities) {
            if (c == null || c.getItemWeight() == null) {
                continue;
            }
            String key = containerKey(c.getCargoNo(), c.getContainerNo());
            int itemWeightKg = toKg(c.getItemWeight(), c.getItemWeightUnit());
            sumByKey.merge(key, itemWeightKg, Integer::sum);
        }
        return sumByKey;
    }

    /** Key format aligned with {@code SiCreateService}: cargoNo|containerNo. */
    public static String containerKey(Integer cargoNo, String containerNo) {
        Integer cn = cargoNo != null ? cargoNo : 1;
        String no = containerNo != null ? containerNo.trim() : "";
        return cn + "|" + no;
    }

    /** Convert commodity itemWeight to kg: T (tonnes) * 1000, K (kilos) as-is. */
    public static int toKg(Float itemWeight, WeightUnit unit) {
        if (itemWeight == null) {
            return 0;
        }
        int w = Math.round(itemWeight);
        if (unit == WeightUnit.T) {
            return w * 1000;
        }
        return w;
    }

    /**
     * Maps commodity-level weight to {@code ecombooking_commodity.itemWeight} / {@code itemWeightUnit},
     * aligned with {@code editempbooking_commodity} ({@code itemWeight} preferred, then {@code nettwt} as kg).
     */
    public static void applyCommodityItemWeight(EcomBookingCommodity row, CommodityDTO dto) {
        if (row == null || dto == null) {
            return;
        }
        if (dto.getItemWeight() != null) {
            row.setItemWeight(dto.getItemWeight());
            row.setItemWeightUnit(dto.getItemWeightUnit() != null
                    ? dto.getItemWeightUnit().getValue() : WeightUnit.K.getValue());
        } else if (dto.getNettwt() != null) {
            row.setItemWeight(dto.getNettwt().floatValue());
            row.setItemWeightUnit(WeightUnit.K.getValue());
        }
    }

    /**
     * Resolve per-line weight for persistence to {@code ecombooking_commodity.nettwt} (kg).
     * Prefers {@code itemWeight} + unit conversion; falls back to request {@code nettwt}.
     */
    public static Double resolveLineWeightKg(CommodityDTO c) {
        if (c == null) {
            return null;
        }
        if (c.getItemWeight() != null) {
            return (double) toKg(c.getItemWeight(), c.getItemWeightUnit());
        }
        return c.getNettwt();
    }
}
