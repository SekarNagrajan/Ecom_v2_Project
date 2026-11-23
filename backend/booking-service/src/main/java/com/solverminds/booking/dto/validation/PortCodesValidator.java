package com.solverminds.booking.dto.validation;

import com.solverminds.booking.dto.BookingRequestDTO;
import com.solverminds.booking.service.MasterDataCacheService;
import com.solverminds.booking.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator to check if port codes exist in the master data cache.
 * Validates: originPortId, finalPortId, loadPortId, dischargePortId
 */
public class PortCodesValidator implements ConstraintValidator<ValidPortCodes, BookingRequestDTO> {

    private static final Logger logger = LoggerFactory.getLogger(PortCodesValidator.class);

    @Override
    public boolean isValid(BookingRequestDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true;

        MasterDataCacheService cacheService = getCacheService();
        if (cacheService == null || !cacheService.isInitialized()) {
            logger.warn("MasterDataCacheService not available, skipping port validation");
            return true; // Skip validation if cache not available
        }

        boolean ok = true;
        ctx.disableDefaultConstraintViolation();

        // Validate originPortId
        if (dto.getOriginPortId() != null && !dto.getOriginPortId().trim().isEmpty()) {
            if (!cacheService.isValidPortCode(dto.getOriginPortId())) {
                ctx.buildConstraintViolationWithTemplate("Invalid origin port code: " + dto.getOriginPortId())
                   .addPropertyNode("originPortId")
                   .addConstraintViolation();
                ok = false;
                logger.debug("Invalid origin port code: {}", dto.getOriginPortId());
            }
        }

        // Validate finalPortId
        if (dto.getFinalPortId() != null && !dto.getFinalPortId().trim().isEmpty()) {
            if (!cacheService.isValidPortCode(dto.getFinalPortId())) {
                ctx.buildConstraintViolationWithTemplate("Invalid final port code: " + dto.getFinalPortId())
                   .addPropertyNode("finalPortId")
                   .addConstraintViolation();
                ok = false;
                logger.debug("Invalid final port code: {}", dto.getFinalPortId());
            }
        }

        // Validate loadPortId
        if (dto.getLoadPortId() != null && !dto.getLoadPortId().trim().isEmpty()) {
            if (!cacheService.isValidPortCode(dto.getLoadPortId())) {
                ctx.buildConstraintViolationWithTemplate("Invalid load port code: " + dto.getLoadPortId())
                   .addPropertyNode("loadPortId")
                   .addConstraintViolation();
                ok = false;
                logger.debug("Invalid load port code: {}", dto.getLoadPortId());
            }
        }

        // Validate dischargePortId
        if (dto.getDischargePortId() != null && !dto.getDischargePortId().trim().isEmpty()) {
            if (!cacheService.isValidPortCode(dto.getDischargePortId())) {
                ctx.buildConstraintViolationWithTemplate("Invalid discharge port code: " + dto.getDischargePortId())
                   .addPropertyNode("dischargePortId")
                   .addConstraintViolation();
                ok = false;
                logger.debug("Invalid discharge port code: {}", dto.getDischargePortId());
            }
        }

        // Validate routeId against active routes in routedetail table
        if (dto.getRouteId() != null) {
            if (!cacheService.isValidRouteId(dto.getRouteId())) {
                ctx.buildConstraintViolationWithTemplate("Invalid route ID: " + dto.getRouteId() + " - route not found or not active")
                   .addPropertyNode("routeId")
                   .addConstraintViolation();
                ok = false;
                logger.debug("Invalid route ID: {}", dto.getRouteId());
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

