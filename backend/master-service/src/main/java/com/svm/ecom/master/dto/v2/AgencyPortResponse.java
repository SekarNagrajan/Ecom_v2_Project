package com.svm.ecom.master.dto.v2;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AgencyPort", description = "Controlling port for an agency.")
public class AgencyPortResponse {

    @Schema(description = "Port code", example = "INMAA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "Valid from (date)", example = "2022-01-01", format = "date",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String validFrom;

    @Schema(description = "Valid to (date)", example = "2030-12-31", format = "date",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String validTo;

    @Schema(description = "Port relationship type", example = "Export",
            requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"Import", "Export"})
    private String type;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
