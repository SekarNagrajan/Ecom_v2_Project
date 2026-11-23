package com.solverminds.booking.repo;

import com.solverminds.edi.entity.EdiTempBookingConf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdiTempBookingConfRepository extends JpaRepository<EdiTempBookingConf, Long> {
    
    /**
     * Find all conf records for a specific booking
     */
    List<EdiTempBookingConf> findByBookNo(String bookNo);
    
    /**
     * Find all conf records for a specific booking and EDI reader ID
     */
    List<EdiTempBookingConf> findByBookNoAndEdireaderid(String bookNo, String edireaderid);
}

