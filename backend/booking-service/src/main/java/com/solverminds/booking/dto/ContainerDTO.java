package com.solverminds.booking.dto;

import com.svm.ecom.common.enums.YesNoFlag;
import com.svm.ecom.common.enums.EquipmentType;
import com.svm.ecom.common.enums.EquipmentStatus;
import com.svm.ecom.common.enums.WeightUnit;
import com.svm.ecom.common.enums.LengthUnit;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.validation.Payload;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.*;

import com.solverminds.booking.dto.validation.ValidationGroups;

import com.solverminds.booking.util.SpringContextHolder;
import com.solverminds.booking.service.MasterDataCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Container details including equipment specifications and cargo information.
 * Reefer settings are nested within the container when applicable.
 */
@Schema(description = "Container details with optional reefer settings")
@ValidContainerRules
public class ContainerDTO {

    @NotNull
    @Min(1)
    @Schema(description = "Cargo sequence number", example = "1")
    private Integer cargoNo;

    @Schema(description = "Out of gauge flag", example = "N")
    private YesNoFlag oogFlag;

    @NotNull(message = "Field must be provided (non-empty).")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Schema(description = "Number of equipment/containers", example = "1", minimum = "1")
    private Integer equipmentQty;

    @NotNull(message = "equipmentType is required")
    @Schema(description = "Equipment type", example = "D", requiredMode = Schema.RequiredMode.REQUIRED)
    private EquipmentType equipmentType;

    @NotBlank
    @Pattern(
        regexp="^[0-9]{2}[A-Z]{2,3}[A-Z0-9]{0,2}$|^[0-9]{2}[A-Z]{2}$",
        message="equipmentSize must resemble a size/type code (e.g., 20DC, 40DC, 40RH)"
    )
    @Schema(description = "Equipment size/type code (e.g., 20DC, 40DC, 40RH)", example = "40DC")
    private String equipmentSize;

    @Schema(description = "Shipper-owned container flag", example = "N")
    private YesNoFlag socFlag;

    @Schema(description = "Dangerous goods flag", example = "N")
    private YesNoFlag dgFlag;

    @NotNull(message = "equipmentStatus is required")
    @Schema(description = "Equipment status", example = "F", requiredMode = Schema.RequiredMode.REQUIRED)
    private EquipmentStatus equipmentStatus;

    @NotNull(message = "Field must be provided (non-empty).")
    @Min(value = 0, message = "Cargo weight cannot be negative")
    @Schema(description = "Cargo weight (net weight of goods)", example = "10000", minimum = "0")
    private Integer cargoWeight;

    @Min(value = 0, message = "Tare weight cannot be negative")
    @Schema(description = "Tare weight (weight of empty container)", example = "2300", minimum = "0")
    private Float tareWeight;

    @Min(value = 0, message = "Gross weight cannot be negative")
    @Schema(description = "Gross weight (cargo weight + tare weight)", example = "12300", minimum = "0")
    private Float grossWeight;

    @NotNull(message = "weightUnit is required")
    @Schema(description = "Weight unit", example = "K", requiredMode = Schema.RequiredMode.REQUIRED)
    private WeightUnit weightUnit;

