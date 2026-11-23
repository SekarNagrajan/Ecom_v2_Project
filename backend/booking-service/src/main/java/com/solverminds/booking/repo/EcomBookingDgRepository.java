package com.solverminds.booking.repo;

import com.solverminds.booking.entity.EcomBookingDg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for EcomBookingDg entity.
 * Handles dangerous goods container data access operations.
 */
@Repository
public interface EcomBookingDgRepository extends JpaRepository<EcomBookingDg, Long> {
    
    /**
     * Find dangerous goods details by booking number.
     * @param ebookno The booking number
     * @return List of dangerous goods details
     */
    List<EcomBookingDg> findByEbookno(String ebookno);
    
    /**
     * Find dangerous goods details by booking number and container sequence.
     * @param ebookno The booking number
     * @param containerSeqno The container sequence number
     * @return List of dangerous goods details
     */
    List<EcomBookingDg> findByEbooknoAndConid(String ebookno, Integer containerSeqno);
    
    /**
     * Find dangerous goods details by booking number, container sequence, and item number.
     * @param ebookno The booking number
     * @param conid The container sequence number
     * @param itemno The item number
     * @return List of dangerous goods details
     */
    List<EcomBookingDg> findByEbooknoAndConidAndItemno(String ebookno, Integer conid, Integer itemno);
    
    /**
     * Delete dangerous goods details by booking number.
     * @param ebookno The booking number
     */
    void deleteByEbookno(String ebookno);
}
