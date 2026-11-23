package com.svm.ecom.schedule.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.schedule.enums.PortCallType;
import com.svm.ecom.schedule.enums.TransportMode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Port pair information containing vessel and schedule details between two ports")
public class PortPair {

    @Schema(description = "Port of loading code", example = "CNNGB")
    private String pol;

    @Schema(description = "Port of discharge code", example = "ESBCN")
    private String pod;

    @Schema(description = "Port of loading name", example = "NINGBO, CHINA")
    private String pol_name;

    @Schema(description = "Port of discharge name", example = "BARCELONA, SPAIN")
    private String pod_name;

    @Schema(description = "Estimated time of departure in POL local time (ISO 8601 with offset)", example = "2026-03-02T17:00:00+08:00")
    private OffsetDateTime etd;

    @Schema(description = "Estimated time of arrival in POD local time (ISO 8601 with offset)", example = "2026-03-12T10:00:00+01:00")
    private OffsetDateTime eta;

    @Schema(description = "Service code", example = "PAX")
    private String service_code;

    @Schema(description = "Service name", example = "PANDA EXPRESS")
    private String service_name;

    @Schema(description = "Vessel code", example = "BUX")
    private String vessel_code;

    @Schema(description = "Vessel name", example = "BUXWAVE")
    private String vessel_name;

    @Schema(description = "Vessel IMO number", example = "9320427")
    private String vessel_imo;

    @Schema(description = "Vessel operator code", example = "TSH")
    private String vessel_operator;

    @Schema(description = "Vessel operator name", example = "TAILWIND SHIPPING LINE")
    private String vessel_operator_name;

    @Schema(description = "Vessel owner type", example = "Chartered In")
    private String vessel_owner_type;

    @Schema(description = "Voyage number", example = "403")
    private String voyage_number;

    @Schema(description = "Bound direction: W – West, E – East, N – North, S – South", example = "W")
    private Bound bound;

    @Schema(description = "Schedule type: T – ROAD, R – RAIL, M – MAINLINE, B – BARGE, F – FEEDER, C – COMBINED, I – ITT", example = "M")
    private TransportMode schedule_type;

    @Schema(description = "Port of loading terminal code", example = "CNNGBCTT00")
    private String pol_terminal;

    @Schema(description = "Port of discharge terminal code", example = "ESBCNHPB00")
    private String pod_terminal;

    @Schema(description = "Port of loading terminal name", example = "Ningbo Beilun Second Container Terminals Co.,LTD")
    private String pol_terminal_name;

    @Schema(description = "Port of discharge terminal name", example = "Hutchison Ports BEST")
    private String pod_terminal_name;

    @Schema(description = "Load type: L – Local, T – Transshipment", example = "L")
    @JsonAlias("loadtype")
    private PortCallType loadType;

    @Schema(description = "Discharge type: L – Local, T – Transshipment", example = "L")
    @JsonAlias("disctype")
    private PortCallType dischType;

    @Schema(description = "Group call ID same as Load Call ID", example = "GrpCall-498839")
    @JsonAlias("groupcallid")
    private String groupCallId;

    @Schema(description = "Discharge call ID", example = "GrpCall-498842")
    @JsonAlias("disch_callid")
    private String dischargeCallId;

    @Schema(description = "Load call ID", example = "GrpCall-498839")
    @JsonAlias("load_callid")
    private String loadCallId;

    @Schema(description = "Discharge bound: W – West, E – East, N – North, S – South", example = "N")
    @JsonAlias("dbound")
    private Bound dischargeBound;

    // Default constructor
    public PortPair() {}

    // Getters and Setters
    public String getPol() {
        return pol;
    }

    public void setPol(String pol) {
        this.pol = pol;
    }

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }

    public String getPol_name() {
        return pol_name;
    }

    public void setPol_name(String pol_name) {
        this.pol_name = pol_name;
    }

    public String getPod_name() {
        return pod_name;
    }

    public void setPod_name(String pod_name) {
        this.pod_name = pod_name;
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

    public String getService_code() {
        return service_code;
    }

    public void setService_code(String service_code) {
        this.service_code = service_code;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getVessel_code() {
        return vessel_code;
    }

    public void setVessel_code(String vessel_code) {
        this.vessel_code = vessel_code;
    }

    public String getVessel_name() {
        return vessel_name;
    }

    public void setVessel_name(String vessel_name) {
        this.vessel_name = vessel_name;
    }

    public String getVessel_imo() {
        return vessel_imo;
    }

    public void setVessel_imo(String vessel_imo) {
        this.vessel_imo = vessel_imo;
    }

    public String getVessel_operator() {
        return vessel_operator;
    }

    public void setVessel_operator(String vessel_operator) {
        this.vessel_operator = vessel_operator;
    }

    public String getVessel_operator_name() {
        return vessel_operator_name;
    }

    public void setVessel_operator_name(String vessel_operator_name) {
        this.vessel_operator_name = vessel_operator_name;
    }

    public String getVessel_owner_type() {
        return vessel_owner_type;
    }

    public void setVessel_owner_type(String vessel_owner_type) {
        this.vessel_owner_type = vessel_owner_type;
    }

    public String getVoyage_number() {
        return voyage_number;
    }

    public void setVoyage_number(String voyage_number) {
        this.voyage_number = voyage_number;
    }

    public Bound getBound() {
        return bound;
    }

    public void setBound(Bound bound) {
        this.bound = bound;
    }

    public TransportMode getSchedule_type() {
        return schedule_type;
    }

    @JsonSetter("schedule_type")
    public void setSchedule_type(String scheduleTypeValue) {
        if (scheduleTypeValue == null || scheduleTypeValue.trim().isEmpty()) {
            this.schedule_type = null;
            return;
        }
        String value = scheduleTypeValue.trim();
        // Map "Third Party" to Feeder
        if ("Third Party".equalsIgnoreCase(value)) {
            this.schedule_type = TransportMode.F;
            return;
        }
        // Map "Mainline" to M
        if ("Mainline".equalsIgnoreCase(value)) {
            this.schedule_type = TransportMode.M;
            return;
        }
        // For standard values (T, R, M, B, F, C, I), use the enum's fromValue
        this.schedule_type = TransportMode.fromValue(value);
    }

    // Overloaded setter for direct enum assignment (programmatic use)
    public void setSchedule_type(TransportMode schedule_type) {
        this.schedule_type = schedule_type;
    }

    public String getPol_terminal() {
        return pol_terminal;
    }

    public void setPol_terminal(String pol_terminal) {
        this.pol_terminal = pol_terminal;
    }

    public String getPod_terminal() {
        return pod_terminal;
    }

    public void setPod_terminal(String pod_terminal) {
        this.pod_terminal = pod_terminal;
    }

    public String getPol_terminal_name() {
        return pol_terminal_name;
    }

    public void setPol_terminal_name(String pol_terminal_name) {
        this.pol_terminal_name = pol_terminal_name;
    }

    public String getPod_terminal_name() {
        return pod_terminal_name;
    }

    public void setPod_terminal_name(String pod_terminal_name) {
        this.pod_terminal_name = pod_terminal_name;
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
