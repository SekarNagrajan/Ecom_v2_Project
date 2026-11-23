package com.solverminds.ecom.register.repo;

import com.solverminds.ecom.register.dto.CompanyCity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for customer master validation from ecomm_jsf database
 */
@Repository
public class CustomerMasterRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Find active customer by customer code from ecomm_jsf.customermaster
     * @param custCode customer code to validate
     * @return Optional containing CompanyCity if found, empty if not found or inactive
     */
    public Optional<CompanyCity> findActiveByCustCode(String custCode) {
        String sql = "SELECT TOP 1 cm.CustName, cm.city " +
                     "FROM customermaster cm WITH (NOLOCK) " +
                     "WHERE cm.status='A' AND cm.inactiveflag='A' AND cm.CustCode=?";
        
        try {
            return jdbcTemplate.query(sql, 
                ps -> ps.setString(1, custCode),
                rs -> {
                    if (rs.next()) {
                        String custName = rs.getString(1);
                        String city = rs.getString(2);
                        // Convert to uppercase like legacy systems
                        return Optional.of(new CompanyCity(
                            custName != null ? custName.toUpperCase() : null,
                            city != null ? city.toUpperCase() : null
                        ));
                    }
                    return Optional.empty();
                });
        } catch (Exception e) {
            // Log the error but return empty to indicate customer not found
            return Optional.empty();
        }
    }
}
