package com.solverminds.booking.common.repository;

import com.solverminds.booking.common.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Booking entity (view-only operations).
 */
@Repository
public interface BookingViewRepository extends JpaRepository<Booking, String> {

    /**
     * Find booking by book_no (primary key)
     */
    Optional<Booking> findByBookNo(String bookNo);

    /**
     * Find booking by agency and book_no
     */
    Optional<Booking> findByAgencyIdAndBookNo(String agencyId, String bookNo);
}

