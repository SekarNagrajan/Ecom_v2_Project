package com.solverminds.booking.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Repository for fetching routing details from routedetail table.
 * Provides access to routing master information including main service code,
 * terminals, transit ports, and other routing configuration.
 */
@Repository("bookingRoutingDetailRepository")
public class RoutingDetailRepository {

    private static final Logger logger = LoggerFactory.getLogger(RoutingDetailRepository.class);

    private static final String SQL_GET_ROUTING_DETAIL = 
        "SELECT id, origin, orgMode, BLPOL, BLPOLTer, mode, BLPOLSvc, " +
        "OpsPol1, OpsPol1Ter, opsMode1, OpsPOLSVC1, " +
        "OpsPol2, OpsPol2Ter, opsMode2, OpsPOLSVC2, " +
        "OpsPol3, OpsPol3Ter, OpsMode3, OpsPolSvc3, " +
        "BlPod, BlPodTer, delivery, DelMode, DefRoute, MainSvc, " +
        "opsTrans1Mde, opsTrans2Mde, opsTrans3Mde, opsTrans4Mde, " +
        "OpsPol4, OpsPol4Ter, opsMode4, OpsPOLSVC4, " +
        "OpsPol5, OpsPol5Ter, opsMode5, OpsPOLSVC5, " +
        "OpsPol1LTer, OpsPol2LTer, OpsPol3LTer, mainSvc as svcCode " +
        "FROM routedetail (nolock) WHERE id = ?";

    private final JdbcTemplate reportJdbcTemplate;

