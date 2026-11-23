package com.solverminds.sibl.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.enums.EquipmentStatus;
import com.svm.ecom.common.enums.EquipmentType;
import com.svm.ecom.common.enums.WeightUnit;
import com.svm.ecom.common.enums.YesNoFlag;
import com.solverminds.sibl.util.DtoUtil;
import com.solverminds.sibl.dto.validation.ValidSIBLContainerRules;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ValidSIBLContainerRules
@Schema(name = "SIBLContainerDTO", description = "Container / equipment line item for SI. Service validation requires at least one commodity per container line (matching cargoNo and containerNo). When commodities are present, cargoWeight and weightUnit are optional (derived from SUM of commodity itemWeight).")
public class SIBLContainerDTO {

    // ===== Hidden / server-managed =====
    @Schema(hidden = true) @JsonIgnore
    private String onlineSINo;            // eSINo (link to SI) - hidden json

    @NotNull(message = "equipmentType is required")
    @JsonProperty("equipmentType")
    @Schema(description = "Equipment Type: D - Dry, R - Reefer", example = "D", requiredMode = Schema.RequiredMode.REQUIRED)
    private EquipmentType equipmentType;   // eqpType (D/R) - persisted to ecomSI_cargo.eqpType

    // ===== Visible / request fields =====
    @NotNull @Min(1)
    @Schema(description = "Sequence number for the container within the SI.",
            example = "1", required = true)
    private Integer cargoNo;              // conId

    @NotBlank(message = "equipmentSize is required")
    @Pattern(
        regexp = "^[0-9]{2}[A-Z]{2,3}[A-Z0-9]{0,2}$|^[0-9]{2}[A-Z]{2}$",
        message = "equipmentSize must resemble a size/type code (e.g., 20DC, 40DC, 40RH)"
    )
    @Schema(description = "Equipment size/type code (e.g., 20DC, 40DC, 40RH)", example = "40DC", requiredMode = Schema.RequiredMode.REQUIRED)
    private String equipmentSize;         // eqpSize

    @NotBlank(message = "containerNo is required")
    @Schema(description = "Container equipment number (dummy or actual). ISO 6346 format (e.g. ABCD1234567) or internal ID (6–20 chars).",
            example = "ABDU0408131", requiredMode = Schema.RequiredMode.REQUIRED)
    private String containerNo;           // containerno

    @JsonProperty("soc")
    @Schema(description = "Is Shipper Owned Container? Y (Yes) or N (No).",
            example = "N")
    private YesNoFlag socFlag;             // shipperOwn

    @Schema(description = "Carrier seal number.", example = "CS1")
    private String carrierSeal;           // carrierSeal

    @Schema(description = "Shipper seal number.")
    private String shipperSeal;           // ShipperSeal

    @Min(value = 0, message = "tareWeight must be non-negative")
    @Schema(description = "Tare weight of the empty container (kg).",
            example = "2200", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer tareWeight;           // tareWeight

    @Schema(description = "Cargo weight inside the container. Optional when commodities are present (derived from SUM of commodity itemWeight). Required when no commodities.", example = "5000")
    private Integer cargoWeight;          // cargoWeight

    @JsonProperty("grossWeight")
    @Schema(description = "Gross weight (tare + cargo).", example = "7200")
    private Integer grossweight;          // grossweight

    @Schema(description = "Weight unit - K (Kilos) or T (Metric Tonnes). Optional when commodities are present (derived uses K). Required when no commodities.", example = "K")
    private WeightUnit weightUnit;        // weightUnit

    @Schema(description = "NOR (Non-Operating Reefer) flag - Y (Yes) or N (No). Required when equipment is reefer.",
            example = "N")
    private YesNoFlag nor;                // ref_nor

    @NotNull(message = "equipmentStatus is required")
    @Schema(description = "Equipment load status: F = Full/Laden, M = Empty (use M for SOC empty moves).",
            example = "F", requiredMode = Schema.RequiredMode.REQUIRED)
    private EquipmentStatus equipmentStatus; // eqpstatus

    @JsonProperty(value = "oog", access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "OOG flag - Y (Out of Gauge) or N (Standard).",
            example = "N", hidden = true)
    private YesNoFlag oogFlag;            // oog flag (visible)

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "Rate equipment type ID (e.g. 20DC).", example = "20DC", hidden = true)
    private String rateEquipmentTypeId;   // rate_eqpTypeid

