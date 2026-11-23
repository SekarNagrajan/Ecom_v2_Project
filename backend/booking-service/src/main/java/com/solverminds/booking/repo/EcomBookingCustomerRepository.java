package com.solverminds.booking.repo;

import com.solverminds.booking.entity.EcomBookingCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcomBookingCustomerRepository extends JpaRepository<EcomBookingCustomer, Long> {
    List<EcomBookingCustomer> findByEbookno(String ebookno);
    
    /**
     * Find all customers for a booking ordered by customer ID (sequence)
     * @param ebookno The booking number
     * @return Ordered list of customers
     */
    List<EcomBookingCustomer> findByEbooknoOrderByCid(String ebookno);
    
    /**
     * Delete all customers for a booking
     * @param ebookno The booking number
     */
    void deleteByEbookno(String ebookno);
}
