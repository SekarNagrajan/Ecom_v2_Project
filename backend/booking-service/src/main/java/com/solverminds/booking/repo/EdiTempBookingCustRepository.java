package com.solverminds.booking.repo;

import com.solverminds.edi.entity.EdiTempBookingCust;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdiTempBookingCustRepository extends JpaRepository<EdiTempBookingCust, Long> {
    
    /**
     * Find all customer records for a specific booking
     */
    List<EdiTempBookingCust> findByBookNo(String bookNo);
    
    /**
     * Find all customer records for a specific booking and EDI reader ID
     */
    List<EdiTempBookingCust> findByBookNoAndEdireaderid(String bookNo, String edireaderid);
}

