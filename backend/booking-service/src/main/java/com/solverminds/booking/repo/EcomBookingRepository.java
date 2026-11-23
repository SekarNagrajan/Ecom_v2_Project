package com.solverminds.booking.repo;

import com.solverminds.booking.entity.EcomBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EcomBookingRepository extends JpaRepository<EcomBooking, String> {
    /**
     * Find an EcomBooking by its booking number (primary key).
     * @param ebookno the booking number
     * @return Optional containing the EcomBooking if found, empty otherwise
     */
    Optional<EcomBooking> findByEbookno(String ebookno);

    /**
     * Find an EcomBooking by extBookNo (ERP booking number) and customer code.
     * Used when bookingId in path is the ERP/external booking number (e.g. TSHG123).
     *
     * @param extBookNo The external/ERP booking number
     * @param custCode  The customer code for access control
     * @return Optional containing the EcomBooking if found, empty otherwise
     */
    @Query("SELECT e FROM EcomBooking e WHERE e.extBookNo = :extBookNo AND e.custCode = :custCode")
    Optional<EcomBooking> findByExtBookNoAndCustCode(@Param("extBookNo") String extBookNo, @Param("custCode") String custCode);

    /**
     * Find any ECOM row linked to an LRP {@code book_no} (used before creating a new shell).
     */
    List<EcomBooking> findByExtBookNo(String extBookNo);
    
    /**
     * Find cancelled/rejected bookings for a customer.
     * Includes bookings with status V (Rejected) or L (Cancelled).
     * These are excluded from the main stored procedure results.
     * 
     * @param custCode The customer code
     * @return List of cancelled/rejected bookings
     */
    @Query("SELECT e FROM EcomBooking e WHERE e.custCode = :custCode AND e.ebookStatus IN ('V', 'L') ORDER BY e.datecr DESC")
    List<EcomBooking> findCancelledBookingsByCustCode(@Param("custCode") String custCode);
    
    /**
     * Find bookings by customer code and status.
     * 
     * @param custCode The customer code
     * @param ebookStatus The booking status
     * @return List of bookings matching the criteria
     */
    List<EcomBooking> findByCustCodeAndEbookStatus(String custCode, String ebookStatus);
}
