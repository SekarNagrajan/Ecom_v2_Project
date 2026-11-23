package com.solverminds.booking.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repository for querying equipment data from the equipments table.
 */
@Repository
public class EquipmentRepository {

    private static final Logger logger = LoggerFactory.getLogger(EquipmentRepository.class);

    private static final String SQL_GET_TARE_WEIGHT = 
        "SELECT tarekg FROM equipments (nolock) WHERE equiptype = ?";

    private static final String SQL_GET_GROSS_WEIGHT = 
        "SELECT grosskg FROM equipments (nolock) WHERE status='A' AND equiptype = ?";

    private static final String SQL_GET_ALL_GROSS_WEIGHTS = 
        "SELECT equiptype, grosskg FROM equipments (nolock) WHERE status='A' AND grosskg IS NOT NULL";

    private static final String SQL_GET_ALL_TARE_WEIGHTS = 
        "SELECT equiptype, tarekg FROM equipments (nolock) WHERE status='A' AND tarekg IS NOT NULL";

    private final JdbcTemplate reportJdbcTemplate;

    public EquipmentRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    /**
     * Fetches the tare weight (in kg) for a given equipment type from equipments table.
     * @param equipType The equipment type (e.g., "40DC", "20DC", "40RH")
     * @return The tare weight in kg if found, null otherwise
     */
    public Float getTareWeightByEquipType(String equipType) {
        if (equipType == null || equipType.trim().isEmpty()) {
            logger.warn("Equipment type is null or empty, cannot fetch tare weight");
            return null;
        }

        try {
            logger.debug("Fetching tare weight for equipment type: {}", equipType);
            
            List<Float> results = reportJdbcTemplate.query(
                SQL_GET_TARE_WEIGHT,
                ps -> ps.setString(1, equipType.trim()),
                (rs, rowNum) -> rs.getFloat("tarekg")
            );

            if (results.isEmpty()) {
                logger.warn("No tare weight found for equipment type: {}", equipType);
                return null;
            }

            Float tareWeight = results.get(0);
            logger.debug("Found tare weight: {} kg for equipment type: {}", tareWeight, equipType);
            
            // If multiple results, log warning and return first
            if (results.size() > 1) {
                logger.warn("Multiple tare weights found for equipment type {}, using first: {}", equipType, tareWeight);
            }
            
            return tareWeight;
            
        } catch (Exception e) {
            logger.error("Error fetching tare weight for equipment type: " + equipType, e);
            return null;
        }
    }

    /**
     * Fetches the gross weight (max payload capacity in kg) for a given equipment type from equipments table.
     * This is used for validating that container cargo weight does not exceed capacity.
     * 
     * @param equipType The equipment type (e.g., "40DC", "20DC", "40RH")
     * @return The gross weight in kg if found, null otherwise
     */
    public Float getGrossWeightByEquipType(String equipType) {
        if (equipType == null || equipType.trim().isEmpty()) {
            logger.warn("Equipment type is null or empty, cannot fetch gross weight");
            return null;
        }

        try {
            logger.debug("Fetching gross weight for equipment type: {}", equipType);
            
            List<Float> results = reportJdbcTemplate.query(
                SQL_GET_GROSS_WEIGHT,
                ps -> ps.setString(1, equipType.trim()),
                (rs, rowNum) -> rs.getFloat("grosskg")
            );

            if (results.isEmpty()) {
                logger.warn("No gross weight found for equipment type: {}", equipType);
                return null;
            }

            Float grossWeight = results.get(0);
            logger.debug("Found gross weight: {} kg for equipment type: {}", grossWeight, equipType);
            
            // If multiple results, log warning and return first
            if (results.size() > 1) {
                logger.warn("Multiple gross weights found for equipment type {}, using first: {}", equipType, grossWeight);
            }
            
            return grossWeight;
            
        } catch (Exception e) {
            logger.error("Error fetching gross weight for equipment type: " + equipType, e);
            return null;
        }
    }

    /**
     * Fetches tare weights for multiple equipment types in a single query.
     * This is more efficient when processing multiple containers.
     * 
     * @param equipTypes Collection of equipment types (e.g., "40DC", "20DC", "40RH")
     * @return Map of equipment type to tare weight (in kg)
     */
    public Map<String, Float> getTareWeightsByEquipTypes(Collection<String> equipTypes) {
        Map<String, Float> result = new HashMap<>();
        
        if (equipTypes == null || equipTypes.isEmpty()) {
            logger.debug("No equipment types provided for bulk tare weight lookup");
            return result;
        }

        // Filter out null/empty values and get unique equipment types
        List<String> validEquipTypes = equipTypes.stream()
                .filter(et -> et != null && !et.trim().isEmpty())
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());

