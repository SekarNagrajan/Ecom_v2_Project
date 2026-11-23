package com.svm.ecom.common.master.repo;

import com.svm.ecom.common.master.dto.CustomerDetail;
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
 * Common repository for querying master data from reportDB for cache loading.
 * Used by CommonMasterDataCacheService (e.g. SIBL service). Ports, customers, countries,
 * commodities, package, terminal, service, vessel codes.
 */
@Repository("commonMasterDataRepository")
public class MasterDataRepository {

    private static final Logger logger = LoggerFactory.getLogger(MasterDataRepository.class);

    private static final String SQL_GET_ALL_PORTS =
        "SELECT portcode FROM ports (nolock) WHERE status='A' AND portcode IS NOT NULL";
    private static final String SQL_GET_ALL_CUSTOMERS =
        "SELECT custcode FROM customermaster (nolock) WHERE status='A' AND custcode IS NOT NULL";
    private static final String SQL_GET_ALL_CUSTOMER_DETAILS =
        "SELECT custcode, custname FROM customermaster (nolock) WHERE status='A' AND custcode IS NOT NULL";
    private static final String SQL_GET_ALL_COUNTRIES =
        "SELECT countrycode FROM country (nolock) WHERE status='A' AND countrycode IS NOT NULL";
    private static final String SQL_GET_ALL_COMMODITIES =
        "SELECT comdtycode, grpcode FROM commodity (nolock) WHERE status='A' AND comdtycode IS NOT NULL";
    private static final String SQL_GET_ALL_PACKAGES =
        "SELECT packagecode FROM package (nolock) WHERE status='A' AND packagecode IS NOT NULL";
    private static final String SQL_GET_ALL_TERMINALS =
        "SELECT terminalcode FROM terminals (nolock) WHERE status='A' AND terminalcode IS NOT NULL";
    /** Terminal code → name for SI view (same table as booking service). */
    private static final String SQL_GET_TERMINAL_NAMES =
        "SELECT terminalcode, terminalname FROM terminals (nolock) WHERE status='A' AND terminalcode IS NOT NULL";
    private static final String SQL_GET_ALL_SERVICES =
        "SELECT servicecode FROM service (nolock) WHERE status='Active' AND servicecode IS NOT NULL";
    private static final String SQL_GET_ALL_VESSELS =
        "SELECT vesselcode FROM vesselmaster (nolock) WHERE status='A' AND vesselcode IS NOT NULL";

    private final JdbcTemplate reportJdbcTemplate;

    public MasterDataRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    public Set<String> getAllActivePortCodes() {
        Set<String> result = new HashSet<>();
        try {
            List<String> ports = reportJdbcTemplate.query(SQL_GET_ALL_PORTS,
                (rs, rowNum) -> {
                    String code = rs.getString("portcode");
                    return code != null ? code.trim().toUpperCase() : null;
                });
            for (String port : ports) {
                if (port != null && !port.isEmpty()) result.add(port);
            }
            logger.info("Loaded {} active port codes", result.size());
        } catch (Exception e) {
            logger.error("Error fetching all port codes", e);
        }
        return result;
    }

    public Set<String> getAllActiveCustomerCodes() {
        Set<String> result = new HashSet<>();
        try {
            List<String> customers = reportJdbcTemplate.query(SQL_GET_ALL_CUSTOMERS,
                (rs, rowNum) -> {
                    String code = rs.getString("custcode");
                    return code != null ? code.trim().toUpperCase() : null;
                });
            for (String c : customers) {
                if (c != null && !c.isEmpty()) result.add(c);
            }
            logger.info("Loaded {} active customer codes", result.size());
        } catch (Exception e) {
            logger.error("Error fetching all customer codes", e);
        }
        return result;
    }

