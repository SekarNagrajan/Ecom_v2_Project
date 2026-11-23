package com.svm.ecom.security.quickreg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Reads the registration status for a logged-in user from the {@code ecomuserdetails}
 * table using the existing {@code ecomJdbcTemplate} configured in
 * {@code com.svm.ecom.config.ECOMDataConfig}.
 *
 * <p>Used exclusively by {@link QuickRegistrationGuardFilter} to gate write
 * (POST / PUT) requests for users whose registration is still incomplete
 * (status = {@code 'D'}).
 *
 * <p>Returns {@code null} when no row matches the given email, which the
 * caller treats as "block for security safety".
 */
@Service
public class UserStatusService {

    private static final Logger logger = LoggerFactory.getLogger(UserStatusService.class);

    private static final String SQL_FIND_STATUS_BY_EMAIL =
            "SELECT TOP 1 status FROM ecomuserdetails WITH (NOLOCK) WHERE emailid = ?";

    private final JdbcTemplate ecomJdbcTemplate;

    public UserStatusService(@Qualifier("ecomJdbcTemplate") JdbcTemplate ecomJdbcTemplate) {
        this.ecomJdbcTemplate = ecomJdbcTemplate;
    }

    /**
     * Look up the registration status for the given email ID.
     *
     * @param emailId the logged-in user's email ID (from {@code SecurityContextHolder})
     * @return the status code (e.g. {@code "A"}, {@code "D"}) or {@code null}
     *         when the user is not found or the value is null in the database
     */
    public String findStatusByEmail(String emailId) {
        if (emailId == null || emailId.isEmpty()) {
            return null;
        }
        try {
            return ecomJdbcTemplate.queryForObject(SQL_FIND_STATUS_BY_EMAIL, String.class, emailId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (RuntimeException ex) {
            // Defensive: never let a transient DB error mask a real auth check.
            // Returning null causes the guard filter to block the request,
            // which is the documented "fail-secure" behaviour.
            logger.warn("Failed to load ecomuserdetails.status for emailid='{}': {}", emailId, ex.getMessage());
            return null;
        }
    }
}
