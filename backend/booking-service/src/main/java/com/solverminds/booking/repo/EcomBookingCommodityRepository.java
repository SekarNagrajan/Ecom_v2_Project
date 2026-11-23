package com.solverminds.booking.repo;

import com.solverminds.booking.entity.EcomBookingCommodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for EcomBookingCommodity entity.
 * Provides standard CRUD operations and custom queries.
 */
@Repository
public interface EcomBookingCommodityRepository extends JpaRepository<EcomBookingCommodity, Long> {
    
    /**
     * Find all commodities for a specific booking.
     * @param ebookno The booking number
     * @return List of commodities for the booking
     */
    List<EcomBookingCommodity> findByEbookno(String ebookno);
    
    /**
     * Find all commodities for a specific booking and container sequence.
     * @param ebookno The booking number
     * @param containerSeqno The container sequence number
     * @return List of commodities for the booking and container
     */
    List<EcomBookingCommodity> findByEbooknoAndConid(String ebookno, Integer containerSeqno);
    
    /**
     * Find all commodities for a booking ordered by container ID and item number
     * @param ebookno The booking number
     * @return Ordered list of commodities
     */
    List<EcomBookingCommodity> findByEbooknoOrderByConidAscItemnoAsc(String ebookno);
    
    /**
     * Delete all commodities for a booking
     * @param ebookno The booking number
     */
    void deleteByEbookno(String ebookno);
}

// If using composite PK, replace Long with EcomBookingCommodityId in the JpaRepository declaration
