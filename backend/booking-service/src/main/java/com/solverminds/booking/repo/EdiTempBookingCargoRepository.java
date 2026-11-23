package com.solverminds.booking.repo;

import com.solverminds.edi.entity.EdiTempBookingCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdiTempBookingCargoRepository extends JpaRepository<EdiTempBookingCargo, Long> {
    
    /**
     * Find all cargo records for a specific booking
     */
    List<EdiTempBookingCargo> findByBookNo(String bookNo);
    
    /**
     * Find all cargo records for a specific booking and EDI reader ID
     */
    List<EdiTempBookingCargo> findByBookNoAndEdireaderid(String bookNo, String edireaderid);
}

