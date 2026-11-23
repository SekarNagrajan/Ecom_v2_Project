package com.solverminds.booking.repo;

import com.solverminds.booking.entity.EcomBookingReefer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for EcomBookingReefer entity.
 * Handles reefer container settings data access operations.
 */
@Repository
public interface EcomBookingReeferRepository extends JpaRepository<EcomBookingReefer, Long> {
    
    /**
     * Find reefer settings by booking number.
     * @param ebookno The booking number
     * @return List of reefer settings
     */
    List<EcomBookingReefer> findByEbookno(String ebookno);
    
    /**
     * Find reefer settings by booking number and container sequence.
     * @param ebookno The booking number
     * @param containerSeqno The container sequence number
     * @return Optional containing reefer settings if found
     */
    Optional<EcomBookingReefer> findByEbooknoAndConid(String ebookno, Integer containerSeqno);
    
    /**
     * Delete reefer settings by booking number.
     * @param ebookno The booking number
     */
    void deleteByEbookno(String ebookno);
}
