package com.solverminds.booking.common.repository;

import com.solverminds.booking.common.entity.BookingCust;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BookingCust entity (view-only operations).
 */
@Repository
public interface BookingCustViewRepository extends JpaRepository<BookingCust, Long> {

    /**
     * Find all customers for a booking
     */
    @Query("SELECT bc FROM BookingCust bc WHERE bc.booking.bookNo = :bookNo ORDER BY bc.cid")
    List<BookingCust> findByBookNoOrderByCid(@Param("bookNo") String bookNo);

    /**
     * Find customer by booking number and customer type (e.g., 'BP' for Booking Party)
     */
    @Query("SELECT bc FROM BookingCust bc WHERE bc.booking.bookNo = :bookNo AND bc.custTypeId = :custTypeId")
    Optional<BookingCust> findByBookNoAndCustTypeId(@Param("bookNo") String bookNo, @Param("custTypeId") String custTypeId);
}

