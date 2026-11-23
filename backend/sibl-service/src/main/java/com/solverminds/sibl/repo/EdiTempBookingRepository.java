package com.solverminds.sibl.repo;

import com.solverminds.edi.entity.EdiTempBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("siblEdiTempBookingRepository")
public interface EdiTempBookingRepository extends JpaRepository<EdiTempBooking, Integer> {
    
    /**
     * Find all EDI temp bookings with given status
     */
    List<EdiTempBooking> findByStatus(String status);
    
    /**
     * Find EDI temp booking by booking number
     */
    EdiTempBooking findByBookNo(String bookNo);
    
    /**
     * Find all EDI temp bookings with status = 'S' and not yet processed
     */
    @Query("SELECT e FROM EdiTempBooking e WHERE e.status = 'S' AND (e.edicleanstatus IS NULL OR e.edicleanstatus != 'P')")
    List<EdiTempBooking> findPendingSuccessfulBookings();
    
    /**
     * Find all EDI temp bookings that haven't been processed yet (all statuses)
     * Used for inserting into ecom tables regardless of status
     */
    @Query("SELECT e FROM EdiTempBooking e WHERE e.edicleanstatus IS NULL OR e.edicleanstatus != 'P'")
    List<EdiTempBooking> findPendingBookings();
}
