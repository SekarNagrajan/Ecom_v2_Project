package com.solverminds.booking.common.repository;

import com.solverminds.booking.common.entity.BookingRouting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BookingRouting entity (view-only operations).
 */
@Repository
public interface BookingRoutingViewRepository extends JpaRepository<BookingRouting, Integer> {

    /**
     * Find all routing entries for a booking
     */
    @Query("SELECT br FROM BookingRouting br WHERE br.booking.bookNo = :bookNo ORDER BY br.id")
    List<BookingRouting> findByBookNoOrderById(@Param("bookNo") String bookNo);

    /**
     * Find first routing entry for a booking (to get routeId)
     */
    @Query("SELECT br FROM BookingRouting br WHERE br.booking.bookNo = :bookNo ORDER BY br.id")
    Optional<BookingRouting> findFirstByBookNo(@Param("bookNo") String bookNo);
}

