package com.solverminds.booking.repo;

import com.solverminds.edi.entity.EdiTempBookingRemarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdiTempBookingRemarksRepository extends JpaRepository<EdiTempBookingRemarks, Long> {
    
    /**
     * Find all remarks records for a specific booking
     */
    List<EdiTempBookingRemarks> findByBookNo(String bookNo);
    
    /**
     * Find all remarks records for a specific booking and EDI reader ID
     */
    List<EdiTempBookingRemarks> findByBookNoAndEdireaderid(String bookNo, String edireaderid);
}

