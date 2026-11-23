package com.solverminds.booking.common.repository;

import com.solverminds.booking.common.entity.BookingLeg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for BookingLeg entity (view-only operations).
 */
@Repository
public interface BookingLegViewRepository extends JpaRepository<BookingLeg, Integer> {

    /**
     * Find all leg/route entries for a booking ordered by leg number
     */
    @Query("SELECT bl FROM BookingLeg bl WHERE bl.booking.bookNo = :bookNo ORDER BY bl.legNo")
    List<BookingLeg> findByBookNoOrderByLegNo(@Param("bookNo") String bookNo);
}

