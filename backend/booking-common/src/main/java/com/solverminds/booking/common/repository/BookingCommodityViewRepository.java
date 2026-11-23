package com.solverminds.booking.common.repository;

import com.solverminds.booking.common.entity.BookingCommodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for BookingCommodity entity (view-only operations).
 */
@Repository
public interface BookingCommodityViewRepository extends JpaRepository<BookingCommodity, Long> {

    /**
     * Find all commodity entries for a booking
     */
    @Query("SELECT bc FROM BookingCommodity bc WHERE bc.booking.bookNo = :bookNo ORDER BY bc.cargoNo, bc.itemNo")
    List<BookingCommodity> findByBookNoOrderByCargoNoAndItemNo(@Param("bookNo") String bookNo);
}

