package com.solverminds.booking.dto.validation;

import com.solverminds.booking.dto.RouteDTO;
import com.svm.ecom.common.enums.TransportMode;
import com.solverminds.booking.service.MasterDataCacheService;
import com.solverminds.booking.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator to check if route port codes and master data exist in the cache.
 * Validates: loadPortId, dischargePortId in RouteDTO
 * For serviceType M (Mainline) or F (Feeder): serviceCode, vesselCode, loadTerminal, dischargeTerminal
 */
public class RoutePortsValidator implements ConstraintValidator<ValidRoutePorts, RouteDTO> {

    private static final Logger logger = LoggerFactory.getLogger(RoutePortsValidator.class);

    @Override
    public boolean isValid(RouteDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true;

        MasterDataCacheService cacheService = getCacheService();
        if (cacheService == null || !cacheService.isInitialized()) {
            logger.warn("MasterDataCacheService not available, skipping route port validation");
            return true; // Skip validation if cache not available
        }

        boolean ok = true;
        ctx.disableDefaultConstraintViolation();

        // Validate loadPortId
        if (dto.getLoadPortId() != null && !dto.getLoadPortId().trim().isEmpty()) {
            if (!cacheService.isValidPortCode(dto.getLoadPortId())) {
                ctx.buildConstraintViolationWithTemplate("Invalid route load port code: " + dto.getLoadPortId())
                   .addPropertyNode("loadPortId")
                   .addConstraintViolation();
                ok = false;
                logger.debug("Invalid route load port code: {}", dto.getLoadPortId());
            }
        }

        // Validate dischargePortId
        if (dto.getDischargePortId() != null && !dto.getDischargePortId().trim().isEmpty()) {
            if (!cacheService.isValidPortCode(dto.getDischargePortId())) {
                ctx.buildConstraintViolationWithTemplate("Invalid route discharge port code: " + dto.getDischargePortId())
                   .addPropertyNode("dischargePortId")
                   .addConstraintViolation();
                ok = false;
                logger.debug("Invalid route discharge port code: {}", dto.getDischargePortId());
            }
        }

        // Additional validations for Mainline (M) or Feeder (F) service types
        if (isMainlineOrFeeder(dto.getServiceType())) {
            // Validate serviceCode against service master
            if (dto.getServiceCode() != null && !dto.getServiceCode().trim().isEmpty()) {
                if (!cacheService.isValidServiceCode(dto.getServiceCode())) {
                    ctx.buildConstraintViolationWithTemplate("Invalid route service code: " + dto.getServiceCode())
                       .addPropertyNode("serviceCode")
                       .addConstraintViolation();
                    ok = false;
                    logger.debug("Invalid route service code: {}", dto.getServiceCode());
                }
            }

            // Validate vesselCode against vessel master
            if (dto.getVesselCode() != null && !dto.getVesselCode().trim().isEmpty()) {
                if (!cacheService.isValidVesselCode(dto.getVesselCode())) {
                    ctx.buildConstraintViolationWithTemplate("Invalid route vessel code: " + dto.getVesselCode())
                       .addPropertyNode("vesselCode")
                       .addConstraintViolation();
                    ok = false;
                    logger.debug("Invalid route vessel code: {}", dto.getVesselCode());
                }
            }

            // Validate loadTerminal against terminal master
            if (dto.getLoadTerminal() != null && !dto.getLoadTerminal().trim().isEmpty()) {
                if (!cacheService.isValidTerminalCode(dto.getLoadTerminal())) {
                    ctx.buildConstraintViolationWithTemplate("Invalid route load terminal: " + dto.getLoadTerminal())
                       .addPropertyNode("loadTerminal")
                       .addConstraintViolation();
                    ok = false;
                    logger.debug("Invalid route load terminal: {}", dto.getLoadTerminal());
                }
            }

            // Validate dischargeTerminal against terminal master
            if (dto.getDischargeTerminal() != null && !dto.getDischargeTerminal().trim().isEmpty()) {
                if (!cacheService.isValidTerminalCode(dto.getDischargeTerminal())) {
                    ctx.buildConstraintViolationWithTemplate("Invalid route discharge terminal: " + dto.getDischargeTerminal())
                       .addPropertyNode("dischargeTerminal")
                       .addConstraintViolation();
                    ok = false;
                    logger.debug("Invalid route discharge terminal: {}", dto.getDischargeTerminal());
                }
            }
        }

        return ok;
    }

    /**
     * Check if service type is Mainline (M) or Feeder (F)
     */
    private boolean isMainlineOrFeeder(TransportMode serviceType) {
        return serviceType == TransportMode.M || serviceType == TransportMode.F;
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

