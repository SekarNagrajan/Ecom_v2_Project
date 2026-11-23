package com.solverminds.sibl.repo;

import com.solverminds.sibl.entity.EcomSiCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEcomSiCustomerRepository")
public interface EcomSiCustomerRepository extends JpaRepository<EcomSiCustomer, Long> {
    
    /**
     * Find all customers for a specific SI
     * @param eSINo the SI number
     * @return list of customers for the SI
     */
    List<EcomSiCustomer> findByeSINo(String eSINo);

    void deleteByeSINo(String eSINo);
}
