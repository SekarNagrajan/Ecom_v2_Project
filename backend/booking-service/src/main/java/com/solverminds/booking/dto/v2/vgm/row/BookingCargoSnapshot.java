package com.solverminds.booking.dto.v2.vgm.row;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * One LRP read: booking exists (status &lt;&gt; V) plus cargo lines.
 * Used by V3 so exists + type counts + dummy/SOC flags share a single query.
 */
public class BookingCargoSnapshot {

    private final boolean bookingFound;
    private final List<BookingCargoRow> cargoRows;

    public BookingCargoSnapshot(boolean bookingFound, List<BookingCargoRow> cargoRows) {
        this.bookingFound = bookingFound;
        this.cargoRows = cargoRows == null ? new ArrayList<>() : cargoRows;
    }

    public static BookingCargoSnapshot notFound() {
        return new BookingCargoSnapshot(false, Collections.emptyList());
    }

    public boolean isBookingFound() {
        return bookingFound;
    }

    public List<BookingCargoRow> getCargoRows() {
        return cargoRows;
    }
}
