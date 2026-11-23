package com.solverminds.booking.common.repository;

import com.solverminds.booking.common.entity.BookingCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for BookingCargo entity (view-only operations).
 */
@Repository
public interface BookingCargoViewRepository extends JpaRepository<BookingCargo, Long> {

    /**
     * Find all cargo/container entries for a booking
     */
    @Query("SELECT bc FROM BookingCargo bc WHERE bc.booking.bookNo = :bookNo ORDER BY bc.cargoNo")
    List<BookingCargo> findByBookNoOrderByCargoNo(@Param("bookNo") String bookNo);
}

