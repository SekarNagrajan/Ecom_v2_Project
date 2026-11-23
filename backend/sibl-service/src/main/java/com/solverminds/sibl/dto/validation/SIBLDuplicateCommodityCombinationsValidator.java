package com.solverminds.sibl.dto.validation;

import com.solverminds.sibl.dto.SIBLCommodityDTO;
import com.solverminds.sibl.dto.SIBLRequestDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Rejects duplicate commodity lines when the composite key
 * {@code containerNo|commodityGroup|hsCode|packageType} matches (aligned with booking-service).
 */
public class SIBLDuplicateCommodityCombinationsValidator
        implements ConstraintValidator<ValidSIBLDuplicateCommodityCombinations, SIBLRequestDTO> {

    static final String MESSAGE_PREFIX = "Duplicate commodity found. Combination already exists";

    @Override
    public boolean isValid(SIBLRequestDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) {
            return true;
        }
        List<SIBLCommodityDTO> commodities = dto.getCommodities();
        if (commodities == null || commodities.isEmpty()) {
            return true;
        }

        Set<String> seen = new HashSet<>();
        Set<String> reported = new LinkedHashSet<>();
        boolean valid = true;
        ctx.disableDefaultConstraintViolation();

        for (int i = 0; i < commodities.size(); i++) {
            SIBLCommodityDTO commodity = commodities.get(i);
            if (commodity == null) {
                continue;
            }
            String key = commodityCombinationKey(
                    commodity.getContainerNo(),
                    commodity.getCommodityGroup(),
                    commodity.getHsCode(),
                    commodity.getPackageType());
            if (!seen.add(key) && reported.add(key)) {
                String displayContainerNo = displayContainerNo(commodity.getContainerNo());
                ctx.buildConstraintViolationWithTemplate(MESSAGE_PREFIX + ": " + displayContainerNo)
                        .addPropertyNode("commodities")
                        .addPropertyNode(null).inIterable().atIndex(i)
                        .addPropertyNode("containerNo")
                        .addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }

    static String commodityCombinationKey(String containerNo, String commodityGroup, String hsCode, String packageType) {
        return normalizeField(containerNo) + "|"
                + normalizeField(commodityGroup) + "|"
                + normalizeField(hsCode) + "|"
                + normalizeField(packageType);
    }

    private static String normalizeField(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private static String displayContainerNo(String containerNo) {
        return containerNo != null ? containerNo.trim() : null;
    }
}
