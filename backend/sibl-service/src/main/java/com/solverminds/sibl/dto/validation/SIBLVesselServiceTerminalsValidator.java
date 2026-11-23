package com.solverminds.sibl.dto.validation;

import com.solverminds.sibl.dto.SIBLRequestDTO;
import com.svm.ecom.common.master.cache.CommonMasterDataCacheService;
import com.solverminds.sibl.util.SpringContextHolder;
import com.svm.ecom.common.enums.TransportMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * When serviceType is M or F: vessel must exist in vessel master; serviceCode must exist in service master;
 * polTerminal and podTerminal must exist in terminal master (same as Booking RoutePortsValidator).
 */
public class SIBLVesselServiceTerminalsValidator implements ConstraintValidator<ValidSIBLVesselServiceTerminals, SIBLRequestDTO> {

    private static final Logger logger = LoggerFactory.getLogger(SIBLVesselServiceTerminalsValidator.class);

    private static boolean isMainlineOrFeeder(TransportMode serviceType) {
        return serviceType == TransportMode.M || serviceType == TransportMode.F;
    }

    @Override
    public boolean isValid(SIBLRequestDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true;

        CommonMasterDataCacheService cacheService = getCacheService();
        if (cacheService == null || !cacheService.isInitialized()) {
            logger.warn("CommonMasterDataCacheService not available, skipping vessel/service/terminal validation");
            return true;
        }

        boolean ok = true;
        ctx.disableDefaultConstraintViolation();

        TransportMode serviceType = dto.getServiceType();
        if (!isMainlineOrFeeder(serviceType)) return true;

        String vesselCode = dto.getVesselCode();
        if (notEmpty(vesselCode) && !cacheService.isValidVesselCode(vesselCode)) {
            addViolation(ctx, "vesselCode", "Invalid vessel code: " + vesselCode);
            ok = false;
        }

        String serviceCode = dto.getServiceCode();
        if (notEmpty(serviceCode) && !cacheService.isValidServiceCode(serviceCode)) {
            addViolation(ctx, "serviceCode", "Invalid service code: " + serviceCode);
            ok = false;
        }

        String polTerminal = dto.getPolTerminal();
        if (notEmpty(polTerminal) && !cacheService.isValidTerminalCode(polTerminal)) {
            addViolation(ctx, "polTerminal", "Invalid POL terminal code: " + polTerminal);
            ok = false;
        }

        String podTerminal = dto.getPodTerminal();
        if (notEmpty(podTerminal) && !cacheService.isValidTerminalCode(podTerminal)) {
            addViolation(ctx, "podTerminal", "Invalid POD terminal code: " + podTerminal);
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
