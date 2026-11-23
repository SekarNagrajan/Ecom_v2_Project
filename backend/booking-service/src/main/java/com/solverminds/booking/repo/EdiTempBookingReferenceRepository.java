package com.solverminds.booking.repo;

import com.solverminds.edi.entity.EdiTempBookingReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdiTempBookingReferenceRepository extends JpaRepository<EdiTempBookingReference, Integer> {
    
    /**
     * Find all reference records for a specific booking
     */
    List<EdiTempBookingReference> findByBookNo(String bookNo);
    
    /**
     * Find all reference records for a specific booking and EDI reader ID
     */
    List<EdiTempBookingReference> findByBookNoAndEdireaderid(String bookNo, String edireaderid);

    /**
     * Find reference records by booking number and refid (e.g. refid='SHR')
     */
    List<EdiTempBookingReference> findByBookNoAndRefid(String bookNo, String refid);
}

