package com.solverminds.booking.repo;

import com.solverminds.edi.entity.EdiTempBookingCommodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdiTempBookingCommodityRepository extends JpaRepository<EdiTempBookingCommodity, Long> {
    
    /**
     * Find all commodity records for a specific booking
     */
    List<EdiTempBookingCommodity> findByBookNo(String bookNo);
    
    /**
     * Find all commodity records for a specific booking and EDI reader ID
     */
    List<EdiTempBookingCommodity> findByBookNoAndEdireaderid(String bookNo, String edireaderid);
}