    @NotNull(message = "nor is required")
    @Schema(description = "Non-operating Reefer flag - Y: Reefer used as dry container (cooling off), N: Operating reefer (cooling on)", 
            example = "N", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    private YesNoFlag nor;

    @NotBlank(groups = ValidationGroups.UpdateBooking.class, 
              message = "containerNo is required for booking amendments")
    @Pattern(
        regexp="^[A-Z]{4}[0-9]{7}$|^[A-Za-z0-9_-]{1,11}$",
        message="containerNo must be a valid container number (e.g., ABCD1234567) or an internal ID (1-11 chars)"
    )
    @Schema(description = "Container number or internal temp identifier. Optional for new bookings (auto-generated), required for amendments.", 
            example = "20DCTBN1")
    @Size(max = 11, message = "containerNo must not exceed 11 characters")
    private String containerNo;

    @Valid
    @Schema(description = "Reefer settings (required if equipmentType=R)")
    private ReeferSettingDTO reefer;

    // OOG (Out-of-Gauge) dimension fields - required when oogFlag = Y
    @Schema(description = "OOG - Left side overhang in feet", example = "0.5")
    private Float oogLeftFt;

    @Schema(description = "OOG - Right side overhang in feet", example = "0.5")
    private Float oogRightFt;

    @Schema(description = "OOG - Forward overhang in feet", example = "1.0")
    private Float oogForward;

    @Schema(description = "OOG - Aft (rear) overhang in feet", example = "1.0")
    private Float oogAft;

    @Schema(description = "OOG - Top overhang in feet", example = "0.8")
    private Float oogTopFt;

    @Schema(description = "OOG dimension unit - F (Feet) or M (Meters)", example = "F")
    private LengthUnit oogUnit;

    public ContainerDTO() {
    }

    public ContainerDTO(Integer cargoNo, YesNoFlag oogFlag, Integer equipmentQty, EquipmentType equipmentType,
                       String equipmentSize, YesNoFlag socFlag, YesNoFlag dgFlag, EquipmentStatus equipmentStatus,
                       Integer cargoWeight, WeightUnit weightUnit, YesNoFlag nor, String containerNo,
                       ReeferSettingDTO reefer) {
        this.cargoNo = cargoNo;
        this.oogFlag = oogFlag;
        this.equipmentQty = equipmentQty;
        this.equipmentType = equipmentType;
        this.equipmentSize = equipmentSize;
        this.socFlag = socFlag;
        this.dgFlag = dgFlag;
        this.equipmentStatus = equipmentStatus;
        this.cargoWeight = cargoWeight;
        this.weightUnit = weightUnit;
        this.nor = nor;
        this.containerNo = containerNo;
        this.reefer = reefer;
    }

    public Integer getCargoNo() {
        return cargoNo;
    }

    public void setCargoNo(Integer cargoNo) {
        this.cargoNo = cargoNo;
    }

    public YesNoFlag getOogFlag() {
        return oogFlag;
    }

    public void setOogFlag(YesNoFlag oogFlag) {
        this.oogFlag = oogFlag;
    }

    public Integer getEquipmentQty() {
        return equipmentQty;
    }

    public void setEquipmentQty(Integer equipmentQty) {
        this.equipmentQty = equipmentQty;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getEquipmentSize() {
        return equipmentSize;
    }

    public void setEquipmentSize(String equipmentSize) {
        this.equipmentSize = equipmentSize;
    }

    public YesNoFlag getSocFlag() {
        return socFlag;
    }

    public void setSocFlag(YesNoFlag socFlag) {
        this.socFlag = socFlag;
    }

    public YesNoFlag getDgFlag() {
        return dgFlag;
    }

    public void setDgFlag(YesNoFlag dgFlag) {
        this.dgFlag = dgFlag;
    }

    public EquipmentStatus getEquipmentStatus() {
        return equipmentStatus;
    }

    public void setEquipmentStatus(EquipmentStatus equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }

    public Integer getCargoWeight() {
        return cargoWeight;
    }

    public void setCargoWeight(Integer cargoWeight) {
        this.cargoWeight = cargoWeight;
    }

    public Float getTareWeight() {
        return tareWeight;
    }

    public void setTareWeight(Float tareWeight) {
        this.tareWeight = tareWeight;
    }

    public Float getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(Float grossWeight) {
        this.grossWeight = grossWeight;
    }

    public WeightUnit getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(WeightUnit weightUnit) {
        this.weightUnit = weightUnit;
    }

    public YesNoFlag getNor() {
        return nor;
    }

    public void setNor(YesNoFlag nor) {
        this.nor = nor;
    }

    public String getContainerNo() {
        return containerNo;
    }

    public void setContainerNo(String containerNo) {
        this.containerNo = containerNo;
    }

    public ReeferSettingDTO getReefer() {
        return reefer;
    }

    public void setReefer(ReeferSettingDTO reefer) {
        this.reefer = reefer;
    }

    public Float getOogLeftFt() {
        return oogLeftFt;
    }

    public void setOogLeftFt(Float oogLeftFt) {
        this.oogLeftFt = oogLeftFt;
    }

    public Float getOogRightFt() {
        return oogRightFt;
    }

    public void setOogRightFt(Float oogRightFt) {
        this.oogRightFt = oogRightFt;
    }

    public Float getOogForward() {
        return oogForward;
    }

    public void setOogForward(Float oogForward) {
        this.oogForward = oogForward;
    }

    public Float getOogAft() {
        return oogAft;
    }

    public void setOogAft(Float oogAft) {
        this.oogAft = oogAft;
    }

    public Float getOogTopFt() {
        return oogTopFt;
    }

    public void setOogTopFt(Float oogTopFt) {
        this.oogTopFt = oogTopFt;
    }

    public LengthUnit getOogUnit() {
        return oogUnit;
    }

    public void setOogUnit(LengthUnit oogUnit) {
        this.oogUnit = oogUnit;
    }
}

/**
 * Custom validation annotation for container-specific business rules.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ContainerRulesValidator.class)
@interface ValidContainerRules {
    String message() default "Invalid container rules";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

/**
 * Validator for container-specific business rules:
 * - If equipmentType == 'R' (Reefer) → reefer settings must be present
 * - Cargo weight + tare weight should not exceed container gross weight capacity (fetched dynamically from DB)
 * 
 * Note: dgFlag='Y' validation is handled at BookingRequestDTO level in ContainerCargoValidator,
 * where DG details (unNo, classCode) are validated from the matching commodity's dangerousGoods.
 */
class ContainerRulesValidator implements ConstraintValidator<ValidContainerRules, ContainerDTO> {

    private static final Logger logger = LoggerFactory.getLogger(ContainerRulesValidator.class);

    @Override
    public boolean isValid(ContainerDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true;

        boolean ok = true;

        // Rule A: Reefer requires reefer settings (Error Code: 4313)
        if (dto.getEquipmentType() == com.svm.ecom.common.enums.EquipmentType.R) {
            if (dto.getReefer() == null) {
                addFieldViolation(ctx, "reefer", "Reefer settings are required when equipmentType is R (Reefer)");
                ok = false;
            }
        }

        // Note: dgFlag='Y' is allowed - DG validation happens in ContainerCargoValidator
        // which validates unNo and classCode from commodity's dangerousGoods against master data

        // Rule B: Validate equipment size exists in equipment master
        if (dto.getEquipmentSize() != null && !dto.getEquipmentSize().trim().isEmpty()) {
            MasterDataCacheService cacheService = getCacheService();
            if (cacheService != null && cacheService.isInitialized()) {
                if (!cacheService.isValidEquipmentSize(dto.getEquipmentSize())) {
                    addFieldViolation(ctx, "equipmentSize", "Invalid equipment size: " + dto.getEquipmentSize());
                    ok = false;
                    logger.debug("Invalid equipment size: {}", dto.getEquipmentSize());
                }
            }
        }

        // Rule C: Gross weight should not exceed container capacity from DB (Error Code: 4315)
        if (dto.getEquipmentSize() != null) {
            // Get tare weight from cache and set it on the DTO
            Float cachedTareWeight = getTareWeightFromCache(dto.getEquipmentSize());
            if (cachedTareWeight != null) {
                dto.setTareWeight(cachedTareWeight);
                logger.debug("Set tare weight from cache for {}: {} kg", dto.getEquipmentSize(), cachedTareWeight);
            }
            
            // Calculate gross weight from cargo + tare (using cached tare weight)
            float grossWeightKg;
            if (dto.getCargoWeight() != null) {
                float cargoWt = dto.getCargoWeight();
                float tareWt = dto.getTareWeight() != null ? dto.getTareWeight() : 0;
                grossWeightKg = cargoWt + tareWt;
                
                // Also set the gross weight on the DTO
                dto.setGrossWeight(grossWeightKg);
                logger.debug("Set gross weight on DTO: {} kg (cargo: {} + tare: {})", grossWeightKg, cargoWt, tareWt);
            } else {
                grossWeightKg = 0;
            }
            
            // Fetch max gross weight from cache (loaded from reportDB)
            Float maxGrossWeight = getMaxGrossWeightFromCache(dto.getEquipmentSize());
            logger.debug("maxGrossWeight: {} grossWeightKg: {}", maxGrossWeight, grossWeightKg);
            if (maxGrossWeight != null && maxGrossWeight > 0 && grossWeightKg > maxGrossWeight) {
                addFieldViolation(ctx, "cargoWeight", "Weight " + (int)grossWeightKg + " kg exceeds container capacity (" + maxGrossWeight.intValue() + " kg for " + dto.getEquipmentSize() + ")");
                ok = false;
            }
        }

        // Rule D: OOG dimensions required when oogFlag is Y (Error Code: 4316)
        if (dto.getOogFlag() == com.svm.ecom.common.enums.YesNoFlag.Y) {
            boolean hasAnyDimension = (dto.getOogLeftFt() != null && dto.getOogLeftFt() > 0) ||
                                      (dto.getOogRightFt() != null && dto.getOogRightFt() > 0) ||
                                      (dto.getOogForward() != null && dto.getOogForward() > 0) ||
                                      (dto.getOogAft() != null && dto.getOogAft() > 0) ||
                                      (dto.getOogTopFt() != null && dto.getOogTopFt() > 0);
            
            if (!hasAnyDimension) {
                addFieldViolation(ctx, "oogFlag", "Out-of-Gauge cargo needs dimensions");
                ok = false;
            }
        }

        // Rule E: Gross weight must equal (tare weight + cargo weight) (Error Code: 4317) - Disabled
        /*if (dto.getGrossWeight() != null && dto.getTareWeight() != null && dto.getCargoWeight() != null) {
            float expectedGrossWeight = dto.getTareWeight() + dto.getCargoWeight();
            float actualGrossWeight = dto.getGrossWeight();
            
            // Allow small tolerance for floating point comparison
            float tolerance = 0.01f;
            if (Math.abs(actualGrossWeight - expectedGrossWeight) > tolerance) {
                add(ctx, "Gross weight must equal cargo weight plus tare weight");
                ok = false;
            }
        }*/

        return ok;
    }

    /**
     * Fetches tare weight from cache (loaded from reportDB equipments table).
     * Uses MasterDataCacheService for fast lookups without DB calls.
     * 
     * @param equipmentSize The equipment type/size (e.g., "40DC", "20DC", "40RH")
     * @return The tare weight in kg, or null if not found or cache unavailable
     */
    private Float getTareWeightFromCache(String equipmentSize) {
        try {
            MasterDataCacheService cacheService = SpringContextHolder.getBean(MasterDataCacheService.class);
            if (cacheService != null && cacheService.isInitialized()) {
                Float tareWeight = cacheService.getEquipmentTareWeight(equipmentSize);
                logger.debug("Retrieved tare weight from cache for {}: {} kg", equipmentSize, tareWeight);
                return tareWeight;
            } else {
                logger.warn("MasterDataCacheService not available or not initialized, cannot get tare weight");
            }
        } catch (Exception e) {
            logger.error("Error getting tare weight from cache for equipment type: " + equipmentSize, e);
        }
        return null;
    }

    /**
     * Fetches max gross weight from cache (loaded from reportDB equipments table).
     * Uses MasterDataCacheService for fast lookups without DB calls.
     * 
     * @param equipmentSize The equipment type/size (e.g., "40DC", "20DC", "40RH")
     * @return The max gross weight in kg, or null if not found or cache unavailable
     */
    private Float getMaxGrossWeightFromCache(String equipmentSize) {
        try {
            MasterDataCacheService cacheService = SpringContextHolder.getBean(MasterDataCacheService.class);
            if (cacheService != null && cacheService.isInitialized()) {
                Float grossWeight = cacheService.getEquipmentGrossWeight(equipmentSize);
                logger.debug("Retrieved max gross weight from cache for {}: {} kg", equipmentSize, grossWeight);
                return grossWeight;
            } else {
                logger.warn("MasterDataCacheService not available or not initialized, skipping weight capacity validation");
            }
        } catch (Exception e) {
            logger.error("Error getting max gross weight from cache for equipment type: " + equipmentSize, e);
        }
        return null;
    }

    private MasterDataCacheService getCacheService() {
        try {
            return SpringContextHolder.getBean(MasterDataCacheService.class);
        } catch (Exception e) {
            logger.error("Error getting MasterDataCacheService", e);
            return null;
        }
    }

    private static void addFieldViolation(ConstraintValidatorContext ctx, String field, String msg){
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(msg)
           .addPropertyNode(field)
           .addConstraintViolation();
    }
}
