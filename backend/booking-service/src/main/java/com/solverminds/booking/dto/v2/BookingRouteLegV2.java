package com.solverminds.booking.dto.v2;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.common.enums.PortCallType;
import com.svm.ecom.common.enums.TransportMode;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

/**
 * Details about a booked route's leg for V2 API.
 */
@Schema(name = "BookingRouteLeg", description = "Details about a booked route's leg")
public class BookingRouteLegV2 {

    @Schema(description = "Service code", example = "PAX")
    private String serviceCode;

    @Schema(description = "Vessel code", example = "P07")
    private String vesselCode;

    @Schema(description = "Voyage code", example = "532")
    private String voyageCode;

    @NotNull
    @Schema(description = "Service type (Transport mode)", example = "M")
    private TransportMode serviceType;

    @NotBlank
    @Schema(description = "Load port ID", example = "CNNGB")
    private String loadPortId;

    @Schema(description = "Load bound direction", example = "N")
    private Bound loadBound;

    @Schema(description = "Load terminal ID", example = "CNNGBCTT00")
    private String loadTerminalId;

    @Schema(description = "Load type (Port call type)", example = "L")
    private PortCallType loadType;

    @NotBlank
    @Schema(description = "Discharge port ID", example = "ESBCN")
    private String dischargePortId;

    @Schema(description = "Discharge bound direction", example = "N")
    private Bound dischargeBound;

    @Schema(description = "Discharge terminal ID", example = "ESBCNHPB00")
    private String dischargeTerminalId;

    @Schema(description = "Discharge type (Port call type)", example = "L")
    private PortCallType dischargeType;

    @Schema(description = "Estimated time of departure in load port local time (ISO 8601 with offset)", example = "2025-09-19T03:00:00+05:30", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime etd;

    @Schema(description = "Estimated time of arrival in discharge port local time (ISO 8601 with offset)", example = "2025-09-26T02:00:00+04:00", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime eta;

    @Schema(description = "Group call ID", example = "GrpCall-6736")
    private String groupCallId;

    @Schema(description = "Load call ID", example = "GrpCall-6736")
    private String loadCallId;

    @Schema(description = "Discharge call ID", example = "GrpCall-6739")
    private String dischargeCallId;

    public BookingRouteLegV2() {
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getVesselCode() {
        return vesselCode;
    }

    public void setVesselCode(String vesselCode) {
        this.vesselCode = vesselCode;
    }

    public String getVoyageCode() {
        return voyageCode;
    }

    public void setVoyageCode(String voyageCode) {
        this.voyageCode = voyageCode;
    }

    public TransportMode getServiceType() {
        return serviceType;
    }

    public void setServiceType(TransportMode serviceType) {
        this.serviceType = serviceType;
    }

    public String getLoadPortId() {
        return loadPortId;
    }

    public void setLoadPortId(String loadPortId) {
        this.loadPortId = loadPortId;
    }

    public Bound getLoadBound() {
        return loadBound;
    }

    public void setLoadBound(Bound loadBound) {
        this.loadBound = loadBound;
    }

    public String getLoadTerminalId() {
        return loadTerminalId;
    }

    public void setLoadTerminalId(String loadTerminalId) {
        this.loadTerminalId = loadTerminalId;
    }

    public PortCallType getLoadType() {
        return loadType;
    }

    public void setLoadType(PortCallType loadType) {
        this.loadType = loadType;
    }

    public String getDischargePortId() {
        return dischargePortId;
    }

    public void setDischargePortId(String dischargePortId) {
        this.dischargePortId = dischargePortId;
    }

    public Bound getDischargeBound() {
        return dischargeBound;
    }

    public void setDischargeBound(Bound dischargeBound) {
        this.dischargeBound = dischargeBound;
    }

    public String getDischargeTerminalId() {
        return dischargeTerminalId;
    }

    public void setDischargeTerminalId(String dischargeTerminalId) {
        this.dischargeTerminalId = dischargeTerminalId;
    }

    public PortCallType getDischargeType() {
        return dischargeType;
    }

    public void setDischargeType(PortCallType dischargeType) {
        this.dischargeType = dischargeType;
    }

    public OffsetDateTime getEtd() {
        return etd;
    }

    public void setEtd(OffsetDateTime etd) {
        this.etd = etd;
    }

    public OffsetDateTime getEta() {
        return eta;
    }

    public void setEta(OffsetDateTime eta) {
        this.eta = eta;
    }

    public String getGroupCallId() {
        return groupCallId;
    }

    public void setGroupCallId(String groupCallId) {
        this.groupCallId = groupCallId;
    }

    public String getLoadCallId() {
        return loadCallId;
    }

    public void setLoadCallId(String loadCallId) {
        this.loadCallId = loadCallId;
    }

    public String getDischargeCallId() {
        return dischargeCallId;
    }

    public void setDischargeCallId(String dischargeCallId) {
        this.dischargeCallId = dischargeCallId;
    }
}

