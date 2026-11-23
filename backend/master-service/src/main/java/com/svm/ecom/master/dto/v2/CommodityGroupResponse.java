package com.svm.ecom.master.dto.v2;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Commodity group descriptor for GET /api/v2/master/commodities.
 * Matches client request OpenAPI schema "CommodityGroup".
 */
@Schema(name = "CommodityGroup", description = "Commodity group descriptor",
        example = "{\"code\":\"08\",\"name\":\"Fruit and nuts, edible; peel of citrus fruit or melons\"}")
public class CommodityGroupResponse {

    @Schema(description = "Commodity group code", example = "08", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "Commodity group name", example = "Fruit and nuts, edible; peel of citrus fruit or melons",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    public CommodityGroupResponse() {}

    public CommodityGroupResponse(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
