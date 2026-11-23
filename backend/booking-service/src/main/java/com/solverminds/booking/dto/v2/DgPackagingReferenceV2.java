package com.solverminds.booking.dto.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Packaging reference: client sends code; name is enriched read-only on response.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "DgPackagingReference", description = "DG packaging code reference")
public class DgPackagingReferenceV2 {

    @Schema(description = "Packaging code from dg-packagings master", example = "1G",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "Packaging name (enriched from master)", example = "FIBRE DRUM",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
