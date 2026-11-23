package com.solverminds.booking.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Repository for querying master data from reportDB for cache loading.
 * Provides bulk load methods for validation data:
 * - Port codes
 * - Customer codes
 * - Country codes
 * - Commodity codes
 * - Package codes
 * - Terminal codes
 * - Service codes
 * - Vessel codes
 */
@Repository
public class MasterDataRepository {

    private static final Logger logger = LoggerFactory.getLogger(MasterDataRepository.class);

    // SQL queries for loading master data
    private static final String SQL_GET_ALL_PORTS = 
        "SELECT portcode FROM ports (nolock) WHERE status='A' AND portcode IS NOT NULL";

    private static final String SQL_GET_ALL_PORT_NAMES = 
        "SELECT portcode, portname FROM ports (nolock) WHERE status='A' AND portcode IS NOT NULL";

    private static final String SQL_GET_ALL_CUSTOMERS = 
        "SELECT custcode FROM customermaster (nolock) WHERE status='A' AND custcode IS NOT NULL";

    private static final String SQL_GET_ALL_COUNTRIES = 
        "SELECT countrycode FROM country (nolock) WHERE status='A' AND countrycode IS NOT NULL";

    private static final String SQL_GET_ALL_COMMODITIES =
        "SELECT comdtycode, grpcode FROM commodity (nolock) WHERE status='A' AND comdtycode IS NOT NULL";

    private static final String SQL_GET_ALL_PACKAGES = 
        "SELECT packagecode FROM package (nolock) WHERE status='A' AND packagecode IS NOT NULL";

    private static final String SQL_GET_ALL_TERMINALS = 
        "SELECT terminalcode FROM terminals (nolock) WHERE status='A' AND terminalcode IS NOT NULL";

    private static final String SQL_GET_ALL_SERVICES = 
        "SELECT servicecode FROM service (nolock) WHERE status='Active' AND servicecode IS NOT NULL";

    private static final String SQL_GET_ALL_VESSELS = 
        "SELECT vesselcode FROM vesselmaster (nolock) WHERE status='A' AND vesselcode IS NOT NULL";

    private static final String SQL_GET_ALL_TIMEZONES = 
        "SELECT portcode, timeOffSet FROM timezone (nolock) WHERE status='A' AND portcode IS NOT NULL";

    /** Same source as /api/v1/master/dglist: imdgclass table; alias imdgclass AS class for ResultSet label "class". */
    private static final String SQL_GET_ALL_DANGEROUS_GOODS = 
        "SELECT unno, imdgclass AS class FROM imdgclass (nolock) WHERE status = 'A' AND unno IS NOT NULL";

    private static final String SQL_GET_ALL_ACTIVE_ROUTES = 
        "SELECT id FROM routedetail (nolock) WHERE status = 'A' AND GETDATE() BETWEEN fromdate AND todate AND id IS NOT NULL";

    private final JdbcTemplate reportJdbcTemplate;

    public MasterDataRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    /**
     * Fetches all active port codes for cache loading.
     * @return Set of valid port codes (uppercase)
     */
    public Set<String> getAllActivePortCodes() {
        Set<String> result = new HashSet<>();

        try {
            logger.debug("Fetching all active port codes for cache");

            List<String> ports = reportJdbcTemplate.query(
                SQL_GET_ALL_PORTS,
                (rs, rowNum) -> {
                    String code = rs.getString("portcode");
                    return code != null ? code.trim().toUpperCase() : null;
                }
            );

            for (String port : ports) {
                if (port != null && !port.isEmpty()) {
                    result.add(port);
                }
            }

            logger.info("Loaded {} active port codes", result.size());

        } catch (Exception e) {
            logger.error("Error fetching all port codes", e);
        }

        return result;
    }

    /**
     * Fetches all active port codes with their names for cache loading.
     * @return Map of port codes to port names (uppercase keys)
     */
    public Map<String, String> getAllActivePortNames() {
        Map<String, String> result = new HashMap<>();

        try {
            logger.debug("Fetching all active port names for cache");

            reportJdbcTemplate.query(
                SQL_GET_ALL_PORT_NAMES,
                (rs) -> {
                    String portCode = rs.getString("portcode");
                    String portName = rs.getString("portname");
                    if (portCode != null && !portCode.trim().isEmpty()) {
                        result.put(portCode.trim().toUpperCase(), portName != null ? portName.trim() : null);
                    }
                }
            );

            logger.info("Loaded {} active port names", result.size());

        } catch (Exception e) {
            logger.error("Error fetching all port names", e);
        }

        return result;
    }

