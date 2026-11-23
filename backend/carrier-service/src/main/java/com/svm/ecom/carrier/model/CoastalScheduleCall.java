package com.svm.ecom.carrier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.svm.ecom.carrier.enums.CallStatus;
import com.svm.ecom.carrier.enums.CallType;
import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.common.enums.YesNoFlag;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true, value = {

        "callOrder", "cycleNumber", "subCycleNumber",

        "statusFlag", "callOrder", "cycleNumber", "subCycleNumber"

})
@JsonPropertyOrder({
        "serviceCode", "serviceName", "vesselCode", "vesselName", "vesselImoNumber",
        "voyageNumber", "pfBound", "bound",
        "pivotVoyage1", "pivotBound1", "pivotVoyage2", "pivotBound2", "pivotVoyage3", "pivotBound3",
        "portCode", "portName", "terminalCode", "terminalName",
        "callType", "groupCallId", "arrPilot", "arrDock", "depDock", "depPilot",
        "terminalTime", "remarks", "status", "arrivalReported"
})
@Schema(name = "CoastalScheduleCall", description = "One coastal port call.")
public class CoastalScheduleCall {

    @Schema(description = "Service code.", example = "PAX")
    private String serviceCode;

    @Schema(description = "Service name.", example = "PACIFIC XPRESS")
    private String serviceName;

    @Schema(description = "Vessel code.", example = "PCX")
    private String vesselCode;

    @Schema(description = "Vessel name.", example = "PC XPRESS")
    private String vesselName;

    @Schema(description = "Vessel IMO number.", example = "12345")
    private String vesselImoNumber;

    @Schema(description = "Voyage number.", example = "045")
    private String voyageNumber;

    @Schema(description = "Proforma bound direction: W – West, E – East, N – North, S – South.", example = "N")
    private Bound pfBound;

    @Schema(description = "Voyage bound direction: W – West, E – East, N – North, S – South.", example = "N")
    private Bound bound;

    @Schema(description = "Pivot voyage number 1.", example = "612")
    private String pivotVoyage1;

    @Schema(description = "Pivot bound 1: W – West, E – East, N – North, S – South.", example = "N")
    private Bound pivotBound1;

    @Schema(description = "Pivot voyage number 2.", example = "")
    private String pivotVoyage2;

    @Schema(description = "Pivot bound 2: W – West, E – East, N – North, S – South.", example = "S")
    private Bound pivotBound2;

    @Schema(description = "Pivot voyage number 3.", example = "")
    private String pivotVoyage3;

    @Schema(description = "Pivot bound 3: W – West, E – East, N – North, S – South.", example = "E")
    private Bound pivotBound3;

    @Schema(description = "Port code.", example = "INNSA")
    private String portCode;

    @Schema(description = "Port name.", example = "NHAVA SHEVA, INDIA")
    private String portName;

    @Schema(description = "Terminal code.", example = "INNSABMCT0")
    private String terminalCode;

    @Schema(description = "Terminal name.", example = "BMCT TERMINAL")
    private String terminalName;

    @Schema(description = "Call type: C – Commercial, P – Physical.", example = "C")
    private CallType callType;

    @Schema(description = "Group call identifier.", example = "Grpcall-90142")
    private String groupCallId;

    @Schema(description = "Arrival pilot timestamp.", type = "string", format = "date-time", example = "2026-08-19T09:00:00+05:30")
    private OffsetDateTime arrPilot;

    @Schema(description = "Arrival dock timestamp.", type = "string", format = "date-time", example = "2026-08-19T11:00:00+05:30")
    private OffsetDateTime arrDock;

    @Schema(description = "Departure dock timestamp.", type = "string", format = "date-time", example = "2026-08-21T05:00:00+05:30")
    private OffsetDateTime depDock;

    @Schema(description = "Departure pilot timestamp.", type = "string", format = "date-time", example = "2026-08-21T06:00:00+05:30")
    private OffsetDateTime depPilot;

    @Schema(description = "Terminal stay time in hours.", example = "48.0")
    private Double terminalTime;

    @Schema(description = "Remarks for the port call.", example = "")
    private String remarks;

    @Schema(description = "Call status: S – Sailed/Confirmed Call, N – Normal call, D – Omitted call.", example = "S")
    private CallStatus status;


