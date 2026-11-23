package com.svm.ecom.master.repository;

import com.svm.ecom.master.model.VesselView;
import com.svm.ecom.master.model.VesselViewImpl;
import com.svm.ecom.master.model.PortView;
import com.svm.ecom.master.model.PortViewImpl;
import com.svm.ecom.master.model.CountryView;
import com.svm.ecom.master.model.CountryViewImpl;
import com.svm.ecom.master.model.PackageView;
import com.svm.ecom.master.model.PackageViewImpl;
import com.svm.ecom.master.model.EquipmentView;
import com.svm.ecom.master.model.EquipmentViewImpl;
import com.svm.ecom.master.model.TerminalView;
import com.svm.ecom.master.model.TerminalViewImpl;
import com.svm.ecom.master.model.CommodityView;
import com.svm.ecom.master.model.CommodityViewImpl;
import com.svm.ecom.master.model.HSView;
import com.svm.ecom.master.model.HSViewImpl;
import com.svm.ecom.master.model.DGView;
import com.svm.ecom.master.model.DGViewImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository class for master data queries
 * Uses native SQL queries with interface-based projections
 */
@Repository
public class MasterRepository {

    private final JdbcTemplate reportJdbcTemplate;

    public MasterRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

        /**
         * Find active vessels excluding test vessels (codes starting with 'xx')
         * 
         * @return List of active vessels with code and name
         */
        public List<VesselView> findActiveVessels() {
                String sql = "SELECT DISTINCT " +
                        "vesselcode, vesselname, imo, built, buildmonth, status, " +
                        "grt, loa, beam, sdraft, dwt, plugs, capacityteu, flag, callsign " +
                        "FROM vesselmaster (NOLOCK) " +
                        "WHERE status = 'A' " +
                        "AND vesselcode NOT LIKE 'xx%' " +
                        "ORDER BY vesselname";

                return reportJdbcTemplate.query(sql, (rs, rowNum) -> {
                        String dbStatus = rs.getString("status");
                        String apiStatus = normalizeVesselStatus(dbStatus);
                        return new VesselViewImpl(
                                trim(rs.getString("vesselcode")),
                                trim(rs.getString("vesselname")),
                                trim(rs.getString("imo")),
                                trim(rs.getString("built")),
                                trim(rs.getString("buildmonth")),
                                apiStatus,
                                toInteger(rs.getObject("grt")),
                                toDouble(rs.getObject("loa")),
                                toDouble(rs.getObject("beam")),
                                toDouble(rs.getObject("sdraft")),
                                toDouble(rs.getObject("dwt")),
                                toInteger(rs.getObject("plugs")),
                                toInteger(rs.getObject("capacityteu")),
                                trim(rs.getString("flag")),
                                trim(rs.getString("callsign"))
                        );
                });
        }

        private static String trim(String v) {
                return v != null ? v.trim() : null;
        }

        private static Integer toInteger(Object v) {
                if (v == null) return null;
                if (v instanceof Integer) return (Integer) v;
                if (v instanceof Number) return ((Number) v).intValue();
                try {
                        String s = String.valueOf(v).trim();
                        return s.isEmpty() ? null : Integer.valueOf(s);
                } catch (Exception e) {
                        return null;
                }
        }

        private static Double toDouble(Object v) {
                if (v == null) return null;
                if (v instanceof Double) return (Double) v;
                if (v instanceof Number) return ((Number) v).doubleValue();
                try {
                        String s = String.valueOf(v).trim();
                        return s.isEmpty() ? null : Double.valueOf(s);
                } catch (Exception e) {
                        return null;
                }
        }

        /**
         * Normalizes typical 1-char MSSQL vessel status codes to more descriptive values,
         * while remaining backward compatible (unknown values pass through).
         */
        private static String normalizeVesselStatus(String dbStatus) {
                if (dbStatus == null) return null;
                String s = dbStatus.trim();
                if (s.length() == 1) {
                        if ("A".equalsIgnoreCase(s)) return "ACTIVE";
                        if ("I".equalsIgnoreCase(s)) return "INACTIVE";
                }
                return s;
        }

        /**
         * Find active ports with timezone and country info, excluding test ports (codes starting with 'xx')
         * 
         * @return List of active ports with code, name, type, coordinates, timezone offset, and country
         */
        @SuppressWarnings("unchecked")
        public List<PortView> findActivePorts() {
                String sql = "SELECT p.PortCode, p.PortName, p.portType, p.Latitude, p.Longitude, " +
                                "t.timeOffSet, p.CountryCode, p.eur " +
                                "FROM Ports p (NOLOCK) " +
                                "INNER JOIN timezone t (NOLOCK) ON t.portcode = p.portcode " +
                                "WHERE p.status = 'A' " +
                                "AND p.portcode NOT LIKE 'xx%' " +
                                "AND t.status = 'A' " +
                                "AND t.portcode IS NOT NULL " +
                                "ORDER BY p.PortName";
                return reportJdbcTemplate.query(sql, (rs, rowNum) -> new PortViewImpl(
                        String.valueOf(rs.getObject(1)),
                        String.valueOf(rs.getObject(2)),
                        rs.getObject(3) != null ? String.valueOf(rs.getObject(3)) : null,
                        rs.getObject(4) != null ? ((Number) rs.getObject(4)).doubleValue() : null,
                        rs.getObject(5) != null ? ((Number) rs.getObject(5)).doubleValue() : null,
                        rs.getObject(6) != null ? String.valueOf(rs.getObject(6)) : null,
                        rs.getObject(7) != null ? String.valueOf(rs.getObject(7)) : null,
                        rs.getObject(8) != null ? String.valueOf(rs.getObject(8)) : null
                ));
        }