    /**
     * Fetches all active customer codes for cache loading.
     * @return Set of valid customer codes (uppercase)
     */
    public Set<String> getAllActiveCustomerCodes() {
        Set<String> result = new HashSet<>();

        try {
            logger.debug("Fetching all active customer codes for cache");

            List<String> customers = reportJdbcTemplate.query(
                SQL_GET_ALL_CUSTOMERS,
                (rs, rowNum) -> {
                    String code = rs.getString("custcode");
                    return code != null ? code.trim().toUpperCase() : null;
                }
            );

            for (String customer : customers) {
                if (customer != null && !customer.isEmpty()) {
                    result.add(customer);
                }
            }

            logger.info("Loaded {} active customer codes", result.size());

        } catch (Exception e) {
            logger.error("Error fetching all customer codes", e);
        }

        return result;
    }

    /**
     * Fetches all active country codes for cache loading.
     * @return Set of valid country codes (uppercase)
     */
    public Set<String> getAllActiveCountryCodes() {
        Set<String> result = new HashSet<>();

        try {
            logger.debug("Fetching all active country codes for cache");

            List<String> countries = reportJdbcTemplate.query(
                SQL_GET_ALL_COUNTRIES,
                (rs, rowNum) -> {
                    String code = rs.getString("countrycode");
                    return code != null ? code.trim().toUpperCase() : null;
                }
            );

            for (String country : countries) {
                if (country != null && !country.isEmpty()) {
                    result.add(country);
                }
            }

            logger.info("Loaded {} active country codes", result.size());

        } catch (Exception e) {
            logger.error("Error fetching all country codes", e);
        }

        return result;
    }

    /**
     * Loads active commodity rows: distinct {@code comdtycode} values and mapping
     * {@code comdtycode} (HS commodity) → distinct {@code grpcode} (commodity group), trim + uppercase.
     * Rows with null/blank {@code grpcode} still contribute the {@code comdtycode} to {@code validCommodityCodesOut}.
     */
    public void populateCommodityMasterData(Set<String> validCommodityCodesOut, Map<String, Set<String>> hsCodeToGroupCodesOut) {
        validCommodityCodesOut.clear();
        hsCodeToGroupCodesOut.clear();
        try {
            logger.debug("Fetching commodity master data for cache");
            reportJdbcTemplate.query(SQL_GET_ALL_COMMODITIES, (ResultSetExtractor<Void>) rs -> {
                while (rs.next()) {
                    String code = rs.getString("comdtycode");
                    if (code == null) {
                        continue;
                    }
                    String c = code.trim().toUpperCase();
                    if (c.isEmpty()) {
                        continue;
                    }
                    validCommodityCodesOut.add(c);
                    String grp = rs.getString("grpcode");
                    if (grp != null && !grp.trim().isEmpty()) {
                        hsCodeToGroupCodesOut.computeIfAbsent(c, k -> new HashSet<>()).add(grp.trim().toUpperCase());
                    }
                }
                return null;
            });
            logger.info("Loaded {} active commodity codes, {} hsCode keys with group mappings",
                    validCommodityCodesOut.size(), hsCodeToGroupCodesOut.size());
        } catch (Exception e) {
            logger.error("Error fetching commodity master data", e);
        }
    }

    /**
     * Fetches all active package codes for cache loading.
     * @return Set of valid package codes (uppercase)
     */
    public Set<String> getAllActivePackageCodes() {
        Set<String> result = new HashSet<>();

        try {
            logger.debug("Fetching all active package codes for cache");

            List<String> packages = reportJdbcTemplate.query(
                SQL_GET_ALL_PACKAGES,
                (rs, rowNum) -> {
                    String code = rs.getString("packagecode");
                    return code != null ? code.trim().toUpperCase() : null;
                }
            );

            for (String pkg : packages) {
                if (pkg != null && !pkg.isEmpty()) {
                    result.add(pkg);
                }
            }

            logger.info("Loaded {} active package codes", result.size());

        } catch (Exception e) {
            logger.error("Error fetching all package codes", e);
        }

        return result;
    }

    /**
     * Fetches all active terminal codes for cache loading.
     * @return Set of valid terminal codes (uppercase)
     */
    public Set<String> getAllActiveTerminalCodes() {
        Set<String> result = new HashSet<>();

        try {
            logger.debug("Fetching all active terminal codes for cache");

            List<String> terminals = reportJdbcTemplate.query(
                SQL_GET_ALL_TERMINALS,
                (rs, rowNum) -> {
                    String code = rs.getString("terminalcode");
                    return code != null ? code.trim().toUpperCase() : null;
                }
            );

            for (String terminal : terminals) {
                if (terminal != null && !terminal.isEmpty()) {
                    result.add(terminal);
                }
            }

            logger.info("Loaded {} active terminal codes", result.size());

        } catch (Exception e) {
            logger.error("Error fetching all terminal codes", e);
        }

        return result;
    }

