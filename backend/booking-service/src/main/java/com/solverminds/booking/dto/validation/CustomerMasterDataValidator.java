package com.solverminds.booking.dto.validation;

import com.solverminds.booking.dto.CustomerDTO;
import com.solverminds.booking.service.MasterDataCacheService;
import com.solverminds.booking.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator to check if customer code and country code exist in the master data cache.
 * Validates: customerCode (against customermaster), countryId (against country)
 */
public class CustomerMasterDataValidator implements ConstraintValidator<ValidCustomerMasterData, CustomerDTO> {

    private static final Logger logger = LoggerFactory.getLogger(CustomerMasterDataValidator.class);

    @Override
    public boolean isValid(CustomerDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true;

        MasterDataCacheService cacheService = getCacheService();
        if (cacheService == null || !cacheService.isInitialized()) {
            logger.warn("MasterDataCacheService not available, skipping customer master data validation");
            return true; // Skip validation if cache not available
        }

        boolean ok = true;
        ctx.disableDefaultConstraintViolation();

        // Validate customerCode against customermaster
        if (dto.getCustomerCode() != null && !dto.getCustomerCode().trim().isEmpty()) {
            if (!cacheService.isValidCustomerCode(dto.getCustomerCode())) {
                ctx.buildConstraintViolationWithTemplate("Invalid customer code: " + dto.getCustomerCode())
                   .addPropertyNode("customerCode")
                   .addConstraintViolation();
                ok = false;
                logger.debug("Invalid customer code: {}", dto.getCustomerCode());
            }
        }

        // Validate countryId against country
        if (dto.getCountryId() != null && !dto.getCountryId().trim().isEmpty()) {
            if (!cacheService.isValidCountryCode(dto.getCountryId())) {
                ctx.buildConstraintViolationWithTemplate("Invalid country code: " + dto.getCountryId())
                   .addPropertyNode("countryId")
                   .addConstraintViolation();
                ok = false;
                logger.debug("Invalid country code: {}", dto.getCountryId());
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

