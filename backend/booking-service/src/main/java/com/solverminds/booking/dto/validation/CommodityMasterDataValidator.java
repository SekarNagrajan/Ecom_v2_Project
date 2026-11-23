package com.solverminds.booking.dto.validation;

import com.solverminds.booking.dto.CommodityDTO;
import com.solverminds.booking.service.MasterDataCacheService;
import com.solverminds.booking.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator to check if commodity-related codes exist in the master data cache.
 * Validates:
 * - commodityGroup (against commodity table)
 * - hsCode (against commodity table, only if provided)
 * - packageType (against package table, only if provided)
 */
public class CommodityMasterDataValidator implements ConstraintValidator<ValidCommodityMasterData, CommodityDTO> {

    private static final Logger logger = LoggerFactory.getLogger(CommodityMasterDataValidator.class);

    private static final String HS_GROUP_MISMATCH = "hsCode is not applicable for the given commodity group code";

    @Override
    public boolean isValid(CommodityDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true;

        MasterDataCacheService cacheService = getCacheService();
        if (cacheService == null || !cacheService.isInitialized()) {
            logger.warn("MasterDataCacheService not available, skipping commodity master data validation");
            return true; // Skip validation if cache not available
        }

        boolean ok = true;
        ctx.disableDefaultConstraintViolation();

        // Validate commodityGroup (string) against commodity table; preserves leading zeros e.g. "09"
        String commodityGroupStr = dto.getCommodityGroup();
        boolean commodityGroupOk = true;
        if (commodityGroupStr != null && !commodityGroupStr.trim().isEmpty()) {
            if (!cacheService.isValidCommodityCode(commodityGroupStr.trim())) {
                ctx.buildConstraintViolationWithTemplate("Invalid commodity group: " + commodityGroupStr)
                   .addPropertyNode("commodityGroup")
                   .addConstraintViolation();
                ok = false;
                commodityGroupOk = false;
                logger.debug("Invalid commodity group: {}", commodityGroupStr);
            }
        }

        // Validate hsCode against commodity table (only if provided)
        boolean hsCodeOk = true;
        if (dto.getHsCode() != null && !dto.getHsCode().trim().isEmpty()) {
            if (!cacheService.isValidCommodityCode(dto.getHsCode())) {
                ctx.buildConstraintViolationWithTemplate("Invalid HS code: " + dto.getHsCode())
                   .addPropertyNode("hsCode")
                   .addConstraintViolation();
                ok = false;
                hsCodeOk = false;
                logger.debug("Invalid HS code: {}", dto.getHsCode());
            }
        }

        if (commodityGroupOk && hsCodeOk
                && commodityGroupStr != null && !commodityGroupStr.trim().isEmpty()
                && dto.getHsCode() != null && !dto.getHsCode().trim().isEmpty()) {
            if (!cacheService.isHsCodeApplicableForCommodityGroup(dto.getHsCode(), commodityGroupStr)) {
                ctx.buildConstraintViolationWithTemplate(HS_GROUP_MISMATCH)
                        .addPropertyNode("hsCode")
                        .addConstraintViolation();
                ok = false;
            }
        }

        // Validate packageType against package table (only if provided)
        if (dto.getPackageType() != null && !dto.getPackageType().trim().isEmpty()) {
            if (!cacheService.isValidPackageCode(dto.getPackageType())) {
                ctx.buildConstraintViolationWithTemplate("Invalid package type: " + dto.getPackageType())
                   .addPropertyNode("packageType")
                   .addConstraintViolation();
                ok = false;
                logger.debug("Invalid package type: {}", dto.getPackageType());
            }
        }

        return ok;
    }

    private MasterDataCacheService getCacheService() {
        try {
            return SpringContextHolder.getBean(MasterDataCacheService.class);
        } catch (Exception e) {
            logger.error("Error getting MasterDataCacheService", e);
            return null;
        }
    }
}

