package com.solverminds.booking.repo;

import com.solverminds.booking.entity.EcomBookingOog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for EcomBookingOog entity.
 * Handles out-of-gauge container data access operations.
 */
@Repository
public interface EcomBookingOogRepository extends JpaRepository<EcomBookingOog, Long> {
    
    /**
     * Find OOG details by booking number.
     * @param ebookno The booking number
     * @return List of OOG details
     */
    List<EcomBookingOog> findByEbookno(String ebookno);
    
    /**
     * Find OOG details by booking number and container sequence.
     * @param ebookno The booking number
     * @param containerSeqno The container sequence number
     * @return List of OOG details
     */
    List<EcomBookingOog> findByEbooknoAndConid(String ebookno, Integer containerSeqno);
    
    /**
     * Delete OOG details by booking number.
     * @param ebookno The booking number
     */
    void deleteByEbookno(String ebookno);
}
