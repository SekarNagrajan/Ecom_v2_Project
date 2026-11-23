package com.solverminds.booking.dto.validation;

import com.solverminds.booking.dto.BookingRequestDTO;
import com.solverminds.booking.dto.ContainerDTO;
import com.solverminds.booking.dto.CommodityDTO;
import com.solverminds.booking.dto.v2.DangerousGoodsInfoV2;
import com.solverminds.booking.service.MasterDataCacheService;
import com.solverminds.booking.util.SpringContextHolder;
import com.svm.ecom.common.enums.YesNoFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validator that ensures container type matches cargo requirements:
 * 1. Each container's cargoNo must have corresponding commodity with same cargoNo
 * 2. Each container's containerNo must have corresponding commodity with same containerNo
 *    (only when containerNo is provided - it's optional for new bookings)
 * 3. When dgFlag is 'Y', validates dangerous goods UN numbers and class codes against master data
 * 
 * Note: containerNo is optional for new bookings (auto-generated) but required for amendments.
 * When containerNo is not provided, matching is done by cargoNo only.
 */
public class ContainerCargoValidator implements ConstraintValidator<ValidContainerCargo, BookingRequestDTO> {

    private static final Logger logger = LoggerFactory.getLogger(ContainerCargoValidator.class);

    @Override
    public void initialize(ValidContainerCargo constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(BookingRequestDTO booking, ConstraintValidatorContext context) {
        if (booking == null) {
            return true;
        }

        List<ContainerDTO> containers = booking.getContainers();
        List<CommodityDTO> commodities = booking.getCommodities();

        // Let @NotEmpty handle empty lists
        if (containers == null || containers.isEmpty() || commodities == null || commodities.isEmpty()) {
            return true;
        }

        boolean isValid = true;

        // Get all cargo numbers from commodities
        Set<Integer> commodityCargoNos = commodities.stream()
                .filter(c -> c.getCargoNo() != null)
                .map(CommodityDTO::getCargoNo)
                .collect(Collectors.toSet());

        // Get all container numbers from commodities (only non-empty ones)
        Set<String> commodityContainerNos = commodities.stream()
                .filter(c -> c.getContainerNo() != null && !c.getContainerNo().trim().isEmpty())
                .map(CommodityDTO::getContainerNo)
                .collect(Collectors.toSet());

        for (ContainerDTO container : containers) {
            if (container == null) continue;

            // Rule 1: Container's cargoNo must have at least one corresponding commodity
            if (container.getCargoNo() != null && !commodityCargoNos.contains(container.getCargoNo())) {
                addError(context, "Container with cargoNo " + container.getCargoNo() + 
                        " has no matching commodity. Container type must match cargo requirements.");
                isValid = false;
            }

            // Rule 2: Container's containerNo must exist in commodities
            // ONLY validate when containerNo is provided (optional for new bookings)
            // When containerNo is null/empty, containers will be expanded and matched by cargoNo in EDI export
            if (container.getContainerNo() != null && !container.getContainerNo().trim().isEmpty()) {
                // Only check if commodities also have containerNo values
                if (!commodityContainerNos.isEmpty() && !commodityContainerNos.contains(container.getContainerNo())) {
                    addError(context, "Container " + container.getContainerNo() + 
                            " has no matching commodity. Container type must match cargo requirements.");
                    isValid = false;
                }
            }

            // Rule 3: When dgFlag is 'Y', validate dangerous goods UN numbers and class codes
            if (container.getDgFlag() == YesNoFlag.Y) {
                isValid = validateDangerousGoodsForContainer(container, commodities, context) && isValid;
            }
        }

        return isValid;
    }

    /**
     * Validates dangerous goods for a container when dgFlag is 'Y'.
     * Finds commodities matching the container and validates their dangerous goods.
     */
    private boolean validateDangerousGoodsForContainer(ContainerDTO container, 
                                                        List<CommodityDTO> commodities,
                                                        ConstraintValidatorContext context) {
        MasterDataCacheService cacheService = getCacheService();
        if (cacheService == null || !cacheService.isInitialized()) {
            logger.warn("MasterDataCacheService not available, skipping DG validation");
            return true; // Skip validation if cache not available
        }

        boolean isValid = true;
        String containerNo = container.getContainerNo();
        Integer cargoNo = container.getCargoNo();

        // Find commodities matching this container (by containerNo or cargoNo)
        for (int commodityIdx = 0; commodityIdx < commodities.size(); commodityIdx++) {
            CommodityDTO commodity = commodities.get(commodityIdx);
            if (commodity == null) continue;

            // Match by containerNo or cargoNo
            boolean matchesByContainerNo = containerNo != null && 
                containerNo.equals(commodity.getContainerNo());
            boolean matchesByCargoNo = cargoNo != null && 
                cargoNo.equals(commodity.getCargoNo());

            if (matchesByContainerNo || matchesByCargoNo) {
                DangerousGoodsInfoV2 dgi = commodity.getDangerousGoods();
                if (dgi != null) {
                    isValid = validateDangerousGoodsInfo(dgi, commodityIdx, cacheService, context) && isValid;
                }
            }
        }

        return isValid;
    }

    /**
     * Validates {@link DangerousGoodsInfoV2} unNo and classCode against master data.
     */
    private boolean validateDangerousGoodsInfo(DangerousGoodsInfoV2 dgi,
                                             int commodityIdx,
                                             MasterDataCacheService cacheService,
                                             ConstraintValidatorContext context) {
        boolean isValid = true;
        String unNo = dgi.getUnNo();
        String classCode = dgi.getClassCode();

        if (unNo != null && !unNo.trim().isEmpty()) {
            if (!cacheService.isValidUnNo(unNo)) {
                addDgInfoFieldError(context, commodityIdx, "unNo",
                    "Invalid UN number: " + unNo);
                isValid = false;
                logger.debug("Invalid UN number: {}", unNo);
            }
        }

        if (classCode != null && !classCode.trim().isEmpty()) {
            if (!cacheService.isValidClassCode(classCode)) {
                addDgInfoFieldError(context, commodityIdx, "classCode",
                    "Invalid class code: " + classCode);
                isValid = false;
                logger.debug("Invalid class code: {}", classCode);
            }
        }

        return isValid;
    }

    private MasterDataCacheService getCacheService() {
        try {
            return SpringContextHolder.getBean(MasterDataCacheService.class);
        } catch (Exception e) {
            logger.error("Error getting MasterDataCacheService", e);
            return null;
        }
    }

    private void addError(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    /**
     * Path: {@code commodities[i].dangerousGoods.fieldName}
     */
    private void addDgInfoFieldError(ConstraintValidatorContext context,
                                     int commodityIdx,
                                     String fieldName, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
               .addPropertyNode("commodities")
               .addPropertyNode(null).inIterable().atIndex(commodityIdx)
               .addPropertyNode("dangerousGoods")
               .addPropertyNode(fieldName)
               .addConstraintViolation();
    }
}