    public Set<String> getAllActiveCountryCodes() {
        Set<String> result = new HashSet<>();
        try {
            List<String> countries = reportJdbcTemplate.query(SQL_GET_ALL_COUNTRIES,
                (rs, rowNum) -> {
                    String code = rs.getString("countrycode");
                    return code != null ? code.trim().toUpperCase() : null;
                });
            for (String c : countries) {
                if (c != null && !c.isEmpty()) result.add(c);
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

    public Set<String> getAllActivePackageCodes() {
        Set<String> result = new HashSet<>();
        try {
            List<String> packages = reportJdbcTemplate.query(SQL_GET_ALL_PACKAGES,
                (rs, rowNum) -> {
                    String code = rs.getString("packagecode");
                    return code != null ? code.trim().toUpperCase() : null;
                });
            for (String p : packages) {
                if (p != null && !p.isEmpty()) result.add(p);
            }
            logger.info("Loaded {} active package codes", result.size());
        } catch (Exception e) {
            logger.error("Error fetching all package codes", e);
        }
        return result;
    }

    public Set<String> getAllActiveTerminalCodes() {
        Set<String> result = new HashSet<>();
        try {
            List<String> terminals = reportJdbcTemplate.query(SQL_GET_ALL_TERMINALS,
                (rs, rowNum) -> {
                    String code = rs.getString("terminalcode");
                    return code != null ? code.trim().toUpperCase() : null;
                });
            for (String t : terminals) {
                if (t != null && !t.isEmpty()) result.add(t);
            }
            logger.info("Loaded {} active terminal codes", result.size());
        } catch (Exception e) {
            logger.error("Error fetching all terminal codes", e);
        }
        return result;
    }

    /**
     * Terminal code → terminal name for SI view (report DB). Same pattern as booking service master data.
     * Used to show terminal name in polTerminal/podTerminal in bill-of-lading view.
     */
    public Map<String, String> getTerminalNameMap() {
        Map<String, String> result = new HashMap<>();
        try {
            List<Map<String, Object>> rows = reportJdbcTemplate.queryForList(SQL_GET_TERMINAL_NAMES);
            for (Map<String, Object> row : rows) {
                Object code = row.get("terminalcode");
                Object name = row.get("terminalname");
                if (code != null && code.toString() != null && !code.toString().trim().isEmpty()) {
                    result.put(code.toString().trim(), name != null ? name.toString().trim() : "");
                }
            }
            logger.info("Loaded {} terminal names for view", result.size());
        } catch (Exception e) {
            logger.error("Error fetching terminal names", e);
        }
        return result;
    }

    public Set<String> getAllActiveServiceCodes() {
        Set<String> result = new HashSet<>();
        try {
            List<String> services = reportJdbcTemplate.query(SQL_GET_ALL_SERVICES,
                (rs, rowNum) -> {
                    String code = rs.getString("servicecode");
                    return code != null ? code.trim().toUpperCase() : null;
                });
            for (String s : services) {
                if (s != null && !s.isEmpty()) result.add(s);
            }
            logger.info("Loaded {} active service codes", result.size());
        } catch (Exception e) {
            logger.error("Error fetching all service codes", e);
        }
        return result;
    }

    public Set<String> getAllActiveVesselCodes() {
        Set<String> result = new HashSet<>();
        try {
            List<String> vessels = reportJdbcTemplate.query(SQL_GET_ALL_VESSELS,
                (rs, rowNum) -> {
                    String code = rs.getString("vesselcode");
                    return code != null ? code.trim().toUpperCase() : null;
                });
            for (String v : vessels) {
                if (v != null && !v.isEmpty()) result.add(v);
            }
            logger.info("Loaded {} active vessel codes", result.size());
        } catch (Exception e) {
            logger.error("Error fetching all vessel codes", e);
        }
        return result;
    }

    /**
     * Fetch all active customer details (code and name) for caching.
     * Used by CommonMasterDataCacheService to build customer detail cache.
     * 
     * @return Map of customer code (uppercase) → CustomerDetail
     */
    public Map<String, CustomerDetail> getAllActiveCustomerDetails() {
        Map<String, CustomerDetail> result = new HashMap<>();
        try {
            List<CustomerDetail> customers = reportJdbcTemplate.query(SQL_GET_ALL_CUSTOMER_DETAILS,
                (rs, rowNum) -> {
                    String code = rs.getString("custcode");
                    String name = rs.getString("custname");
                    if (code != null && !code.trim().isEmpty()) {
                        return new CustomerDetail(code.trim().toUpperCase(), name != null ? name.trim() : "");
                    }
                    return null;
                });
            
            for (CustomerDetail customer : customers) {
                if (customer != null && customer.getCustCode() != null) {
                    result.put(customer.getCustCode(), customer);
                }
            }
            logger.info("Loaded {} active customer details", result.size());
        } catch (Exception e) {
            logger.error("Error fetching all customer details", e);
        }
        return result;
    }
}
