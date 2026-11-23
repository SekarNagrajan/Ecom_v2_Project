package com.solverminds.booking.dto.v2.vgm;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "VgmStatus", description = "VGM Status: A – Active", enumAsRef = true, example = "A")
public enum VgmStatus {
    @Schema(description = "A – Active")
    A
}
