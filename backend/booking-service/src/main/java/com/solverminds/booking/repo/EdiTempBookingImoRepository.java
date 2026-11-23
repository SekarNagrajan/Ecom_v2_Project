package com.solverminds.booking.repo;

import com.solverminds.edi.entity.EdiTempBookingImo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdiTempBookingImoRepository extends JpaRepository<EdiTempBookingImo, Integer> {
    
    /**
     * Find all IMO/DG records for a specific booking
     */
    List<EdiTempBookingImo> findByBookNo(String bookNo);
    
    /**
     * Find all IMO/DG records for a specific booking and EDI reader ID
     */
    List<EdiTempBookingImo> findByBookNoAndEdireaderid(String bookNo, String edireaderid);
}

