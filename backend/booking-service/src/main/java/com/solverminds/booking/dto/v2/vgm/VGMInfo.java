package com.solverminds.booking.dto.v2.vgm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.solverminds.booking.dto.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@Schema(
        name = "VGMInfo",
        description = "Verified Gross Mass (VGM) information. "
                + "GET returns booking-level header fields with a containers array (one entry per equipment). "
                + "PUT accepts the same shape via the VGMInfo request body.")
public class VGMInfo {

    @Schema(description = "Reference number")
    private String referenceNumber;

    @Schema(hidden = true)
    @JsonIgnore
    private String orderNumber;

    @NotBlank(groups = ValidationGroups.VgmUpsert.class, message = "authorizedPerson is required")
    @Schema(description = "Name of authorized person", requiredMode = Schema.RequiredMode.REQUIRED)
    private String authorizedPerson;

    @NotBlank(groups = ValidationGroups.VgmUpsert.class, message = "obtainedAt is required")
    @Schema(description = "Date and time when VGM was obtained", format = "date-time", requiredMode = Schema.RequiredMode.REQUIRED)
    private String obtainedAt;

    @NotNull(groups = ValidationGroups.VgmUpsert.class, message = "method is required")
    @Schema(description = "VGM weighing method", implementation = VgmMethod.class, requiredMode = Schema.RequiredMode.REQUIRED)
    private VgmMethod method;

    @Schema(description = "VGM data source")
    private String source;

    @Schema(description = "VGM version number", type = "integer", format = "int32", example = "0")
    private Integer version;

    @Schema(description = "VGM status", implementation = VgmStatus.class)
    @JsonSerialize(nullsUsing = EmptyStringNullVgmStatusSerializer.class)
    private VgmStatus status;

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

    @NotNull(groups = ValidationGroups.VgmUpsert.class, message = "containers list is required")
    @Size(groups = ValidationGroups.VgmUpsert.class, min = 1, message = "At least one container is required")
    @Valid
    @ArraySchema(
            arraySchema = @Schema(description = "Per-container VGM weights and equipment identifiers", requiredMode = Schema.RequiredMode.REQUIRED),
            schema = @Schema(implementation = ContainerVGM.class))
    private List<ContainerVGM> containers;
}
