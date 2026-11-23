package com.solverminds.booking.dto.v3.vgm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.solverminds.booking.dto.v2.vgm.Address;
import com.solverminds.booking.dto.v2.vgm.EmptyStringNullDoubleSerializer;
import com.solverminds.booking.dto.v2.vgm.EmptyStringNullVgmStatusSerializer;
import com.solverminds.booking.dto.v2.vgm.VgmMethod;
import com.solverminds.booking.dto.v2.vgm.VgmStatus;
import com.svm.ecom.common.enums.WeightUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

/**
 * One container's VGM: equipment/weight plus the declarant fields that v2 stored once per booking.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(
        name = "ContainerVgm",
        description = "VGM weight, equipment, and declarant details for one container")
public class ContainerVgmV3 {

    @NotBlank(message = "equipmentId is required")
    @Schema(description = "Container id", requiredMode = Schema.RequiredMode.REQUIRED, example = "TDTU6709271")
    private String equipmentId;

    @NotBlank(message = "equipmentType is required")
    @Schema(description = "Container type", requiredMode = Schema.RequiredMode.REQUIRED, example = "40DC")
    private String equipmentType;

    @NotNull(message = "weight is required")
    @Positive(message = "weight must be a positive number")
    @Schema(description = "VGM weight in the provided unit", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonSerialize(nullsUsing = EmptyStringNullDoubleSerializer.class)
    private Double weight;

    @NotNull(message = "unit is required")
    @Schema(description = "Weight unit", implementation = WeightUnit.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private WeightUnit unit;

    @NotBlank(message = "authorizedPerson is required")
    @Schema(description = "Name of authorized person", requiredMode = Schema.RequiredMode.REQUIRED)
    private String authorizedPerson;

    @NotBlank(message = "obtainedAt is required")
    @Schema(description = "Date and time when VGM was obtained", format = "date-time", requiredMode = Schema.RequiredMode.REQUIRED)
    private String obtainedAt;

    @NotNull(message = "method is required")
    @Schema(description = "VGM weighing method", implementation = VgmMethod.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private VgmMethod method;

    @Schema(description = "Reference number")
    private String referenceNumber;

    @Schema(description = "Company name")
    private String companyName;

    @Schema(description = "Contact telephone number")
    private String phone;

    @Pattern(
            regexp = "^$|^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$",
            message = "email must be a valid email address")
    @Schema(description = "Contact email address", format = "email")
    private String email;

    @Valid
    @Schema(description = "Company postal address")
    private Address address;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "VGM data source")
    private String source;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "VGM version number", type = "integer", format = "int32", example = "0")
    private Integer version;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "VGM status", implementation = VgmStatus.class)
    @JsonSerialize(nullsUsing = EmptyStringNullVgmStatusSerializer.class)
    private VgmStatus status;
}
