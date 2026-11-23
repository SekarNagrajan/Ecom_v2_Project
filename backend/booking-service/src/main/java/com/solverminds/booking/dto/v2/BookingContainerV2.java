package com.solverminds.booking.dto.v2;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.solverminds.booking.serializer.TwoDecimalSerializer;
import com.svm.ecom.common.enums.EquipmentStatus;
import com.svm.ecom.common.enums.EquipmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Details about a booked container for V2 API.
 * Note: id is optional for new bookings - system will auto-generate container numbers
 * when quantity > 1 (e.g., 20DCTBN1, 20DCTBN2, 20DCTBN3). Sequence resets per container size.
 */
@Schema(name = "BookingContainer", description = "Details about a booked container")
public class BookingContainerV2 {

    @Schema(description = "Container id (number or internal identifier). Optional for new bookings - auto-generated when quantity > 1. Required for amendments.", 
            example = "20DCTBN1")
    @Size(max = 11, message = "id must not exceed 11 characters")
    private String id;

    @NotNull
    @Schema(description = "Number of containers. When > 1 and id is not provided, containers are expanded with auto-generated IDs (e.g., 20DCTBN1, 20DCTBN2). Sequence resets per equipment size.", 
            example = "5")
    private Integer quantity;

    @NotNull
    @Schema(description = "Container type", example = "D")
    private EquipmentType type;

    @NotBlank
    @Schema(description = "Container size type code", example = "20DC")
    private String code;

    @NotNull
    @Schema(description = "Container status", example = "F")
    private EquipmentStatus status;

    @Valid
    @Schema(description = "Reefer container settings")
    private ReeferContainerSettingsV2 reeferSettings;

    @NotNull
    @Schema(description = "Is out-of-gauge container", example = "false")
    private Boolean isOOG;

    @NotNull
    @Schema(description = "Is shipper owned container", example = "false")
    private Boolean isSOC;

    @NotNull
    @Schema(description = "Is container containing dangerous goods", example = "true")
    private Boolean isDG;

    @NotNull
    @Schema(description = "Is non-operating reefer. If set to false, reefer settings have to be provided as well", example = "true")
    private Boolean isNOR;

    @Schema(description = "Tare weight of the container in kilograms", example = "2500.00")
    @JsonSerialize(using = TwoDecimalSerializer.class)
    private Double tareWeight;

    @Schema(description = "Gross weight of the container in kilograms (cargo weight + tare weight)", example = "12300.00")
    @JsonSerialize(using = TwoDecimalSerializer.class)
    private Double grossWeight;

    @NotEmpty
    @Valid
    @Schema(description = "List of commodities contained in the container")
    private List<BookingContainerCommodityV2> commodities;

    public BookingContainerV2() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public EquipmentType getType() {
        return type;
    }

    public void setType(EquipmentType type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    public ReeferContainerSettingsV2 getReeferSettings() {
        return reeferSettings;
    }

    public void setReeferSettings(ReeferContainerSettingsV2 reeferSettings) {
        this.reeferSettings = reeferSettings;
    }

    public Boolean getIsOOG() {
        return isOOG;
    }

    public void setIsOOG(Boolean isOOG) {
        this.isOOG = isOOG;
    }

    public Boolean getIsSOC() {
        return isSOC;
    }

    public void setIsSOC(Boolean isSOC) {
        this.isSOC = isSOC;
    }

    public Boolean getIsDG() {
        return isDG;
    }

    public void setIsDG(Boolean isDG) {
        this.isDG = isDG;
    }

    public Boolean getIsNOR() {
        return isNOR;
    }

    public void setIsNOR(Boolean isNOR) {
        this.isNOR = isNOR;
    }

    public Double getTareWeight() {
        return tareWeight;
    }

    public void setTareWeight(Double tareWeight) {
        this.tareWeight = tareWeight;
    }

    public Double getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(Double grossWeight) {
        this.grossWeight = grossWeight;
    }

    public List<BookingContainerCommodityV2> getCommodities() {
        return commodities;
    }

    public void setCommodities(List<BookingContainerCommodityV2> commodities) {
        this.commodities = commodities;
    }
}

