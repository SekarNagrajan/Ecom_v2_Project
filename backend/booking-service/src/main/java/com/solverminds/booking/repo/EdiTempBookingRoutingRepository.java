package com.solverminds.booking.repo;

import com.solverminds.edi.entity.EdiTempBookingRouting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdiTempBookingRoutingRepository extends JpaRepository<EdiTempBookingRouting, Integer> {
    
    /**
     * Find routing record for a specific booking
     */
    EdiTempBookingRouting findByBookNo(String bookNo);
    
    /**
     * Find all routing records for a specific booking and EDI reader ID
     */
    List<EdiTempBookingRouting> findByBookNoAndEdireaderid(String bookNo, String edireaderid);
}

