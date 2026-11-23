package com.svm.ecom.master.dto.v2;

import com.svm.ecom.master.enums.DgPackagingType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DG packaging response for GET /api/v2/master/dg-packagings.
 * Matches OpenAPI schema "DgPackaging".
 */
@Schema(name = "DgPackaging", description = "DG packaging master entry used for outer/inner packing pickers",
        example = "{\"code\":\"1G\",\"name\":\"FIBRE DRUM\",\"packagingType\":\"OUTER\"}")
public class DgPackagingResponse {

    @Schema(description = "Packaging code", example = "1G", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "Packaging name / description", example = "FIBRE DRUM",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Packaging usage type", example = "OUTER",
            requiredMode = Schema.RequiredMode.REQUIRED, implementation = DgPackagingType.class)
    private DgPackagingType packagingType;

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

    public DgPackagingType getPackagingType() {
        return packagingType;
    }

    public void setPackagingType(DgPackagingType packagingType) {
        this.packagingType = packagingType;
    }
}
