package com.solverminds.booking.dto.v2.vgm;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.solverminds.booking.dto.validation.ValidationGroups;
import com.svm.ecom.common.enums.WeightUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
@Schema(description = "VGM weight and equipment for one container")
public class ContainerVGM {

    @NotBlank(groups = ValidationGroups.VgmUpsert.class, message = "equipmentId is required")
    @Schema(description = "Container id", requiredMode = Schema.RequiredMode.REQUIRED, example = "TDTU6709271")
    private String equipmentId;

    @NotBlank(groups = ValidationGroups.VgmUpsert.class, message = "equipmentType is required")
    @Schema(description = "Container type", requiredMode = Schema.RequiredMode.REQUIRED, example = "40DC")
    private String equipmentType;

    @NotNull(groups = ValidationGroups.VgmUpsert.class, message = "weight is required")
    @Positive(groups = ValidationGroups.VgmUpsert.class, message = "weight must be a positive number")
    @Schema(description = "VGM weight in the provided unit", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonSerialize(nullsUsing = EmptyStringNullDoubleSerializer.class)
    private Double weight;

    @NotNull(groups = ValidationGroups.VgmUpsert.class, message = "unit is required")
    @Schema(description = "Weight unit", implementation = WeightUnit.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private WeightUnit unit;
}
