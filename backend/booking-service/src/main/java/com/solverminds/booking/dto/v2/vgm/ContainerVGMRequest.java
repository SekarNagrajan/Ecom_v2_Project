package com.solverminds.booking.dto.v2.vgm;

import com.svm.ecom.common.enums.WeightUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Schema(name = "ContainerVGM", description = "VGM weight and equipment for one container")
public class ContainerVGMRequest {

    @NotBlank(message = "equipmentId is required")
    @Schema(description = "Container id", requiredMode = Schema.RequiredMode.REQUIRED, example = "TDTU6709271")
    private String equipmentId;

    @NotBlank(message = "equipmentType is required")
    @Schema(description = "Container type", requiredMode = Schema.RequiredMode.REQUIRED, example = "40DC")
    private String equipmentType;

    @NotNull(message = "weight is required")
    @Positive(message = "weight must be a positive number")
    @Schema(description = "VGM weight in the provided unit", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double weight;

    @NotNull(message = "unit is required")
    @Schema(description = "Weight unit", implementation = WeightUnit.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private WeightUnit unit;
}