        if (validEquipTypes.isEmpty()) {
            logger.debug("No valid equipment types after filtering");
            return result;
        }

        try {
            logger.debug("Fetching tare weights for {} unique equipment types", validEquipTypes.size());

            // Build IN clause with placeholders
            String placeholders = validEquipTypes.stream()
                    .map(et -> "?")
                    .collect(Collectors.joining(", "));
            
            String sql = "SELECT equiptype, tarekg FROM equipments (nolock) WHERE equiptype IN (" + placeholders + ")";

            List<Map<String, Object>> rows = reportJdbcTemplate.query(
                sql,
                ps -> {
                    for (int i = 0; i < validEquipTypes.size(); i++) {
                        ps.setString(i + 1, validEquipTypes.get(i));
                    }
                },
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("equiptype", rs.getString("equiptype"));
                    row.put("tarekg", rs.getFloat("tarekg"));
                    return row;
                }
            );

            for (Map<String, Object> row : rows) {
                String equipType = (String) row.get("equiptype");
                Float tareKg = (Float) row.get("tarekg");
                if (equipType != null) {
                    result.put(equipType.trim(), tareKg);
                }
            }

            logger.debug("Found tare weights for {} equipment types: {}", result.size(), result);
            
            // Log any equipment types not found
            for (String et : validEquipTypes) {
                if (!result.containsKey(et)) {
                    logger.warn("No tare weight found for equipment type: {}", et);
                }
            }

        } catch (Exception e) {
            logger.error("Error fetching bulk tare weights for equipment types: " + validEquipTypes, e);
        }

        return result;
    }

    /**
     * Fetches all active equipment gross weights for cache loading.
     * Query: SELECT equiptype, grosskg FROM equipments (nolock) WHERE status='A' AND grosskg IS NOT NULL
     * 
     * @return Map of equipment type to gross weight (in kg)
     */
    public Map<String, Float> getAllEquipmentGrossWeights() {
        Map<String, Float> result = new HashMap<>();

        try {
            logger.debug("Fetching all equipment gross weights for cache");

            List<Map<String, Object>> rows = reportJdbcTemplate.query(
                SQL_GET_ALL_GROSS_WEIGHTS,
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("equiptype", rs.getString("equiptype"));
                    row.put("grosskg", rs.getFloat("grosskg"));
                    return row;
                }
            );

            for (Map<String, Object> row : rows) {
                String equipType = (String) row.get("equiptype");
                Float grossKg = (Float) row.get("grosskg");
                if (equipType != null && grossKg != null) {
                    result.put(equipType.trim().toUpperCase(), grossKg);
                }
            }

            logger.info("Loaded {} equipment gross weights", result.size());

        } catch (Exception e) {
            logger.error("Error fetching all equipment gross weights", e);
        }

        return result;
    }

    /**
     * Fetches all active equipment tare weights for cache loading.
     * Query: SELECT equiptype, tarekg FROM equipments (nolock) WHERE status='A' AND tarekg IS NOT NULL
     * 
     * @return Map of equipment type to tare weight (in kg)
     */
    public Map<String, Float> getAllEquipmentTareWeights() {
        Map<String, Float> result = new HashMap<>();

        try {
            logger.debug("Fetching all equipment tare weights for cache");

            List<Map<String, Object>> rows = reportJdbcTemplate.query(
                SQL_GET_ALL_TARE_WEIGHTS,
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("equiptype", rs.getString("equiptype"));
                    row.put("tarekg", rs.getFloat("tarekg"));
                    return row;
                }
            );

            for (Map<String, Object> row : rows) {
                String equipType = (String) row.get("equiptype");
                Float tareKg = (Float) row.get("tarekg");
                if (equipType != null && tareKg != null) {
                    result.put(equipType.trim().toUpperCase(), tareKg);
                }
            }

            logger.info("Loaded {} equipment tare weights", result.size());

        } catch (Exception e) {
            logger.error("Error fetching all equipment tare weights", e);
        }

        return result;
    }
}