    @JsonProperty("reeferFlag")
    @Schema(description = "Reefer flag - Y (Reefer container) or N (Dry container).",
            example = "N")
    private YesNoFlag reefer;             // reefer flag

    @JsonProperty("reefer")
    @Schema(description = "Reefer settings (required when reefer=Y and nor!=Y)")
    private SIBLReeferDTO reeferSettings;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "OOG dimensions (required when oogFlag=Y)", hidden = true)
    private SIBLOogDTO oogDimensions;

    // ===== ctor =====
    public SIBLContainerDTO() {}

    // ===== getters & setters =====
    public String getOnlineSINo() { return DtoUtil.emptyIfNull(onlineSINo); }
    public void setOnlineSINo(String onlineSINo) { this.onlineSINo = onlineSINo; }

    public Integer getTareWeight() { return DtoUtil.emptyIntIfNull(tareWeight); }
    public void setTareWeight(Integer tareWeight) { this.tareWeight = tareWeight; }

    public Integer getGrossweight() { return DtoUtil.emptyIntIfNull(grossweight); }
    public void setGrossweight(Integer grossweight) { this.grossweight = grossweight; }

    public EquipmentType getEquipmentType() { return equipmentType; }
    public void setEquipmentType(EquipmentType equipmentType) { this.equipmentType = equipmentType; }

    public String getRateEquipmentTypeId() { return DtoUtil.emptyIfNull(rateEquipmentTypeId); }
    public void setRateEquipmentTypeId(String rateEquipmentTypeId) { this.rateEquipmentTypeId = rateEquipmentTypeId; }

    public Integer getCargoNo() { return cargoNo; }
    public void setCargoNo(Integer cargoNo) { this.cargoNo = cargoNo; }

    public String getEquipmentSize() { return DtoUtil.emptyIfNull(equipmentSize); }
    public void setEquipmentSize(String equipmentSize) { this.equipmentSize = equipmentSize; }

    public String getContainerNo() { return DtoUtil.emptyIfNull(containerNo); }
    public void setContainerNo(String containerNo) { this.containerNo = containerNo; }

    public YesNoFlag getSocFlag() { return socFlag; }
    public void setSocFlag(YesNoFlag socFlag) { this.socFlag = socFlag; }

    public String getCarrierSeal() { return DtoUtil.emptyIfNull(carrierSeal); }
    public void setCarrierSeal(String carrierSeal) { this.carrierSeal = carrierSeal; }

    public String getShipperSeal() { return DtoUtil.emptyIfNull(shipperSeal); }
    public void setShipperSeal(String shipperSeal) { this.shipperSeal = shipperSeal; }

    public Integer getCargoWeight() { return cargoWeight; }
    public void setCargoWeight(Integer cargoWeight) { this.cargoWeight = cargoWeight; }

    public WeightUnit getWeightUnit() { return weightUnit; }
    public void setWeightUnit(WeightUnit weightUnit) { this.weightUnit = weightUnit; }

    public YesNoFlag getNor() { return nor; }
    public void setNor(YesNoFlag nor) { this.nor = nor; }

    public EquipmentStatus getEquipmentStatus() { return equipmentStatus; }
    public void setEquipmentStatus(EquipmentStatus equipmentStatus) { this.equipmentStatus = equipmentStatus; }

    public YesNoFlag getReefer() { return reefer; }
    public void setReefer(YesNoFlag reefer) { this.reefer = reefer; }

    public YesNoFlag getOogFlag() { return oogFlag; }
    public void setOogFlag(YesNoFlag oogFlag) { this.oogFlag = oogFlag; }

    /** Returns reefer settings; null when not set (SI view: omit from JSON when reeferFlag=N). */
    public SIBLReeferDTO getReeferSettings() { return reeferSettings; }
    public void setReeferSettings(SIBLReeferDTO reeferSettings) { this.reeferSettings = reeferSettings; }

    /** Returns OOG dimensions; null when not set (SI view: omit from container list; OOG only in commodity when oog=Y). */
    public SIBLOogDTO getOogDimensions() { return oogDimensions; }
    public void setOogDimensions(SIBLOogDTO oogDimensions) { this.oogDimensions = oogDimensions; }
}