    @Schema(description = "Live schedule progress flag, passed through unchanged.", example = "Completed")
    private String statusFlag;

    @Schema(description = "Whether arrival has been reported: Y – Yes, N – No.", example = "Y")
    private YesNoFlag arrivalReported;

    public String getServiceCode() { return serviceCode; }
    public void setServiceCode(String serviceCode) { this.serviceCode = serviceCode; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getVesselCode() { return vesselCode; }
    public void setVesselCode(String vesselCode) { this.vesselCode = vesselCode; }

    public String getVesselName() { return vesselName; }
    public void setVesselName(String vesselName) { this.vesselName = vesselName; }

    public String getVesselImoNumber() { return vesselImoNumber; }
    public void setVesselImoNumber(String vesselImoNumber) { this.vesselImoNumber = vesselImoNumber; }

    public String getVoyageNumber() { return voyageNumber; }
    public void setVoyageNumber(String voyageNumber) { this.voyageNumber = voyageNumber; }

    public Bound getPfBound() { return pfBound; }
    public void setPfBound(Bound pfBound) { this.pfBound = pfBound; }

    public Bound getBound() { return bound; }
    public void setBound(Bound bound) { this.bound = bound; }

    public String getPivotVoyage1() { return pivotVoyage1; }
    public void setPivotVoyage1(String pivotVoyage1) { this.pivotVoyage1 = pivotVoyage1; }

    public Bound getPivotBound1() { return pivotBound1; }
    public void setPivotBound1(Bound pivotBound1) { this.pivotBound1 = pivotBound1; }

    public String getPivotVoyage2() { return pivotVoyage2; }
    public void setPivotVoyage2(String pivotVoyage2) { this.pivotVoyage2 = pivotVoyage2; }

    public Bound getPivotBound2() { return pivotBound2; }
    public void setPivotBound2(Bound pivotBound2) { this.pivotBound2 = pivotBound2; }

    public String getPivotVoyage3() { return pivotVoyage3; }
    public void setPivotVoyage3(String pivotVoyage3) { this.pivotVoyage3 = pivotVoyage3; }

    public Bound getPivotBound3() { return pivotBound3; }
    public void setPivotBound3(Bound pivotBound3) { this.pivotBound3 = pivotBound3; }

    public String getPortCode() { return portCode; }
    public void setPortCode(String portCode) { this.portCode = portCode; }

    public String getPortName() { return portName; }
    public void setPortName(String portName) { this.portName = portName; }

    public String getTerminalCode() { return terminalCode; }
    public void setTerminalCode(String terminalCode) { this.terminalCode = terminalCode; }

    public String getTerminalName() { return terminalName; }
    public void setTerminalName(String terminalName) { this.terminalName = terminalName; }

    public CallType getCallType() { return callType; }
    public void setCallType(CallType callType) { this.callType = callType; }

    public String getGroupCallId() { return groupCallId; }
    public void setGroupCallId(String groupCallId) { this.groupCallId = groupCallId; }

    public OffsetDateTime getArrPilot() { return arrPilot; }
    public void setArrPilot(OffsetDateTime arrPilot) { this.arrPilot = arrPilot; }

    public OffsetDateTime getArrDock() { return arrDock; }
    public void setArrDock(OffsetDateTime arrDock) { this.arrDock = arrDock; }

    public OffsetDateTime getDepDock() { return depDock; }
    public void setDepDock(OffsetDateTime depDock) { this.depDock = depDock; }

    public OffsetDateTime getDepPilot() { return depPilot; }
    public void setDepPilot(OffsetDateTime depPilot) { this.depPilot = depPilot; }

    public Double getTerminalTime() { return terminalTime; }
    public void setTerminalTime(Double terminalTime) { this.terminalTime = terminalTime; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public CallStatus getStatus() { return status; }
    public void setStatus(CallStatus status) { this.status = status; }


    public String getStatusFlag() { return statusFlag; }
    public void setStatusFlag(String statusFlag) { this.statusFlag = statusFlag; }


    public YesNoFlag getArrivalReported() { return arrivalReported; }
    public void setArrivalReported(YesNoFlag arrivalReported) { this.arrivalReported = arrivalReported; }
}
