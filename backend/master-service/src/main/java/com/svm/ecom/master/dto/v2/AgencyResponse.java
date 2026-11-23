package com.svm.ecom.master.dto.v2;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Agency response for GET /api/v2/master/agencies.
 * Matches client request OpenAPI schema "Agency".
 */
@Schema(name = "Agency", description = "Represents an agency entity.")
public class AgencyResponse {

    @Schema(description = "Code of the agency.", example = "INMUMF", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "Name of the agency.", example = "Moov Logistics India Pvt Ltd",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Type of the agency.", example = "Office", nullable = true,
            allowableValues = {"Office", "Corporate", "Branch", "Back Office"})
    private String type;

    @Schema(description = "Postal address", requiredMode = Schema.RequiredMode.REQUIRED)
    private AddressResponse address;

    @Schema(description = "Email address of the agency.", example = "inmum@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "UN/LOCODE port code for the agency location.", example = "DEHAM", nullable = true)
    private String unPortCode;

    @ArraySchema(
            arraySchema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED),
            schema = @Schema(implementation = AgencyPortResponse.class)
    )
    private List<AgencyPortResponse> controllingPorts;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AddressResponse getAddress() {
        return address;
    }

    public void setAddress(AddressResponse address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUnPortCode() {
        return unPortCode;
    }

    public void setUnPortCode(String unPortCode) {
        this.unPortCode = unPortCode;
    }

    public List<AgencyPortResponse> getControllingPorts() {
        return controllingPorts;
    }

    public void setControllingPorts(List<AgencyPortResponse> controllingPorts) {
        this.controllingPorts = controllingPorts;
    }
}
