package com.solverminds.sibl.dto.validation;

import com.solverminds.sibl.dto.SIBLRequestDTO;
import com.svm.ecom.common.master.cache.CommonMasterDataCacheService;
import com.solverminds.sibl.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator to check if port codes exist in the master data cache (same as Booking PortCodesValidator).
 * Validates: originPortId, finalPortId, loadPortId, dischargePortId
 */
public class SIBLPortCodesValidator implements ConstraintValidator<ValidSIBLPortCodes, SIBLRequestDTO> {

    private static final Logger logger = LoggerFactory.getLogger(SIBLPortCodesValidator.class);

    @Override
    public boolean isValid(SIBLRequestDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true;

        CommonMasterDataCacheService cacheService = getCacheService();
        if (cacheService == null || !cacheService.isInitialized()) {
            logger.warn("CommonMasterDataCacheService not available, skipping port validation");
            return true;
        }

        boolean ok = true;
        ctx.disableDefaultConstraintViolation();

        if (notEmpty(dto.getOriginPortId()) && !cacheService.isValidPortCode(dto.getOriginPortId())) {
            addViolation(ctx, "originPortId", "Invalid PLA Code - Port does not exist: " + dto.getOriginPortId());
            ok = false;
        }
        if (notEmpty(dto.getFinalPortId()) && !cacheService.isValidPortCode(dto.getFinalPortId())) {
            addViolation(ctx, "finalPortId", "Invalid PLD Code - Port does not exist: " + dto.getFinalPortId());
            ok = false;
        }
        if (notEmpty(dto.getLoadPortId()) && !cacheService.isValidPortCode(dto.getLoadPortId())) {
            addViolation(ctx, "loadPortId", "Invalid POL Code - Port does not exist: " + dto.getLoadPortId());
            ok = false;
        }
        if (notEmpty(dto.getDischargePortId()) && !cacheService.isValidPortCode(dto.getDischargePortId())) {
            addViolation(ctx, "dischargePortId", "Invalid POD Code - Port does not exist: " + dto.getDischargePortId());
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
