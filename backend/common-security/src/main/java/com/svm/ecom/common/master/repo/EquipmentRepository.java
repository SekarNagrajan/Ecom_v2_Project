package com.svm.ecom.common.master.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Common repository for querying equipment data from reportDB.
 * Used by CommonMasterDataCacheService. All fetches are query-based (WHERE equiptype=? or IN (...)).
 * Aligned with Booking service (equipments table).
 */
@Repository("commonEquipmentRepository")
public class EquipmentRepository {

    private static final Logger logger = LoggerFactory.getLogger(EquipmentRepository.class);

    /** Single equiptype: tare weight (kg). */
    private static final String SQL_GET_TARE_WEIGHT_BY_EQUIPTYPE =
        "SELECT tarekg FROM equipments (nolock) WHERE status='A' AND tarekg IS NOT NULL AND equiptype = ?";
    /** Single equiptype: gross weight (kg). */
    private static final String SQL_GET_GROSS_WEIGHT_BY_EQUIPTYPE =
        "SELECT grosskg FROM equipments (nolock) WHERE status='A' AND grosskg IS NOT NULL AND equiptype = ?";
    /** Lightweight: equiptype codes only (for valid-equipment validation). */
    private static final String SQL_GET_ALL_ACTIVE_EQUIPTYPES =
        "SELECT equiptype FROM equipments (nolock) WHERE status='A'";

    private final JdbcTemplate reportJdbcTemplate;

    public EquipmentRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    /**
     * Fetches active equipment type codes only (for validation). No tare/gross loaded.
     */
    public Set<String> getAllActiveEquipTypes() {
        try {
            List<String> list = reportJdbcTemplate.query(SQL_GET_ALL_ACTIVE_EQUIPTYPES,
                (rs, rowNum) -> rs.getString("equiptype"));
            Set<String> result = list.stream()
                .filter(e -> e != null && !e.trim().isEmpty())
                .map(e -> e.trim().toUpperCase())
                .collect(Collectors.toSet());
            logger.info("Loaded {} active equipment types (validation only)", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Error fetching active equipment types", e);
            return Collections.emptySet();
        }
    }

    /**
     * Single equiptype: tare weight (kg). Use for one-off lookups.
     */
    public Float getTareWeightByEquipType(String equipType) {
        if (equipType == null || equipType.trim().isEmpty()) {
            logger.warn("Equipment type is null or empty, cannot fetch tare weight");
            return null;
        }
        try {
            List<Float> results = reportJdbcTemplate.query(
                SQL_GET_TARE_WEIGHT_BY_EQUIPTYPE,
                ps -> ps.setString(1, equipType.trim()),
                (rs, rowNum) -> rs.getFloat("tarekg"));
            if (results.isEmpty()) return null;
            Float tare = results.get(0);
            if (results.size() > 1) {
                logger.warn("Multiple tare weights for equipment type {}, using first: {}", equipType, tare);
            }
            return tare;
        } catch (Exception e) {
            logger.error("Error fetching tare weight for equipment type: " + equipType, e);
            return null;
        }
    }

    /**
     * Single equiptype: gross weight (kg). Use for one-off lookups.
     */
    public Float getGrossWeightByEquipType(String equipType) {
        if (equipType == null || equipType.trim().isEmpty()) {
            logger.warn("Equipment type is null or empty, cannot fetch gross weight");
            return null;
        }
        try {
            List<Float> results = reportJdbcTemplate.query(
                SQL_GET_GROSS_WEIGHT_BY_EQUIPTYPE,
                ps -> ps.setString(1, equipType.trim()),
                (rs, rowNum) -> rs.getFloat("grosskg"));
            if (results.isEmpty()) return null;
            Float gross = results.get(0);
            if (results.size() > 1) {
                logger.warn("Multiple gross weights for equipment type {}, using first: {}", equipType, gross);
            }
            return gross;
        } catch (Exception e) {
            logger.error("Error fetching gross weight for equipment type: " + equipType, e);
            return null;
        }
    }

    /**
     * Bulk: fetch tare weights for multiple equipment types in one query.
     *
     * @param equipTypes Collection of equipment types (e.g. from container list)
     * @return Map equiptype (trimmed) -> tare weight (kg); only types found in DB are present
     */
    public Map<String, Float> getTareWeightsByEquipTypes(Collection<String> equipTypes) {
        Map<String, Float> result = new HashMap<>();
        if (equipTypes == null || equipTypes.isEmpty()) {
            logger.debug("No equipment types provided for bulk tare weight lookup");
            return result;
        }
        List<String> list = equipTypes.stream()
            .filter(et -> et != null && !et.trim().isEmpty())
            .map(String::trim)
            .distinct()
            .collect(Collectors.toList());
        if (list.isEmpty()) return result;

        try {
            String placeholders = list.stream().map(et -> "?").collect(Collectors.joining(", "));
            String sql = "SELECT equiptype, tarekg FROM equipments (nolock) WHERE status='A' AND tarekg IS NOT NULL AND equiptype IN (" + placeholders + ")";
            List<Map<String, Object>> rows = reportJdbcTemplate.query(sql,
                ps -> {
                    for (int i = 0; i < list.size(); i++) {
                        ps.setString(i + 1, list.get(i));
                    }
                },
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("equiptype", rs.getString("equiptype"));
                    row.put("tarekg", rs.getFloat("tarekg"));
                    return row;
                });
            for (Map<String, Object> row : rows) {
                String equipType = (String) row.get("equiptype");
                Float tareKg = (Float) row.get("tarekg");
                if (equipType != null) {
                    result.put(equipType.trim().toUpperCase(), tareKg);
                }
            }
            logger.debug("Bulk tare: fetched {} tare weights for {} equipment types", result.size(), list.size());
            for (String et : list) {
                if (!result.containsKey(et.toUpperCase())) {
                    logger.warn("No tare weight found for equipment type: {}", et);
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching bulk tare weights for equipment types: " + list, e);
        }
        return result;
    }

    /**
     * Bulk: fetch gross weights for multiple equipment types in one query.
     */
    public Map<String, Float> getGrossWeightsByEquipTypes(Collection<String> equipTypes) {
        Map<String, Float> result = new HashMap<>();
        if (equipTypes == null || equipTypes.isEmpty()) return result;
        List<String> list = equipTypes.stream()
            .filter(et -> et != null && !et.trim().isEmpty())
            .map(String::trim)
            .distinct()
            .collect(Collectors.toList());
        if (list.isEmpty()) return result;

        try {
            String placeholders = list.stream().map(et -> "?").collect(Collectors.joining(", "));
            String sql = "SELECT equiptype, grosskg FROM equipments (nolock) WHERE status='A' AND grosskg IS NOT NULL AND equiptype IN (" + placeholders + ")";
            List<Map<String, Object>> rows = reportJdbcTemplate.query(sql,
                ps -> {
                    for (int i = 0; i < list.size(); i++) {
                        ps.setString(i + 1, list.get(i));
                    }
                },
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("equiptype", rs.getString("equiptype"));
                    row.put("grosskg", rs.getFloat("grosskg"));
                    return row;
                });
            for (Map<String, Object> row : rows) {
                String equipType = (String) row.get("equiptype");
                Float grossKg = (Float) row.get("grosskg");
                if (equipType != null && grossKg != null) {
                    result.put(equipType.trim().toUpperCase(), grossKg);
                }
            }
            logger.debug("Bulk gross: fetched {} gross weights for {} equipment types", result.size(), list.size());
        } catch (Exception e) {
            logger.error("Error fetching bulk gross weights for equipment types: " + list, e);
        }
        return result;
    }
}
