package com.solverminds.sibl.dto.validation;

import com.solverminds.sibl.dto.SIBLCommodityDTO;
import com.svm.ecom.common.master.cache.CommonMasterDataCacheService;
import com.solverminds.sibl.util.SpringContextHolder;
import com.svm.ecom.common.enums.YesNoFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates commodity group, HS code, package type against master; when hazardous=Y requires dangerousGoods (same as Booking CommodityMasterDataValidator + SI hazardous rule).
 */
public class SIBLCommodityMasterDataValidator implements ConstraintValidator<ValidSIBLCommodityMasterData, SIBLCommodityDTO> {

    private static final Logger logger = LoggerFactory.getLogger(SIBLCommodityMasterDataValidator.class);

    private static final String HS_GROUP_MISMATCH = "hsCode is not applicable for the given commodity group code";

    @Override
    public boolean isValid(SIBLCommodityDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true;

        boolean ok = true;
        ctx.disableDefaultConstraintViolation();

        CommonMasterDataCacheService cacheService = getCacheService();
        if (cacheService != null && cacheService.isInitialized()) {
            String cg = dto.getCommodityGroup();
            boolean cgOk = true;
            if (cg != null && !cg.trim().isEmpty() && !cacheService.isValidCommodityCode(cg.trim())) {
                addViolation(ctx, "commodityGroup", "Invalid commodity group: " + cg);
                ok = false;
                cgOk = false;
            }
            boolean hsOk = true;
            if (notEmpty(dto.getHsCode()) && !cacheService.isValidCommodityCode(dto.getHsCode())) {
                addViolation(ctx, "hsCode", "Invalid HS code: " + dto.getHsCode());
                ok = false;
                hsOk = false;
            }
            if (cgOk && hsOk && notEmpty(cg) && notEmpty(dto.getHsCode())) {
                if (!cacheService.isHsCodeApplicableForCommodityGroup(dto.getHsCode(), cg)) {
                    addViolation(ctx, "hsCode", HS_GROUP_MISMATCH);
                    ok = false;
                }
            }
            if (notEmpty(dto.getPackageType()) && !cacheService.isValidPackageCode(dto.getPackageType())) {
                addViolation(ctx, "packageType", "Invalid package type: " + dto.getPackageType());
                ok = false;
            }
        }

        if (dto.getHazardous() == YesNoFlag.Y && dto.getDangerousGoods() == null) {
            addViolation(ctx, "dangerousGoods", "SIBLDangerousGoodsDTO is required when hazardous=Y");
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
