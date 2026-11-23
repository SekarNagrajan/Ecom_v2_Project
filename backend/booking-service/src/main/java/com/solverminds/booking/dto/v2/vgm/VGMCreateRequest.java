package com.solverminds.booking.dto.v2.vgm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(
        name = "VGMInfo",
        description = "Payload to create/replace vgm information. "
                + "Includes booking-level fields and a containers array (one entry per equipment).")
public class VGMCreateRequest {

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

    @Schema(hidden = true)
    private String orderNumber;

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
    @Schema(description = "Company postal address", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private AddressRequest address;

    @NotNull(message = "containers list is required")
    @Size(min = 1, message = "At least one container is required")
    @Valid
    @ArraySchema(
            arraySchema = @Schema(description = "Per-container VGM weights and equipment identifiers", requiredMode = Schema.RequiredMode.REQUIRED),
            schema = @Schema(implementation = ContainerVGMRequest.class))
    private List<ContainerVGMRequest> containers;
}
