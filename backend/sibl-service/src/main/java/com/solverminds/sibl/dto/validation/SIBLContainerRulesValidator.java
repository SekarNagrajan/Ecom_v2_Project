package com.solverminds.sibl.dto.validation;

import com.solverminds.sibl.dto.SIBLContainerDTO;
import com.solverminds.sibl.repo.ContainerProfileRepository;
import com.svm.ecom.common.master.cache.CommonMasterDataCacheService;
import com.solverminds.sibl.util.SpringContextHolder;
import com.svm.ecom.common.enums.EquipmentStatus;
import com.svm.ecom.common.enums.EquipmentType;
import com.svm.ecom.common.enums.YesNoFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Container validation rules (aligned with attached rule order and booking-service):
 * Rule 1: containerNo required for selected container(s).
 * Rule 3: containerNo pattern – valid container number (4 letters + 7 digits) or internal ID (6–20 chars), same as booking-service.
 * Rule 4: containerNo + equipmentType must exist in containerprofile.
 * Rule 7: Reefer settings required when reefer=Y (or equipment type R) and nor!=Y.
 * DG is at commodity level only (no container-level dgDetails).
 * Rule 9: oogDimensions required when oogFlag=Y.
 * Rule 10: equipmentSize must exist in equipment master.
 * Rule 11: cargo + tare must not exceed container capacity; sets tare/gross on DTO (same as booking-service).
 * Rule 12: equipmentStatus must be F or M when provided.
 * (Rule 5: container not in another booking – SIValidationService. Rule 6: unique within SI – ValidSIBLDuplicateContainers.)
 */
public class SIBLContainerRulesValidator implements ConstraintValidator<ValidSIBLContainerRules, SIBLContainerDTO> {

    private static final Logger logger = LoggerFactory.getLogger(SIBLContainerRulesValidator.class);

    /** Same as booking-service ContainerDTO: 4 uppercase + 7 digits (11 chars) OR internal ID 6–20 chars. */
    private static final Pattern CONTAINER_NO_PATTERN = Pattern.compile("^[A-Z]{4}[0-9]{7}$|^[A-Za-z0-9_-]{6,20}$");
    private static final String CONTAINER_NO_MESSAGE = "containerNo must be a valid container number (e.g., ABCD1234567) or an internal ID (6-20 chars)";

    @Override
    public boolean isValid(SIBLContainerDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true;

        boolean ok = true;

        // Rule 1: containerNo required — enforced by @NotBlank on SIBLContainerDTO ("containerNo is required")
        if (!notEmpty(dto.getContainerNo())) {
            return true;
        }
        String cn = dto.getContainerNo().trim();

        // Rule 3: containerNo pattern – same as booking-service (4 letters + 7 digits OR internal ID 6–20 chars)
        if (!CONTAINER_NO_PATTERN.matcher(cn).matches()) {
            addViolation(ctx, "containerNo", CONTAINER_NO_MESSAGE);
            ok = false;
        }

        // Rule 4: containerNo + equipment type/size must exist in containerprofile.
        // DB column equipmenttype stores full size code (e.g. RE20, 20DC), not single char D/R.
        if (ok) {
            String eqpForProfile = getEquipmentTypeOrSizeForProfile(dto);
            if (eqpForProfile != null) {
                ContainerProfileRepository repo = getContainerProfileRepository();
                if (repo != null && !repo.existsByEquipmentTypeAndContainerNo(eqpForProfile, cn)) {
                    addViolation(ctx, "containerNo", "Invalid Container No format. Please enter a valid container number.");
                    ok = false;
                }
            }
        }

        // Rule 7: Reefer settings required when reefer=Y or equipmentType R, and nor!=Y
        boolean isReefer = dto.getReefer() == YesNoFlag.Y || (dto.getEquipmentType() != null && dto.getEquipmentType() == EquipmentType.R);
        boolean isNor = dto.getNor() == YesNoFlag.Y;
        if (isReefer && !isNor && dto.getReeferSettings() == null) {
            addViolation(ctx, "reeferSettings", "Reefer settings are required when reefer=Y (or equipment type R) and nor!=Y");
            ok = false;
        }

        // Rule 9: oogDimensions required when oogFlag=Y
        if (dto.getOogFlag() == YesNoFlag.Y && dto.getOogDimensions() == null) {
            addViolation(ctx, "oogDimensions", "SIBLOogDTO is required when oogFlag=Y");
            ok = false;
        }

        // Rule 10: equipmentSize must exist in equipment master; Rule 11: weight capacity
        if (notEmpty(dto.getEquipmentSize())) {
            CommonMasterDataCacheService cacheService = getCacheService();
            if (cacheService != null && cacheService.isInitialized()) {
                if (!cacheService.isValidEquipmentSize(dto.getEquipmentSize())) {
                    addViolation(ctx, "equipmentSize", "Invalid equipment size: " + dto.getEquipmentSize() + " - Equipment must exist in equipment master");
                    ok = false;
                } else {
                    int cargoWt = dto.getCargoWeight() != null ? dto.getCargoWeight() : 0;
                    float tareWt = dto.getTareWeight() != null ? dto.getTareWeight().floatValue() : 0f;
                    float grossWeightKg = cargoWt + tareWt;
                    dto.setGrossweight((int) grossWeightKg);

                    Float maxGross = getMaxGrossWeightFromCache(dto.getEquipmentSize());
                    if (maxGross != null && maxGross > 0 && grossWeightKg > maxGross) {
                        addViolation(ctx, "cargoWeight", "Weight " + (int) grossWeightKg + " kg exceeds container capacity (" + maxGross.intValue() + " kg for " + dto.getEquipmentSize() + ")");
                        ok = false;
                    }
                }
            }
        }

        // Rule 12: equipmentStatus must be F or M (@NotNull enforces presence)
        if (dto.getEquipmentStatus() != null && dto.getEquipmentStatus() != EquipmentStatus.F && dto.getEquipmentStatus() != EquipmentStatus.M) {
            addViolation(ctx, "equipmentStatus", "Equipment status must be F (Full/Laden) or M (Empty)");
            ok = false;
        }

        return ok;
    }

    /** Value for containerprofile lookup: DB stores full equipment code (e.g. RE20, 20DC), not D/R. Use equipmentSize when set. */
    private static String getEquipmentTypeOrSizeForProfile(SIBLContainerDTO dto) {
        if (dto.getEquipmentSize() != null && !dto.getEquipmentSize().trim().isEmpty()) {
            return dto.getEquipmentSize().trim();
        }
        if (dto.getEquipmentType() != null) {
            return dto.getEquipmentType().getValue();
        }
        return null;
    }

    private static boolean notEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static void addViolation(ConstraintValidatorContext ctx, String property, String msg) {
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(msg).addPropertyNode(property).addConstraintViolation();
    }

    private Float getMaxGrossWeightFromCache(String equipmentSize) {
        CommonMasterDataCacheService cacheService = getCacheService();
        if (cacheService != null && cacheService.isInitialized()) {
            return cacheService.getEquipmentGrossWeight(equipmentSize);
        }
        return null;
    }

    private CommonMasterDataCacheService getCacheService() {
        try {
            return SpringContextHolder.getBean(CommonMasterDataCacheService.class);
        } catch (Exception e) {
            logger.error("Error getting CommonMasterDataCacheService", e);
            return null;
        }
    }

    private ContainerProfileRepository getContainerProfileRepository() {
        try {
            return SpringContextHolder.getBean(ContainerProfileRepository.class);
        } catch (Exception e) {
            logger.error("Error getting ContainerProfileRepository", e);
            return null;
        }
    }
}
