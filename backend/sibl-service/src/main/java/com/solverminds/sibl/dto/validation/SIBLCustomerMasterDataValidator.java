package com.solverminds.sibl.dto.validation;

import com.solverminds.sibl.dto.SIBLCustomerDTO;
import com.svm.ecom.common.master.cache.CommonMasterDataCacheService;
import com.solverminds.sibl.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates customer code and country code against master data cache (same as Booking CustomerMasterDataValidator).
 */
public class SIBLCustomerMasterDataValidator implements ConstraintValidator<ValidSIBLCustomerMasterData, SIBLCustomerDTO> {

    private static final Logger logger = LoggerFactory.getLogger(SIBLCustomerMasterDataValidator.class);

    @Override
    public boolean isValid(SIBLCustomerDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true;

        CommonMasterDataCacheService cacheService = getCacheService();
        if (cacheService == null || !cacheService.isInitialized()) {
            logger.warn("CommonMasterDataCacheService not available, skipping customer master data validation");
            return true;
        }

        boolean ok = true;
        ctx.disableDefaultConstraintViolation();
        String custType = dto.getCustomerType() != null ? dto.getCustomerType().getValue() : "UNKNOWN";

        if (notEmpty(dto.getCustomerCode()) && !cacheService.isValidCustomerCode(dto.getCustomerCode())) {
            addViolation(ctx, "customerCode", "Invalid customer code: " + dto.getCustomerCode() + " for customer type " + custType);
            ok = false;
        }
        if (notEmpty(dto.getCountryId()) && !cacheService.isValidCountryCode(dto.getCountryId())) {
            addViolation(ctx, "countryId", "Invalid country code: " + dto.getCountryId() + " for customer type " + custType);
            ok = false;
        }
        return ok;
    }

    private static boolean notEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static void addViolation(ConstraintValidatorContext ctx, String property, String msg) {
        ctx.buildConstraintViolationWithTemplate(msg).addPropertyNode(property).addConstraintViolation();
    }

    private CommonMasterDataCacheService getCacheService() {
        try {
            return SpringContextHolder.getBean(CommonMasterDataCacheService.class);
        } catch (Exception e) {
            logger.error("Error getting CommonMasterDataCacheService", e);
            return null;
        }
    }
}
