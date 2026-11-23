package com.solverminds.ecom.register.repo;

import com.solverminds.ecom.register.entity.EcomRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcomRegistrationRepository extends JpaRepository<EcomRegistration, String> {
    // Basic CRUD operations are inherited from JpaRepository
    // Custom query methods can be added here if needed in the future
    
    /**
     * Check if a login name already exists (case-insensitive)
     * @param loginName the login name to check
     * @return true if the login name exists, false otherwise
     */
    boolean existsByLoginNameIgnoreCase(String loginName);
}
