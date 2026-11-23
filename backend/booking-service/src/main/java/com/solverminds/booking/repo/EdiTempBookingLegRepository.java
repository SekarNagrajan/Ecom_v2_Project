package com.solverminds.booking.repo;

import com.solverminds.edi.entity.EdiTempBookingLeg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdiTempBookingLegRepository extends JpaRepository<EdiTempBookingLeg, Integer> {
    
    /**
     * Find all leg records for a specific booking
     */
    List<EdiTempBookingLeg> findByBookNo(String bookNo);
    
    /**
     * Find all leg records for a specific booking and EDI reader ID
     */
    List<EdiTempBookingLeg> findByBookNoAndEdireaderid(String bookNo, String edireaderid);
}

