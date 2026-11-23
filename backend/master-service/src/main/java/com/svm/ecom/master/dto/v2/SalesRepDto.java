package com.svm.ecom.master.dto.v2;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * Sales representative. Same documentation style as client request standard.
 * Dates use string ($date-time), example: 2025-09-10T00:00:00Z.
 */
@Schema(name = "SalesRep", description = "Represents a sales representative assigned to the customer.",
        example = "{\"code\":\"SR001\",\"name\":\"Jane Smith\",\"city\":\"New York\",\"activeFrom\":\"2025-09-10T00:00:00Z\",\"activeTill\":\"2026-09-10T00:00:00Z\"}")
public class SalesRepDto {

    @Schema(description = "Code of the sales representative.", example = "SR001")
    private String code;
    @Schema(description = "Name of the sales representative.", example = "Jane Smith")
    private String name;
    @Schema(description = "City where the sales representative is based.", example = "New York")
    private String city;
    @Schema(description = "Active from date timestamp. Start of the assignment period.", format = "date-time", example = "2025-09-10T00:00:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date activeFrom;
    @Schema(description = "Active till date timestamp. End of the assignment period.", format = "date-time", example = "2025-09-10T00:00:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date activeTill;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Date getActiveFrom() { return activeFrom; }
    public void setActiveFrom(Date activeFrom) { this.activeFrom = activeFrom; }
    public Date getActiveTill() { return activeTill; }
    public void setActiveTill(Date activeTill) { this.activeTill = activeTill; }
}
