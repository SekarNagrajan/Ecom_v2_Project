package com.svm.ecom.carrier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.svm.ecom.carrier.enums.CallStatus;
import com.svm.ecom.common.enums.Bound;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
        "serviceCode", "serviceName", "proformaCode", "vesselCode", "vesselName", "vesselImo",
        "maxTeu", "voyageNumber", "bound", "portCode", "portName",
        "terminalCode", "terminalName", "arrivalPilot", "arrivalDock",
        "departureDock", "departurePilot", "callStatus", "remarks", "scheduleWeek", "scheduleMonthYear"
})
@Schema(name = "LongRangeScheduleCall", description = "One long range schedule row.")
public class LongRangeScheduleCall {

    @Schema(description = "Service code.", example = "PAX")
    private String serviceCode;

    @Schema(description = "Service name.", example = "PANDA EXPRESS")
    private String serviceName;

    @Schema(description = "Proforma schedule code.", example = "PAXPRFSCH0001")
    private String proformaCode;

    @Schema(description = "Vessel code.", example = "EXC")
    private String vesselCode;

    @Schema(description = "Vessel name.", example = "XIN CHANG SHU")
    private String vesselName;

    @Schema(description = "Vessel IMO number.", example = "9304813")
    private String vesselImo;

    @Schema(description = "Maximum TEU capacity of the vessel.", example = "8500")
    private Integer maxTeu;

    @Schema(description = "Voyage number.", example = "025")
    private String voyageNumber;

    @Schema(description = "Voyage bound direction: W – West, E – East, N – North, S – South.", example = "S")
    private Bound bound;

    @Schema(description = "Port code.", example = "CNSHK")
    private String portCode;

    @Schema(description = "Port name.", example = "SHEKOU, CHINA")
    private String portName;

    @Schema(description = "Terminal code.", example = "CNSHKCCT00")
    private String terminalCode;

    @Schema(description = "Terminal name.", example = "CHIWAN CONTAINER TERMINAL")
    private String terminalName;

    @Schema(description = "Arrival pilot timestamp.", type = "string", format = "date-time", example = "2026-08-19T01:00:00+08:00")
    private OffsetDateTime arrivalPilot;

    @Schema(description = "Arrival dock timestamp.", type = "string", format = "date-time", example = "2026-08-19T01:49:00+08:00")
    private OffsetDateTime arrivalDock;

    @Schema(description = "Departure dock timestamp.", type = "string", format = "date-time", example = "2026-08-19T11:36:00+08:00")
    private OffsetDateTime departureDock;

    @Schema(description = "Departure pilot timestamp.", type = "string", format = "date-time", example = "2026-08-19T12:36:00+08:00")
    private OffsetDateTime departurePilot;

    @Schema(description = "Call status: S – Sailed/Confirmed Call, N – Normal call, D – Omitted call.", example = "S")
    private CallStatus callStatus;

    @Schema(description = "Remarks for the schedule row.", example = "")
    private String remarks;

    @Schema(description = "Schedule week number.", example = "34")
    private Integer scheduleWeek;

    @Schema(description = "Schedule month and year label.", example = "Aug-2026")
    private String scheduleMonthYear;

    public String getServiceCode() { return serviceCode; }
    public void setServiceCode(String serviceCode) { this.serviceCode = serviceCode; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getProformaCode() { return proformaCode; }
    public void setProformaCode(String proformaCode) { this.proformaCode = proformaCode; }

    public String getVesselCode() { return vesselCode; }
    public void setVesselCode(String vesselCode) { this.vesselCode = vesselCode; }

    public String getVesselName() { return vesselName; }
    public void setVesselName(String vesselName) { this.vesselName = vesselName; }

    public String getVesselImo() { return vesselImo; }
    public void setVesselImo(String vesselImo) { this.vesselImo = vesselImo; }

    public Integer getMaxTeu() { return maxTeu; }
    public void setMaxTeu(Integer maxTeu) { this.maxTeu = maxTeu; }

    public String getVoyageNumber() { return voyageNumber; }
    public void setVoyageNumber(String voyageNumber) { this.voyageNumber = voyageNumber; }

    public Bound getBound() { return bound; }
    public void setBound(Bound bound) { this.bound = bound; }

    public String getPortCode() { return portCode; }
    public void setPortCode(String portCode) { this.portCode = portCode; }

    public String getPortName() { return portName; }
    public void setPortName(String portName) { this.portName = portName; }

    public String getTerminalCode() { return terminalCode; }
    public void setTerminalCode(String terminalCode) { this.terminalCode = terminalCode; }

    public String getTerminalName() { return terminalName; }
    public void setTerminalName(String terminalName) { this.terminalName = terminalName; }

    public OffsetDateTime getArrivalPilot() { return arrivalPilot; }
    public void setArrivalPilot(OffsetDateTime arrivalPilot) { this.arrivalPilot = arrivalPilot; }

    public OffsetDateTime getArrivalDock() { return arrivalDock; }
    public void setArrivalDock(OffsetDateTime arrivalDock) { this.arrivalDock = arrivalDock; }

    public OffsetDateTime getDepartureDock() { return departureDock; }
    public void setDepartureDock(OffsetDateTime departureDock) { this.departureDock = departureDock; }

    public OffsetDateTime getDeparturePilot() { return departurePilot; }
    public void setDeparturePilot(OffsetDateTime departurePilot) { this.departurePilot = departurePilot; }

    public CallStatus getCallStatus() { return callStatus; }
    public void setCallStatus(CallStatus callStatus) { this.callStatus = callStatus; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Integer getScheduleWeek() { return scheduleWeek; }
    public void setScheduleWeek(Integer scheduleWeek) { this.scheduleWeek = scheduleWeek; }

    public String getScheduleMonthYear() { return scheduleMonthYear; }
    public void setScheduleMonthYear(String scheduleMonthYear) { this.scheduleMonthYear = scheduleMonthYear; }
}
