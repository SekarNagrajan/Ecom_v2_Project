package com.solverminds.booking.repo;

import com.solverminds.booking.entity.EcomBookingRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcomBookingRouteRepository extends JpaRepository<EcomBookingRoute, Long> {
    List<EcomBookingRoute> findByEbookno(String ebookno);
    
    /**
     * Find all routes for a booking ordered by leg number
     * @param ebookno The booking number
     * @return Ordered list of routes
     */
    List<EcomBookingRoute> findByEbooknoOrderByLegNo(String ebookno);
    
    /**
     * Delete all routes for a booking
     * @param ebookno The booking number
     */
    void deleteByEbookno(String ebookno);
}
