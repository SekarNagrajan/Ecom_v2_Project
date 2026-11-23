package com.svm.ecom.master.dto.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.master.enums.CommodityType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Commodity response for GET /api/v2/master/commodities.
 * Matches client request OpenAPI schema "Commodity".
 */
@Schema(name = "Commodity", description = "Represents information about a commodity",
        example = "{\"code\":\"0802520000\",\"name\":\"Fresh or dried pistachios, shelled\",\"type\":\"23\","
                + "\"group\":{\"code\":\"08\",\"name\":\"Fruit and nuts, edible; peel of citrus fruit or melons\"},"
                + "\"isRestricted\":false,\"isExempt\":true}")
public class CommodityResponse {

    @Schema(description = "HS code of the commodity", example = "0802520000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "Name of the commodity", example = "Fresh or dried pistachios, shelled",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Type of the commodity", example = "23",
            requiredMode = Schema.RequiredMode.REQUIRED, implementation = CommodityType.class)
    private CommodityType type;

    @Schema(description = "Commodity group information", requiredMode = Schema.RequiredMode.REQUIRED)
    private CommodityGroupResponse group;

    @JsonProperty("isRestricted")
    @Schema(description = "Indicates if this commodity is restricted", example = "false",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean restricted;

    @JsonProperty("isExempt")
    @Schema(description = "Indicates if this commodity is exempt", example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean exempt;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CommodityType getType() { return type; }
    public void setType(CommodityType type) { this.type = type; }

    public CommodityGroupResponse getGroup() { return group; }
    public void setGroup(CommodityGroupResponse group) { this.group = group; }

    public boolean isRestricted() { return restricted; }
    public void setRestricted(boolean restricted) { this.restricted = restricted; }

    public boolean isExempt() { return exempt; }
    public void setExempt(boolean exempt) { this.exempt = exempt; }
}