        /**
         * Find active countries excluding test countries (codes starting with 'xx')
         * 
         * @return List of active countries with code, name, and type
         */
        @SuppressWarnings("unchecked")
        public List<CountryView> findActiveCountries() {
                String sql = "SELECT CountryCode, CountryName " +
                                "FROM country (NOLOCK) " +
                                "WHERE status = 'A' " +
                                "AND countrycode NOT LIKE 'xx%' " +
                                "ORDER BY CountryName";
                return reportJdbcTemplate.query(sql, (rs, rowNum) -> new CountryViewImpl(
                        String.valueOf(rs.getObject(1)),
                        String.valueOf(rs.getObject(2))
                ));
        }

        /**
         * Find active packages excluding test packages (codes starting with 'xx')
         * 
         * @return List of active packages with code, name
         */
        @SuppressWarnings("unchecked")
        public List<PackageView> findActivePackages() {
                String sql = "SELECT PackageCode, Description " +
                                "FROM package (NOLOCK) " +
                                "WHERE status = 'A' " +
                                "AND packagecode NOT LIKE 'xx%' " +
                                "ORDER BY Description";
                return reportJdbcTemplate.query(sql, (rs, rowNum) -> new PackageViewImpl(
                        String.valueOf(rs.getObject(1)),
                        String.valueOf(rs.getObject(2))
                ));
        }

        /**
         * Find active equipment excluding test equipment (codes starting with 'xx')
         * 
         * @return List of active equipment with code, name
         */
        @SuppressWarnings("unchecked")
        public List<EquipmentView> findActiveEquipment() {
                String sql = "SELECT Equiptype, equipdesc " +
                                "FROM equipments (NOLOCK) " +
                                "WHERE status = 'A' " +
                                "AND Equiptype NOT LIKE 'xx%' " +
                                "ORDER BY Equiptype";
                return reportJdbcTemplate.query(sql, (rs, rowNum) -> new EquipmentViewImpl(
                        String.valueOf(rs.getObject(1)),
                        String.valueOf(rs.getObject(2))
                ));
        }

        /**
         * Find active terminal excluding test terminal (codes starting with 'xx')
         * 
         * @return List of active terminal with code, name
         */
        @SuppressWarnings("unchecked")
        public List<TerminalView> findActiveTerminal() {
                String sql = "SELECT terminalcode,terminalname  " +
                                "FROM terminalanddepot (NOLOCK) " +
                                "WHERE terminalname NOT LIKE 'xx%' " +
                                "ORDER BY terminalname";
                return reportJdbcTemplate.query(sql, (rs, rowNum) -> new TerminalViewImpl(
                        String.valueOf(rs.getObject(1)),
                        String.valueOf(rs.getObject(2))
                ));
        }

        /**
         * Find active commodity excluding test commodity (codes starting with 'xx')
         * 
         * @return List of active commodity with code, name
         */
        @SuppressWarnings("unchecked")
        public List<CommodityView> findActiveCommodity() {
                String sql = "select comdtycode as commoditycode,comdtyname as commodityname " +
                "from commodity(NOLOCK) where status='A' and parentCode  = '0'";
                return reportJdbcTemplate.query(sql, (rs, rowNum) -> new CommodityViewImpl(
                        String.valueOf(rs.getObject(1)),
                        String.valueOf(rs.getObject(2))
                ));
        }

        /**
         * Find active HS excluding test HS (codes starting with 'xx')
         * 
         * @return List of active HS with code, name
         */
        @SuppressWarnings("unchecked")
        public List<HSView> findActiveHS() {
                String sql = "select comdtycode as hscode,comdtyname as hsname,grpcode as commoditygroup " +
                "from commodity(NOLOCK) where status='A' and parentCode  <> '0'";
                return reportJdbcTemplate.query(sql, (rs, rowNum) -> new HSViewImpl(
                        String.valueOf(rs.getObject(1)),
                        String.valueOf(rs.getObject(2)),
                        String.valueOf(rs.getObject(3))
                ));
        }

        /**
         * Find active DG excluding test DG (codes starting with 'xx')
         * 
         * @return List of active DG with code, name
         */
        @SuppressWarnings("unchecked")
        public List<DGView> findActiveDG() {
                /*String sql = "select unno,class,PSN as shippingname from subs(nolock) where state='A'"; */
                String sql="select unno,imdgclass as class,imdgname as shippingname from imdgclass(nolock) where status = 'A'";
                return reportJdbcTemplate.query(sql, (rs, rowNum) -> new DGViewImpl(
                        String.valueOf(rs.getObject(1)),
                        String.valueOf(rs.getObject(2)),
                        String.valueOf(rs.getObject(3))
                ));
        }
}