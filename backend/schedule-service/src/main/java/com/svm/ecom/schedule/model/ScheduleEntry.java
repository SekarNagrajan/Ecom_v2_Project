package com.svm.ecom.schedule.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.schedule.enums.SchLegType;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Schedule entry containing port pairs and complete voyage information")
public class ScheduleEntry {

    @Schema(description = "List of port pairs in the voyage")
    private List<PortPair> portpair;

    @Schema(description = "Origin port code", example = "CNNGB")
    private String origin;

    @Schema(description = "Delivery port code", example = "NLRTM")
    private String delivery;

    @Schema(description = "Origin port name", example = "NINGBO, CHINA")
    private String origin_name;

    @Schema(description = "Delivery port name", example = "ROTTERDAM, NETHERLANDS")
    private String delivery_name;

    @Schema(description = "Estimated time of departure from origin in port local time (ISO 8601 with offset)", example = "2026-03-02T17:00:00+08:00")
    private OffsetDateTime etd;

    @Schema(description = "Estimated time of arrival at delivery in port local time (ISO 8601 with offset)", example = "2026-03-22T18:18:00+02:00")
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

    @Schema(description = "Bound direction (N=North, E=East, W=West, S=South)", example = "W")
    private Bound bound;

    @Schema(description = "Leg type: Single (direct route) or Multiple (with transshipments)", implementation = SchLegType.class)
    private SchLegType leg_type;

    @Schema(description = "Default route flag", example = "Y")
    private String default_route;

    @Schema(description = "Transit time in days", example = "44")
    private Integer transit_time;

    @Schema(description = "Load call ID", example = "GrpCall-5499")
    private String load_callid;

    @Schema(description = "Discharge call ID", example = "GrpCall-5449")
    private String disch_callid;

    @Schema(description = "Default route order", example = "1")
    private Integer default_route_order;

    @Schema(description = "Inland flag", example = "Y")
    private String inland_flag;

    @Schema(description = "Transshipment count", example = "1")
    private Integer transshipment_count;

    @Schema(description = "Source port name", example = "NINGBO, CHINA")
    private String sourcePortName;

    @Schema(description = "Destination port name", example = "ROTTERDAM, NETHERLANDS")
    private String destinationPortName;

    @Schema(description = "Source country", example = "CHINA")
    private String sourceCountry;

    @Schema(description = "Destination country", example = "NETHERLANDS")
    private String destinationCountry;

    @Schema(description = "Number of transshipments", example = "1")
    private Integer noOfTransshipMent;

    @Schema(description = "Feeder involved flag", example = "false")
    private Boolean feederInvolved;

    @Schema(description = "Secondary flag 1", example = "N")
    private String secondary1;

    @Schema(description = "Secondary flag 2", example = "N")
    private String secondary2;

    @Schema(description = "Route Id", example = "1890298")
    private Integer routeid;

    // Hidden field - accepted from external API but not included in response or Swagger
    @Schema(hidden = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String advertisedRoute;

    @Schema(description = "Cutoff information containing document, VGM, and container closing times")
    private Cutoff cutoff = new Cutoff();

    // Default constructor
    public ScheduleEntry() {}

    // Getters and Setters
    public List<PortPair> getPortpair() {
        return portpair;
    }

    public void setPortpair(List<PortPair> portpair) {
        this.portpair = portpair;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getOrigin_name() {
        return origin_name;
    }

    public void setOrigin_name(String origin_name) {
        this.origin_name = origin_name;
    }

    public String getDelivery_name() {
        return delivery_name;
    }

    public void setDelivery_name(String delivery_name) {
        this.delivery_name = delivery_name;
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

    public SchLegType getLeg_type() {
        return leg_type;
    }

    public void setLeg_type(SchLegType leg_type) {
        this.leg_type = leg_type;
    }

    public String getDefault_route() {
        return default_route;
    }

    public void setDefault_route(String default_route) {
        this.default_route = default_route;
    }

    public Integer getTransit_time() {
        return transit_time;
    }

    public void setTransit_time(Integer transit_time) {
        this.transit_time = transit_time;
    }

    public String getLoad_callid() {
        return load_callid;
    }

    public void setLoad_callid(String load_callid) {
        this.load_callid = load_callid;
    }

    public String getDisch_callid() {
        return disch_callid;
    }

    public void setDisch_callid(String disch_callid) {
        this.disch_callid = disch_callid;
    }

    public Integer getDefault_route_order() {
        return default_route_order;
    }

    public void setDefault_route_order(Integer default_route_order) {
        this.default_route_order = default_route_order;
    }

    public String getInland_flag() {
        return inland_flag;
    }

    public void setInland_flag(String inland_flag) {
        this.inland_flag = inland_flag;
    }

    public Integer getTransshipment_count() {
        return transshipment_count;
    }

    public void setTransshipment_count(Integer transshipment_count) {
        this.transshipment_count = transshipment_count;
    }

    public String getSourcePortName() {
        return sourcePortName;
    }

    public void setSourcePortName(String sourcePortName) {
        this.sourcePortName = sourcePortName;
    }

    public String getDestinationPortName() {
        return destinationPortName;
    }

    public void setDestinationPortName(String destinationPortName) {
        this.destinationPortName = destinationPortName;
    }

    public String getSourceCountry() {
        return sourceCountry;
    }

    public void setSourceCountry(String sourceCountry) {
        this.sourceCountry = sourceCountry;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public Integer getNoOfTransshipMent() {
        return noOfTransshipMent;
    }

    public void setNoOfTransshipMent(Integer noOfTransshipMent) {
        this.noOfTransshipMent = noOfTransshipMent;
    }

    public Boolean getFeederInvolved() {
        return feederInvolved;
    }

    public void setFeederInvolved(Boolean feederInvolved) {
        this.feederInvolved = feederInvolved;
    }

    public String getSecondary1() {
        return secondary1;
    }

    public void setSecondary1(String secondary1) {
        this.secondary1 = secondary1;
    }

    public String getSecondary2() {
        return secondary2;
    }

    public void setSecondary2(String secondary2) {
        this.secondary2 = secondary2;
    }

    public Integer getRouteid() {
        return routeid;
    }

    public void setRouteid(Integer routeid) {
        this.routeid = routeid;
    }

    public String getAdvertisedRoute() {
        return advertisedRoute;
    }

    public void setAdvertisedRoute(String advertisedRoute) {
        this.advertisedRoute = advertisedRoute;
    }

    public Cutoff getCutoff() {
        return cutoff;
    }

    public void setCutoff(Cutoff cutoff) {
        this.cutoff = cutoff;
    }
}
