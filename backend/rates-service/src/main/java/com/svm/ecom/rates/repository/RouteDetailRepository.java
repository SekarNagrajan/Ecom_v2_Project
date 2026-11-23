package com.svm.ecom.rates.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Loads Booking route master rows from ReportDB {@code Routedetail}
 * (same source as {@code booking-service} {@code RoutingDetailRepository}).
 */
@Repository
public class RouteDetailRepository {

    private static final Logger logger = LoggerFactory.getLogger(RouteDetailRepository.class);

    private static final String SQL_BY_ID =
            "SELECT id, origin, orgMode, BLPOL, BLPOLTer, mode, "
                    + "OpsPol1, OpsPol1Ter, OpsPol2, OpsPol2Ter, OpsPol3, OpsPol3Ter, "
                    + "OpsPol4, OpsPol4Ter, OpsPol5, OpsPol5Ter, "
                    + "BlPod, BlPodTer, delivery, DelMode, MainSvc "
                    + "FROM Routedetail (nolock) WHERE status = 'A' AND id = ?";

    private final JdbcTemplate reportJdbcTemplate;

    public RouteDetailRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    public Optional<RouteDetail> findByRouteId(Integer routeId) {
        if (routeId == null) {
            return Optional.empty();
        }
        try {
            return reportJdbcTemplate.query(
                    SQL_BY_ID,
                    ps -> ps.setInt(1, routeId),
                    rs -> rs.next() ? Optional.of(mapRow(rs)) : Optional.<RouteDetail>empty());
        } catch (Exception e) {
            logger.error("Failed to load Routedetail id={}", routeId, e);
            return Optional.empty();
        }
    }

    private RouteDetail mapRow(ResultSet rs) throws SQLException {
        RouteDetail d = new RouteDetail();
        d.setOrigin(trim(rs.getString("origin")));
        d.setOrgMode(trim(rs.getString("orgMode")));
        d.setPol(trim(rs.getString("BLPOL")));
        d.setPolTerminal(trim(rs.getString("BLPOLTer")));
        d.setPolMode(trim(rs.getString("mode")));
        d.setPod(trim(rs.getString("BlPod")));
        d.setPodTerminal(trim(rs.getString("BlPodTer")));
        d.setDelivery(trim(rs.getString("delivery")));
        d.setDelMode(trim(rs.getString("DelMode")));
        d.setMainSvc(trim(rs.getString("MainSvc")));
        d.setOpsPol1(trim(rs.getString("OpsPol1")));
        d.setOpsPol1Ter(trim(rs.getString("OpsPol1Ter")));
        d.setOpsPol2(trim(rs.getString("OpsPol2")));
        d.setOpsPol2Ter(trim(rs.getString("OpsPol2Ter")));
        d.setOpsPol3(trim(rs.getString("OpsPol3")));
        d.setOpsPol3Ter(trim(rs.getString("OpsPol3Ter")));
        d.setOpsPol4(trim(rs.getString("OpsPol4")));
        d.setOpsPol4Ter(trim(rs.getString("OpsPol4Ter")));
        d.setOpsPol5(trim(rs.getString("OpsPol5")));
        d.setOpsPol5Ter(trim(rs.getString("OpsPol5Ter")));
        return d;
    }

    private static String trim(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    /**
     * Minimal Routedetail projection needed for {@code surchargeVO} parity.
     */
    public static final class RouteDetail {
        private String origin;
        private String orgMode;
        private String pol;
        private String polTerminal;
        private String polMode;
        private String pod;
        private String podTerminal;
        private String delivery;
        private String delMode;
        private String mainSvc;
        private String opsPol1;
        private String opsPol1Ter;
        private String opsPol2;
        private String opsPol2Ter;
        private String opsPol3;
        private String opsPol3Ter;
        private String opsPol4;
        private String opsPol4Ter;
        private String opsPol5;
        private String opsPol5Ter;

        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public String getOrgMode() { return orgMode; }
        public void setOrgMode(String orgMode) { this.orgMode = orgMode; }
        public String getPol() { return pol; }
        public void setPol(String pol) { this.pol = pol; }
        public String getPolTerminal() { return polTerminal; }
        public void setPolTerminal(String polTerminal) { this.polTerminal = polTerminal; }
        public String getPolMode() { return polMode; }
        public void setPolMode(String polMode) { this.polMode = polMode; }
        public String getPod() { return pod; }
        public void setPod(String pod) { this.pod = pod; }
        public String getPodTerminal() { return podTerminal; }
        public void setPodTerminal(String podTerminal) { this.podTerminal = podTerminal; }
        public String getDelivery() { return delivery; }
        public void setDelivery(String delivery) { this.delivery = delivery; }
        public String getDelMode() { return delMode; }
        public void setDelMode(String delMode) { this.delMode = delMode; }
        public String getMainSvc() { return mainSvc; }
        public void setMainSvc(String mainSvc) { this.mainSvc = mainSvc; }
        public String getOpsPol1() { return opsPol1; }
        public void setOpsPol1(String opsPol1) { this.opsPol1 = opsPol1; }
        public String getOpsPol1Ter() { return opsPol1Ter; }
        public void setOpsPol1Ter(String opsPol1Ter) { this.opsPol1Ter = opsPol1Ter; }
        public String getOpsPol2() { return opsPol2; }
        public void setOpsPol2(String opsPol2) { this.opsPol2 = opsPol2; }
        public String getOpsPol2Ter() { return opsPol2Ter; }
        public void setOpsPol2Ter(String opsPol2Ter) { this.opsPol2Ter = opsPol2Ter; }
        public String getOpsPol3() { return opsPol3; }
        public void setOpsPol3(String opsPol3) { this.opsPol3 = opsPol3; }
        public String getOpsPol3Ter() { return opsPol3Ter; }
        public void setOpsPol3Ter(String opsPol3Ter) { this.opsPol3Ter = opsPol3Ter; }
        public String getOpsPol4() { return opsPol4; }
        public void setOpsPol4(String opsPol4) { this.opsPol4 = opsPol4; }
        public String getOpsPol4Ter() { return opsPol4Ter; }
        public void setOpsPol4Ter(String opsPol4Ter) { this.opsPol4Ter = opsPol4Ter; }
        public String getOpsPol5() { return opsPol5; }
        public void setOpsPol5(String opsPol5) { this.opsPol5 = opsPol5; }
        public String getOpsPol5Ter() { return opsPol5Ter; }
        public void setOpsPol5Ter(String opsPol5Ter) { this.opsPol5Ter = opsPol5Ter; }
    }
}
