package com.solverminds.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.common.enums.PortCallType;
import com.svm.ecom.common.enums.TransportMode;
import com.solverminds.booking.dto.validation.DifferentPorts;
import com.solverminds.booking.dto.validation.ValidRoutePorts;
import com.svm.ecom.constants.DateFormatConstants;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.OffsetDateTime;

/**
 * Route leg details representing vessel schedules and port information.
 */
@DifferentPorts
@ValidRoutePorts
@Schema(description = "Route leg details")
public class RouteDTO {

    @NotNull
    @Schema(description = "Leg sequence number", example = "1")
    private Integer legNo;

    @Schema(description = "Load bound indicator", example = "N")
    private Bound loadBound;

    @Schema(description = "Service code", example = "PRO")
    private String serviceCode;

    @Schema(description = "Vessel code - Required when serviceType is F (Feeder) or M (Mainline)", example = "PRO")
    private String vesselCode;

    @Schema(description = "Voyage code - Required when serviceType is F (Feeder) or M (Mainline)", example = "101")
    private String voyageCode;

    @Schema(description = "Service type - Transport Mode", example = "M")
    private TransportMode serviceType;

    @NotBlank(message = "Field must be provided")
    @Schema(description = "Loading port ID", example = "INNSA")
    private String loadPortId;

    @Schema(description = "Loading terminal", example = "INNSABMCT0")
    private String loadTerminal;

    @NotBlank(message = "Field must be provided")
    @Schema(description = "Discharge port ID", example = "AEJEA")
    private String dischargePortId;

    @Schema(description = "Discharge terminal", example = "AEJEA01")
    private String dischargeTerminal;

    @NotNull(message = "ETD (Departure date) must be selected")
    @Future(message = "Booking date cannot be in the past and empty")
    @Schema(
        description = "Estimated time of departure in load port local time (ISO 8601 with offset). Example: 2025-09-19T14:30:00+08:00",
        example = "2025-09-19T14:30:00+08:00"
    )
    private OffsetDateTime etdDate;

    @NotNull(message = "ETA (Arrival date) must be calculated")
    @Schema(
        description = "Estimated time of arrival in discharge port local time (ISO 8601 with offset). Example: 2025-09-26T02:00:00+04:00",
        example = "2025-09-26T02:00:00+04:00"
    )
    private OffsetDateTime etaDate;

    @Schema(description = "Load type - Port Call Type", example = "L")
    private PortCallType loadType;

    @Schema(description = "Discharge type - Port Call Type", example = "L")
    private PortCallType dischType;

    @Schema(description = "Group call ID", example = "GrpCall-498839")
    private String groupCallId;

    @Schema(description = "Discharge call ID", example = "GrpCall-498842")
    private String dischargeCallId;

    @Schema(description = "Load call ID", example = "GrpCall-498839")
    private String loadCallId;

    @Schema(description = "Discharge bound indicator", example = "N")
    private Bound dischargeBound;

    public RouteDTO() {
    }

    public RouteDTO(Integer legNo, Bound loadBound, String serviceCode, String vesselCode, String voyageCode,
                   TransportMode serviceType, String loadPortId, String loadTerminal, String dischargePortId,
                   String dischargeTerminal, OffsetDateTime etdDate, OffsetDateTime etaDate, PortCallType loadType,
                   PortCallType dischType, String groupCallId, String dischargeCallId, String loadCallId, Bound dischargeBound) {
        this.legNo = legNo;
        this.loadBound = loadBound;
        this.serviceCode = serviceCode;
        this.vesselCode = vesselCode;
        this.voyageCode = voyageCode;
        this.serviceType = serviceType;
        this.loadPortId = loadPortId;
        this.loadTerminal = loadTerminal;
        this.dischargePortId = dischargePortId;
        this.dischargeTerminal = dischargeTerminal;
        this.etdDate = etdDate;
        this.etaDate = etaDate;
        this.loadType = loadType;
        this.dischType = dischType;
        this.groupCallId = groupCallId;
        this.dischargeCallId = dischargeCallId;
        this.loadCallId = loadCallId;
        this.dischargeBound = dischargeBound;
    }

    public Integer getLegNo() {
        return legNo;
    }

    public void setLegNo(Integer legNo) {
        this.legNo = legNo;
    }

    public Bound getLoadBound() {
        return loadBound;
    }

    public void setLoadBound(Bound loadBound) {
        this.loadBound = loadBound;
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

    public String getLoadTerminal() {
        return loadTerminal;
    }

    public void setLoadTerminal(String loadTerminal) {
        this.loadTerminal = loadTerminal;
    }

    public String getDischargePortId() {
        return dischargePortId;
    }

    public void setDischargePortId(String dischargePortId) {
        this.dischargePortId = dischargePortId;
    }

    public String getDischargeTerminal() {
        return dischargeTerminal;
    }

    public void setDischargeTerminal(String dischargeTerminal) {
        this.dischargeTerminal = dischargeTerminal;
    }

    public OffsetDateTime getEtdDate() {
        return etdDate;
    }

    public void setEtdDate(OffsetDateTime etdDate) {
        this.etdDate = etdDate;
    }

    public OffsetDateTime getEtaDate() {
        return etaDate;
    }

    public void setEtaDate(OffsetDateTime etaDate) {
        this.etaDate = etaDate;
    }

    public PortCallType getLoadType() {
        return loadType;
    }

    public void setLoadType(PortCallType loadType) {
        this.loadType = loadType;
    }

    public PortCallType getDischType() {
        return dischType;
    }

    public void setDischType(PortCallType dischType) {
        this.dischType = dischType;
    }

    public String getGroupCallId() {
        return groupCallId;
    }

    public void setGroupCallId(String groupCallId) {
        this.groupCallId = groupCallId;
    }

    public String getDischargeCallId() {
        return dischargeCallId;
    }

    public void setDischargeCallId(String dischargeCallId) {
        this.dischargeCallId = dischargeCallId;
    }

    public String getLoadCallId() {
        return loadCallId;
    }

    public void setLoadCallId(String loadCallId) {
        this.loadCallId = loadCallId;
    }

    public Bound getDischargeBound() {
        return dischargeBound;
    }

    public void setDischargeBound(Bound dischargeBound) {
        this.dischargeBound = dischargeBound;
    }
}
