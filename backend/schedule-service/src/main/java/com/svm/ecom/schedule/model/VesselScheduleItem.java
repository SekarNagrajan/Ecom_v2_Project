package com.svm.ecom.schedule.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.schedule.enums.TransportMode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * POJO representing a single vessel schedule item.
 * arrdate and depdate are in port local time as OffsetDateTime.
 */
@Schema(description = "Individual vessel schedule entry containing port and timing details")
public class VesselScheduleItem {

    @Schema(description = "Terminal name", example = "Jaya Container Terminal")
    @JsonProperty("terminalname")
    private String terminalName;

    @Schema(description = "Port code", example = "LKCMB")
    @JsonProperty("port")
    private String port;

    @Schema(description = "Arrival date and time in port local time (ISO 8601 with offset)", example = "2023-11-23T14:30:00+05:30")
    @JsonProperty("arrdate")
    private OffsetDateTime arrivalDate;

    @Schema(description = "Service type", example = "PAX")
    @JsonProperty("service")
    private String service;

    @Schema(description = "Vessel code", example = "P01")
    @JsonProperty("vessel")
    private String vessel;

    @Schema(description = "Bound direction (N=North, E=East, W=West, S=South)", example = "E")
    @JsonProperty("bound")
    private Bound bound;

    @Schema(description = "Full port name", example = "COLOMBO, SRI LANKA")
    @JsonProperty("portname")
    private String portName;

    @Schema(description = "Terminal code", example = "LKCMBJCT00")
    @JsonProperty("terminal")
    private String terminal;

    @Schema(description = "Departure date and time in port local time (ISO 8601 with offset)", example = "2023-11-23T19:25:00+05:30")
    @JsonProperty("depdate")
    private OffsetDateTime departureDate;

    @Schema(description = "Transport mode classification (F=Feeder, M=Mainline, B=Barge, T=Truck, R=Rail)", example = "M")
    @JsonProperty("type")
    private TransportMode type;

    @Schema(description = "Full vessel name", example = "PANDA 001")
    @JsonProperty("vesselname")
    private String vesselName;

    @Schema(description = "Voyage number", example = "511")
    @JsonProperty("voyage")
    private String voyage;

    @Schema(description = "Cut-off times from ecom_cutoffconfig; null when no config applies")
    @JsonProperty("cutoff")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private CutoffVo cutoff;

    public VesselScheduleItem() {}

    public VesselScheduleItem(String terminalName, String port, OffsetDateTime arrivalDate, String service,
                              String vessel, Bound bound, String portName, String terminal,
                              OffsetDateTime departureDate, TransportMode type, String vesselName, String voyage) {
        this.terminalName = terminalName;
        this.port = port;
        this.arrivalDate = arrivalDate;
        this.service = service;
        this.vessel = vessel;
        this.bound = bound;
        this.portName = portName;
        this.terminal = terminal;
        this.departureDate = departureDate;
        this.type = type;
        this.vesselName = vesselName;
        this.voyage = voyage;
    }

    public String getTerminalName() { return terminalName; }
    public void setTerminalName(String terminalName) { this.terminalName = terminalName; }

    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }

    public OffsetDateTime getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(OffsetDateTime arrivalDate) { this.arrivalDate = arrivalDate; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getVessel() { return vessel; }
    public void setVessel(String vessel) { this.vessel = vessel; }

    public Bound getBound() { return bound; }
    public void setBound(Bound bound) { this.bound = bound; }

    public String getPortName() { return portName; }
    public void setPortName(String portName) { this.portName = portName; }

    public String getTerminal() { return terminal; }
    public void setTerminal(String terminal) { this.terminal = terminal; }

    public OffsetDateTime getDepartureDate() { return departureDate; }
    public void setDepartureDate(OffsetDateTime departureDate) { this.departureDate = departureDate; }

    public TransportMode getType() { return type; }
    public void setType(TransportMode type) { this.type = type; }

    public String getVesselName() { return vesselName; }
    public void setVesselName(String vesselName) { this.vesselName = vesselName; }

    public String getVoyage() { return voyage; }
    public void setVoyage(String voyage) { this.voyage = voyage; }

    public CutoffVo getCutoff() { return cutoff; }
    public void setCutoff(CutoffVo cutoff) { this.cutoff = cutoff; }

    @Override
    public String toString() {
        return "VesselScheduleItem{" +
                "terminalName='" + terminalName + '\'' +
                ", port='" + port + '\'' +
                ", arrivalDate='" + arrivalDate + '\'' +
                ", service='" + service + '\'' +
                ", vessel='" + vessel + '\'' +
                ", bound='" + bound + '\'' +
                ", portName='" + portName + '\'' +
                ", terminal='" + terminal + '\'' +
                ", departureDate='" + departureDate + '\'' +
                ", type='" + type + '\'' +
                ", vesselName='" + vesselName + '\'' +
                ", voyage='" + voyage + '\'' +
                ", cutoff=" + cutoff +
                '}';
    }
}
