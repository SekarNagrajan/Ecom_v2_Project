package com.solverminds.booking.dto.validation;

import com.solverminds.booking.dto.BookingRequestDTO;
import com.solverminds.booking.dto.CommodityDTO;
import com.solverminds.booking.dto.ContainerDTO;

import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Shared duplicate-value collection for V1 booking {@code containerNo} / {@code cargoNo} validation.
 * Each duplicate is reported with a property path ({@code containers[i].containerNo} or
 * {@code commodities[i].containerNo}) so error responses expose the correct field and value.
 */
final class BookingDuplicateFieldCollector {

    static final String CONTAINERS = "containers";
    static final String COMMODITIES = "commodities";
    static final String FIELD_CONTAINER_NO = "containerNo";
    static final String FIELD_CARGO_NO = "cargoNo";

    private BookingDuplicateFieldCollector() {
    }

    static List<IndexedFieldDuplicate> findContainerNoDuplicates(BookingRequestDTO booking) {
        List<IndexedFieldDuplicate> duplicates = new ArrayList<>();
        if (booking == null) {
            return duplicates;
        }
        collectDuplicateContainerNosInContainers(booking.getContainers(), duplicates);
        return duplicates;
    }

    static List<IndexedFieldDuplicate> findCargoNoDuplicates(BookingRequestDTO booking) {
        List<IndexedFieldDuplicate> duplicates = new ArrayList<>();
        if (booking == null) {
            return duplicates;
        }
        collectDuplicateCargoNosInContainers(booking.getContainers(), duplicates);
        return duplicates;
    }

    /**
     * Finds duplicate commodity lines in {@code commodities[]} when the composite key
     * {@code containerNo|commodityGroup|hsCode|packageType} (trim + uppercase) matches.
     */
    static List<IndexedFieldDuplicate> findCommodityCombinationDuplicates(BookingRequestDTO booking) {
        List<IndexedFieldDuplicate> duplicates = new ArrayList<>();
        if (booking == null) {
            return duplicates;
        }
        collectDuplicateCommodityCombinations(booking.getCommodities(), duplicates);
        return duplicates;
    }

    static String commodityCombinationKey(String containerNo, String commodityGroup, String hsCode, String packageType) {
        return normalizeField(containerNo) + "|"
                + normalizeField(commodityGroup) + "|"
                + normalizeField(hsCode) + "|"
                + normalizeField(packageType);
    }

    static void addFieldViolations(ConstraintValidatorContext context,
                                   String messagePrefix,
                                   List<IndexedFieldDuplicate> duplicates) {
        context.disableDefaultConstraintViolation();
        for (IndexedFieldDuplicate duplicate : duplicates) {
            String message = messagePrefix + ": " + duplicate.getDisplayValue();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(duplicate.getListProperty())
                    .addPropertyNode(null).inIterable().atIndex(duplicate.getIndex())
                    .addPropertyNode(duplicate.getFieldName())
                    .addConstraintViolation();
        }
    }

    private static void collectDuplicateContainerNosInContainers(List<ContainerDTO> containers,
                                                                   List<IndexedFieldDuplicate> duplicates) {
        if (containers == null || containers.isEmpty()) {
            return;
        }

        Set<String> seen = new HashSet<>();
        Set<String> reported = new LinkedHashSet<>();
        for (int i = 0; i < containers.size(); i++) {
            ContainerDTO container = containers.get(i);
            if (container == null) {
                continue;
            }
            String normalized = normalizeContainerNo(container.getContainerNo());
            if (normalized == null) {
                continue;
            }
            if (!seen.add(normalized) && reported.add(normalized)) {
                duplicates.add(new IndexedFieldDuplicate(
                        CONTAINERS, i, FIELD_CONTAINER_NO, displayContainerNo(container.getContainerNo())));
            }
        }
    }

    private static void collectDuplicateCargoNosInContainers(List<ContainerDTO> containers,
                                                             List<IndexedFieldDuplicate> duplicates) {
        if (containers == null || containers.isEmpty()) {
            return;
        }

        Set<Integer> seen = new HashSet<>();
        Set<Integer> reported = new LinkedHashSet<>();
        for (int i = 0; i < containers.size(); i++) {
            ContainerDTO container = containers.get(i);
            if (container == null) {
                continue;
            }
            Integer cargoNo = container.getCargoNo();
            if (cargoNo == null) {
                continue;
            }
            if (!seen.add(cargoNo) && reported.add(cargoNo)) {
                duplicates.add(new IndexedFieldDuplicate(
                        CONTAINERS, i, FIELD_CARGO_NO, String.valueOf(cargoNo)));
            }
        }
    }

    private static void collectDuplicateCommodityCombinations(List<CommodityDTO> commodities,
                                                              List<IndexedFieldDuplicate> duplicates) {
        if (commodities == null || commodities.isEmpty()) {
            return;
        }

        Set<String> seen = new HashSet<>();
        Set<String> reported = new LinkedHashSet<>();
        for (int i = 0; i < commodities.size(); i++) {
            CommodityDTO commodity = commodities.get(i);
            if (commodity == null) {
                continue;
            }
            String key = commodityCombinationKey(
                    commodity.getContainerNo(),
                    commodity.getCommodityGroup(),
                    commodity.getHsCode(),
                    commodity.getPackageType());
            if (!seen.add(key) && reported.add(key)) {
                duplicates.add(new IndexedFieldDuplicate(
                        COMMODITIES, i, FIELD_CONTAINER_NO, displayContainerNo(commodity.getContainerNo())));
            }
        }
    }

    private static String normalizeContainerNo(String containerNo) {
        if (containerNo == null || containerNo.trim().isEmpty()) {
            return null;
        }
        return containerNo.trim().toUpperCase();
    }

    private static String normalizeField(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private static String displayContainerNo(String containerNo) {
        return containerNo != null ? containerNo.trim() : null;
    }

    static final class IndexedFieldDuplicate {
        private final String listProperty;
        private final int index;
        private final String fieldName;
        private final String displayValue;

        IndexedFieldDuplicate(String listProperty, int index, String fieldName, String displayValue) {
            this.listProperty = listProperty;
            this.index = index;
            this.fieldName = fieldName;
            this.displayValue = displayValue;
        }

        String getListProperty() {
            return listProperty;
        }

        int getIndex() {
            return index;
        }

        String getFieldName() {
            return fieldName;
        }

        String getDisplayValue() {
            return displayValue;
        }
    }
}
