package com.solverminds.sibl.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for containerprofile table (reportDB).
 * Used by Rule 4: validate container number and equipment type/size exist in containerprofile.
 */
@Repository("siblContainerProfileRepository")
public class ContainerProfileRepository {

    private static final Logger logger = LoggerFactory.getLogger(ContainerProfileRepository.class);
    private static final String SQL_EXISTS =
        "SELECT 1 FROM containerprofile (NOLOCK) WHERE equipmenttype = ? AND containerno = ?";

    private final JdbcTemplate reportJdbcTemplate;

    public ContainerProfileRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    /**
     * Check if a row exists for the given equipment type/size and container number.
     * DB column equipmenttype holds full code (e.g. RE20, 20DC), not single char D/R.
     *
     * @param equipmentType equipment type or size as stored in DB (e.g. RE20, 20DC)
     * @param containerNo   container number
     * @return true if a row exists, false otherwise
     */
    public boolean existsByEquipmentTypeAndContainerNo(String equipmentType, String containerNo) {
        if (equipmentType == null || equipmentType.trim().isEmpty()
                || containerNo == null || containerNo.trim().isEmpty()) {
            return false;
        }
        try {
            logger.debug("Checking if container profile exists for equipment type: {} and container number: {}", equipmentType, containerNo);
            List<Integer> rows = reportJdbcTemplate.query(SQL_EXISTS,
                    (rs, rowNum) -> 1,
                    equipmentType.trim(), containerNo.trim());
            return rows != null && !rows.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
