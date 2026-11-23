package com.svm.ecom.infrastructure.user;

import com.svm.ecom.domain.user.UserApiPermissionRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * Reads {@code ecomapi_user_api_permissions} via native SQL ({@code ecomJdbcTemplate}).
 * Aligned with booking-service JDBC repositories (NOLOCK, static SQL).
 */
@Repository
public class UserApiPermissionJdbcRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserApiPermissionJdbcRepository.class);

    private static final String ACCESS_FLAG_YES = "Y";

    private static final String SQL_FIND_ACTIVE_BY_USERNAME =
            "SELECT DISTINCT api_endpoint "
                    + "FROM ecomapi_user_api_permissions WITH (NOLOCK) "
                    + "WHERE UPPER(username) = UPPER(?) AND access_flag = ?";

    private final JdbcTemplate ecomJdbcTemplate;

    public UserApiPermissionJdbcRepository(@Qualifier("ecomJdbcTemplate") JdbcTemplate ecomJdbcTemplate) {
        this.ecomJdbcTemplate = ecomJdbcTemplate;
    }

    public List<UserApiPermissionRecord> findActiveByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            List<UserApiPermissionRecord> rows = ecomJdbcTemplate.query(
                    SQL_FIND_ACTIVE_BY_USERNAME,
                    (rs, rowNum) -> new UserApiPermissionRecord(
                            rs.getString("api_endpoint")),
                    username.trim(),
                    ACCESS_FLAG_YES);
            logger.debug("Loaded {} active API permission(s) for username='{}'", rows.size(), username);
            return rows;
        } catch (RuntimeException ex) {
            logger.warn("Failed to load API permissions for username='{}': {}", username, ex.getMessage());
            return Collections.emptyList();
        }
    }
}
