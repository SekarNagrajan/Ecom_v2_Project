package com.solverminds.booking.common.repository;

import com.solverminds.booking.common.entity.BookingReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BookingReference entity (view-only operations).
 * Reference types: SHR (Shipper Ref), EOR (Rate Ref), ERF (Export Ref), EOF (Ocean Freight)
 */
@Repository
public interface BookingReferenceViewRepository extends JpaRepository<BookingReference, Integer> {

    /**
     * Find all references for a booking
     */
    @Query("SELECT br FROM BookingReference br WHERE br.booking.bookNo = :bookNo ORDER BY br.id")
    List<BookingReference> findByBookNoOrderById(@Param("bookNo") String bookNo);

    /**
     * Find specific reference by booking number and reference type
     */
    @Query("SELECT br FROM BookingReference br WHERE br.booking.bookNo = :bookNo AND br.refId = :refId")
    Optional<BookingReference> findByBookNoAndRefId(@Param("bookNo") String bookNo, @Param("refId") String refId);
}