    /**
     * Fetches all active service codes for cache loading.
     * @return Set of valid service codes (uppercase)
     */
    public Set<String> getAllActiveServiceCodes() {
        Set<String> result = new HashSet<>();

        try {
            logger.debug("Fetching all active service codes for cache");

            List<String> services = reportJdbcTemplate.query(
                SQL_GET_ALL_SERVICES,
                (rs, rowNum) -> {
                    String code = rs.getString("servicecode");
                    return code != null ? code.trim().toUpperCase() : null;
                }
            );

            for (String service : services) {
                if (service != null && !service.isEmpty()) {
                    result.add(service);
                }
            }

            logger.info("Loaded {} active service codes", result.size());

        } catch (Exception e) {
            logger.error("Error fetching all service codes", e);
        }

        return result;
    }

    /**
     * Fetches all active vessel codes for cache loading.
     * @return Set of valid vessel codes (uppercase)
     */
    public Set<String> getAllActiveVesselCodes() {
        Set<String> result = new HashSet<>();

        try {
            logger.debug("Fetching all active vessel codes for cache");

            List<String> vessels = reportJdbcTemplate.query(
                SQL_GET_ALL_VESSELS,
                (rs, rowNum) -> {
                    String code = rs.getString("vesselcode");
                    return code != null ? code.trim().toUpperCase() : null;
                }
            );

            for (String vessel : vessels) {
                if (vessel != null && !vessel.isEmpty()) {
                    result.add(vessel);
                }
            }

            logger.info("Loaded {} active vessel codes", result.size());

        } catch (Exception e) {
            logger.error("Error fetching all vessel codes", e);
        }

        return result;
    }

    /**
     * Fetches all active timezone data (portcode -> timeOffSet) for cache loading.
     * @return Map of port codes to their time offsets
     */
    public Map<String, String> getAllActiveTimezones() {
        Map<String, String> result = new HashMap<>();

        try {
            logger.debug("Fetching all active timezone data for cache");

            reportJdbcTemplate.query(
                SQL_GET_ALL_TIMEZONES,
                (rs) -> {
                    String portCode = rs.getString("portcode");
                    String timeOffset = rs.getString("timeOffSet");
                    if (portCode != null && !portCode.trim().isEmpty()) {
                        result.put(portCode.trim().toUpperCase(), timeOffset != null ? timeOffset.trim() : "+00:00");
                    }
                }
            );

            logger.info("Loaded {} active timezone entries", result.size());

        } catch (Exception e) {
            logger.error("Error fetching all timezone data", e);
        }

        return result;
    }

    /**
     * Fetches all active dangerous goods (UN numbers with their IMO class codes) for cache loading.
     * @return Map of UN numbers to their class codes (uppercase)
     */
    public Map<String, String> getAllActiveDangerousGoods() {
        Map<String, String> result = new HashMap<>();

        try {
            logger.debug("Fetching all active dangerous goods (UN numbers) for cache");

            reportJdbcTemplate.query(
                SQL_GET_ALL_DANGEROUS_GOODS,
                (rs) -> {
                    String unNo = rs.getString("unno");
                    String classCode = rs.getString("class");
                    if (unNo != null && !unNo.trim().isEmpty()) {
                        result.put(unNo.trim().toUpperCase(), 
                                   classCode != null ? classCode.trim().toUpperCase() : null);
                    }
                }
            );

            logger.info("Loaded {} active dangerous goods entries", result.size());

        } catch (Exception e) {
            logger.error("Error fetching all dangerous goods data", e);
        }

        return result;
    }

    /**
     * Fetches all active route IDs (where current date is between fromdate and todate) for cache loading.
     * @return Set of valid route IDs
     */
    public Set<Integer> getAllActiveRouteIds() {
        Set<Integer> result = new HashSet<>();

        try {
            logger.debug("Fetching all active route IDs for cache");

            List<Integer> routeIds = reportJdbcTemplate.query(
                SQL_GET_ALL_ACTIVE_ROUTES,
                (rs, rowNum) -> rs.getInt("id")
            );

            for (Integer routeId : routeIds) {
                if (routeId != null) {
                    result.add(routeId);
                }
            }

            logger.info("Loaded {} active route IDs", result.size());

        } catch (Exception e) {
            logger.error("Error fetching all route IDs", e);
        }

        return result;
    }
}

