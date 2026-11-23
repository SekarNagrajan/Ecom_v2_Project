package com.solverminds.booking.dto.v3.vgm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * V3 VGM payload. GET and PUT use the same shape: one object per container,
 * including declarant/header fields that v2 kept at booking level.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(
        name = "VgmInfo",
        description = "Verified Gross Mass (VGM) information. "
                + "GET returns one object per equipment with container-level VGM fields. "
                + "PUT accepts the same shape.")
public class VgmInfoV3 {

    @NotNull(message = "containers list is required")
    @Size(min = 1, message = "At least one container is required")
    @Valid
    @ArraySchema(
            arraySchema = @Schema(
                    description = "Per-container VGM weights, equipment identifiers, and declarant details",
                    requiredMode = Schema.RequiredMode.REQUIRED),
            schema = @Schema(implementation = ContainerVgmV3.class))
    private List<ContainerVgmV3> containers;
}