    public RoutingDetailRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    /**
     * Fetches routing detail information for a given route ID.
     * 
     * @param routeId The route ID to lookup
     * @return RoutingDetail containing routing information, or empty if not found
     */
    public Optional<RoutingDetail> getRoutingDetailByRouteId(Integer routeId) {
        if (routeId == null) {
            logger.warn("Route ID is null, cannot fetch routing detail");
            return Optional.empty();
        }

        try {
            logger.debug("Fetching routing detail for route ID: {}", routeId);
            
            Optional<RoutingDetail> result = reportJdbcTemplate.query(
                SQL_GET_ROUTING_DETAIL,
                ps -> ps.setInt(1, routeId),
                rs -> {
                    if (rs.next()) {
                        return Optional.of(mapRowToRoutingDetail(rs));
                    }
                    return Optional.<RoutingDetail>empty();
                }
            );

            if (result.isPresent()) {
                logger.debug("Found routing detail for route ID: {}", routeId);
            } else {
                logger.warn("No routing detail found for route ID: {}", routeId);
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("Error fetching routing detail for route ID: " + routeId, e);
            return Optional.empty();
        }
    }

    /**
     * Maps a ResultSet row to RoutingDetail object.
     */
    private RoutingDetail mapRowToRoutingDetail(ResultSet rs) throws SQLException {
        RoutingDetail detail = new RoutingDetail();
        
        // Basic routing information
        detail.setRouteId(getIntOrNull(rs, "id"));
        detail.setOrigin(nullIfBlank(rs.getString("origin")));
        detail.setOriginMode(nullIfBlank(rs.getString("orgMode")));
        detail.setMainServiceCode(nullIfBlank(rs.getString("MainSvc")));
        detail.setSvcCode(nullIfBlank(rs.getString("svcCode")));
        detail.setDefRoute(nullIfBlank(rs.getString("DefRoute")));
        
        // POL (Port of Loading) information
        detail.setPol(nullIfBlank(rs.getString("BLPOL")));
        detail.setPolTerminal(nullIfBlank(rs.getString("BLPOLTer")));
        detail.setPolMode(nullIfBlank(rs.getString("mode")));
        detail.setPolSer(nullIfBlank(rs.getString("BLPOLSvc")));
        
        // POD (Port of Discharge) information
        detail.setPod(nullIfBlank(rs.getString("BlPod")));
        detail.setPodTerminal(nullIfBlank(rs.getString("BlPodTer")));
        detail.setPodMode(nullIfBlank(rs.getString("DelMode")));
        
        // Delivery information
        detail.setDelivery(nullIfBlank(rs.getString("delivery")));
        
        // Transit port 1
        detail.setTrans1(nullIfBlank(rs.getString("OpsPol1")));
        detail.setTrans1Ter(nullIfBlank(rs.getString("OpsPol1Ter")));
        detail.setTrans1Mode(nullIfBlank(rs.getString("opsMode1")));
        detail.setTrans1Ser(nullIfBlank(rs.getString("OpsPOLSVC1")));
        detail.setTrans1LTer(nullIfBlank(rs.getString("OpsPol1LTer")));
        
        // Transit port 2
        detail.setTrans2(nullIfBlank(rs.getString("OpsPol2")));
        detail.setTrans2Ter(nullIfBlank(rs.getString("OpsPol2Ter")));
        detail.setTrans2Mode(nullIfBlank(rs.getString("opsMode2")));
        detail.setTrans2Ser(nullIfBlank(rs.getString("OpsPOLSVC2")));
        detail.setTrans2LTer(nullIfBlank(rs.getString("OpsPol2LTer")));
        
        // Transit port 3
        detail.setTrans3(nullIfBlank(rs.getString("OpsPol3")));
        detail.setTrans3Ter(nullIfBlank(rs.getString("OpsPol3Ter")));
        detail.setTrans3Mode(nullIfBlank(rs.getString("OpsMode3")));
        detail.setTrans3Ser(nullIfBlank(rs.getString("OpsPolSvc3")));
        detail.setTrans3LTer(nullIfBlank(rs.getString("OpsPol3LTer")));
        
        // Transit port 4
        detail.setTrans4(nullIfBlank(rs.getString("OpsPol4")));
        detail.setTrans4Ter(nullIfBlank(rs.getString("OpsPol4Ter")));
        detail.setTrans4Mode(nullIfBlank(rs.getString("opsMode4")));
        detail.setTrans4Ser(nullIfBlank(rs.getString("OpsPOLSVC4")));
        
        // Transit port 5
        detail.setTrans5(nullIfBlank(rs.getString("OpsPol5")));
        detail.setTrans5Ter(nullIfBlank(rs.getString("OpsPol5Ter")));
        detail.setTrans5Mode(nullIfBlank(rs.getString("opsMode5")));
        detail.setTrans5Ser(nullIfBlank(rs.getString("OpsPOLSVC5")));
        
        // Operations transit modes
        detail.setOpsTrans1Mde(nullIfBlank(rs.getString("opsTrans1Mde")));
        detail.setOpsTrans2Mde(nullIfBlank(rs.getString("opsTrans2Mde")));
        detail.setOpsTrans3Mde(nullIfBlank(rs.getString("opsTrans3Mde")));
        detail.setOpsTrans4Mde(nullIfBlank(rs.getString("opsTrans4Mde")));
        
        return detail;
    }
    
    /**
     * Helper method to safely get Integer from ResultSet, returning null if value is null.
     */
    private Integer getIntOrNull(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }

    /**
     * Helper method to return null for blank strings.
     */
    private static String nullIfBlank(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    /**
     * Data transfer object for routing detail information.
     * Contains all routing information from the routedetail table.
     */
    public static class RoutingDetail {
        // Basic routing information
        private Integer routeId;
        private String origin;
        private String originMode;
        private String mainServiceCode;
        private String svcCode;
        private String defRoute;
        
        // POL (Port of Loading) information
        private String pol;
        private String polTerminal;
        private String polMode;
        private String polSer;
        
        // POD (Port of Discharge) information
        private String pod;
        private String podTerminal;
        private String podMode;
        
        // Delivery information
        private String delivery;
        
        // Transit port 1
        private String trans1;
        private String trans1Ter;
        private String trans1Mode;
        private String trans1Ser;
        private String trans1LTer;
        
        // Transit port 2
        private String trans2;
        private String trans2Ter;
        private String trans2Mode;
        private String trans2Ser;
        private String trans2LTer;
        
        // Transit port 3
        private String trans3;
        private String trans3Ter;
        private String trans3Mode;
        private String trans3Ser;
        private String trans3LTer;
        
        // Transit port 4
        private String trans4;
        private String trans4Ter;
        private String trans4Mode;
        private String trans4Ser;
        
        // Transit port 5
        private String trans5;
        private String trans5Ter;
        private String trans5Mode;
        private String trans5Ser;
        
        // Operations transit modes
        private String opsTrans1Mde;
        private String opsTrans2Mde;
        private String opsTrans3Mde;
        private String opsTrans4Mde;

        // Getters and setters
        public Integer getRouteId() {
            return routeId;
        }

        public void setRouteId(Integer routeId) {
            this.routeId = routeId;
        }

        public String getMainServiceCode() {
            return mainServiceCode;
        }

        public void setMainServiceCode(String mainServiceCode) {
            this.mainServiceCode = mainServiceCode;
        }

        public String getPolTerminal() {
            return polTerminal;
        }

        public void setPolTerminal(String polTerminal) {
            this.polTerminal = polTerminal;
        }

        public String getPodTerminal() {
            return podTerminal;
        }

        public void setPodTerminal(String podTerminal) {
            this.podTerminal = podTerminal;
        }

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

        public String getPolSer() {
            return polSer;
        }

        public void setPolSer(String polSer) {
            this.polSer = polSer;
        }

        public String getPolMode() {
            return polMode;
        }

        public void setPolMode(String polMode) {
            this.polMode = polMode;
        }

        public String getPodMode() {
            return podMode;
        }

        public void setPodMode(String podMode) {
            this.podMode = podMode;
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

        public String getSvcCode() {
            return svcCode;
        }

        public void setSvcCode(String svcCode) {
            this.svcCode = svcCode;
        }

        public String getOriginMode() {
            return originMode;
        }

        public void setOriginMode(String originMode) {
            this.originMode = originMode;
        }

        public String getDefRoute() {
            return defRoute;
        }

        public void setDefRoute(String defRoute) {
            this.defRoute = defRoute;
        }

        // Transit port 1 getters and setters
        public String getTrans1() {
            return trans1;
        }

        public void setTrans1(String trans1) {
            this.trans1 = trans1;
        }

        public String getTrans1Ter() {
            return trans1Ter;
        }

        public void setTrans1Ter(String trans1Ter) {
            this.trans1Ter = trans1Ter;
        }

        public String getTrans1Mode() {
            return trans1Mode;
        }

        public void setTrans1Mode(String trans1Mode) {
            this.trans1Mode = trans1Mode;
        }

        public String getTrans1Ser() {
            return trans1Ser;
        }

        public void setTrans1Ser(String trans1Ser) {
            this.trans1Ser = trans1Ser;
        }

        public String getTrans1LTer() {
            return trans1LTer;
        }

        public void setTrans1LTer(String trans1LTer) {
            this.trans1LTer = trans1LTer;
        }

        // Transit port 2 getters and setters
        public String getTrans2() {
            return trans2;
        }

        public void setTrans2(String trans2) {
            this.trans2 = trans2;
        }

        public String getTrans2Ter() {
            return trans2Ter;
        }

        public void setTrans2Ter(String trans2Ter) {
            this.trans2Ter = trans2Ter;
        }

        public String getTrans2Mode() {
            return trans2Mode;
        }

        public void setTrans2Mode(String trans2Mode) {
            this.trans2Mode = trans2Mode;
        }

        public String getTrans2Ser() {
            return trans2Ser;
        }

        public void setTrans2Ser(String trans2Ser) {
            this.trans2Ser = trans2Ser;
        }

        public String getTrans2LTer() {
            return trans2LTer;
        }

        public void setTrans2LTer(String trans2LTer) {
            this.trans2LTer = trans2LTer;
        }

        // Transit port 3 getters and setters
        public String getTrans3() {
            return trans3;
        }

        public void setTrans3(String trans3) {
            this.trans3 = trans3;
        }

        public String getTrans3Ter() {
            return trans3Ter;
        }

        public void setTrans3Ter(String trans3Ter) {
            this.trans3Ter = trans3Ter;
        }

        public String getTrans3Mode() {
            return trans3Mode;
        }

        public void setTrans3Mode(String trans3Mode) {
            this.trans3Mode = trans3Mode;
        }

        public String getTrans3Ser() {
            return trans3Ser;
        }

        public void setTrans3Ser(String trans3Ser) {
            this.trans3Ser = trans3Ser;
        }

        public String getTrans3LTer() {
            return trans3LTer;
        }

        public void setTrans3LTer(String trans3LTer) {
            this.trans3LTer = trans3LTer;
        }

        // Transit port 4 getters and setters
        public String getTrans4() {
            return trans4;
        }

        public void setTrans4(String trans4) {
            this.trans4 = trans4;
        }

        public String getTrans4Ter() {
            return trans4Ter;
        }

        public void setTrans4Ter(String trans4Ter) {
            this.trans4Ter = trans4Ter;
        }

        public String getTrans4Mode() {
            return trans4Mode;
        }

        public void setTrans4Mode(String trans4Mode) {
            this.trans4Mode = trans4Mode;
        }

        public String getTrans4Ser() {
            return trans4Ser;
        }

        public void setTrans4Ser(String trans4Ser) {
            this.trans4Ser = trans4Ser;
        }

        // Transit port 5 getters and setters
        public String getTrans5() {
            return trans5;
        }

        public void setTrans5(String trans5) {
            this.trans5 = trans5;
        }

        public String getTrans5Ter() {
            return trans5Ter;
        }

        public void setTrans5Ter(String trans5Ter) {
            this.trans5Ter = trans5Ter;
        }

        public String getTrans5Mode() {
            return trans5Mode;
        }

        public void setTrans5Mode(String trans5Mode) {
            this.trans5Mode = trans5Mode;
        }

        public String getTrans5Ser() {
            return trans5Ser;
        }

        public void setTrans5Ser(String trans5Ser) {
            this.trans5Ser = trans5Ser;
        }

        // Operations transit modes getters and setters
        public String getOpsTrans1Mde() {
            return opsTrans1Mde;
        }

        public void setOpsTrans1Mde(String opsTrans1Mde) {
            this.opsTrans1Mde = opsTrans1Mde;
        }

        public String getOpsTrans2Mde() {
            return opsTrans2Mde;
        }

        public void setOpsTrans2Mde(String opsTrans2Mde) {
            this.opsTrans2Mde = opsTrans2Mde;
        }

        public String getOpsTrans3Mde() {
            return opsTrans3Mde;
        }

        public void setOpsTrans3Mde(String opsTrans3Mde) {
            this.opsTrans3Mde = opsTrans3Mde;
        }

        public String getOpsTrans4Mde() {
            return opsTrans4Mde;
        }

        public void setOpsTrans4Mde(String opsTrans4Mde) {
            this.opsTrans4Mde = opsTrans4Mde;
        }
    }
}

