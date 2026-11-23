package com.solverminds.booking.repo;

import com.solverminds.booking.entity.EcomBookingContainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for EcomBookingContainer entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface EcomBookingContainerRepository extends JpaRepository<EcomBookingContainer, Long> {
    
    /**
     * Find all containers for a specific booking.
     * @param ebookno The booking number
     * @return List of containers for the booking
     */
    List<EcomBookingContainer> findByEbookno(String ebookno);
    
    /**
     * Find containers by booking number and sequence number.
     * @param ebookno The booking number
     * @param seqno The sequence number
     * @return List of matching containers
     */
    List<EcomBookingContainer> findByEbooknoAndConid(String ebookno, Integer seqno);
    
    /**
     * Find containers by booking number ordered by sequence number.
     * @param ebookno The booking number
     * @return Ordered list of containers
     */
    List<EcomBookingContainer> findByEbooknoOrderByConid(String ebookno);
    
    /**
     * Delete containers by booking number.
     * @param ebookno The booking number
     */
    void deleteByEbookno(String ebookno);
}
