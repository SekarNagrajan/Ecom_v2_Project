package com.svm.ecom.master.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Calls usp_eEcomBookingCustomerSearch on ReportDB to retrieve
 * customer codes from previous booking transactions for a given login customer.
 */
@Repository
public class CustomerSearchRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomerSearchRepository.class);

    private static final String SP_CUSTOMER_SEARCH =
            "exec usp_eEcomBookingCustomerSearch ?, 'custname like ''%%''', 'BP', ''";

    private final JdbcTemplate reportJdbcTemplate;

    public CustomerSearchRepository(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    /**
     * Returns the list of custID values from the SP result — these are
     * customer codes previously used in transactions by the login customer.
     */
    public List<String> findPreviouslyUsedCustomerCodes(String loginCustomerCode) {
        try {
            List<String> codes = reportJdbcTemplate.query(SP_CUSTOMER_SEARCH,
                    (rs, rowNum) -> {
                        String custId = rs.getString("custID");
                        return custId != null ? custId.trim() : null;
                    },
                    loginCustomerCode);

            List<String> filtered = codes.stream()
                    .filter(c -> c != null && !c.isEmpty())
                    .collect(Collectors.toList());

            logger.info("Found {} previously used customer codes for login customer: {}",
                    filtered.size(), loginCustomerCode);
            return filtered;

        } catch (Exception e) {
            logger.error("Error calling usp_eEcomBookingCustomerSearch for customer {}: {}",
                    loginCustomerCode, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
