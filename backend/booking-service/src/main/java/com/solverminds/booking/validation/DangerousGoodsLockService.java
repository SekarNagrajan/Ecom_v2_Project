package com.solverminds.booking.validation;

import com.solverminds.booking.dto.BookingRequestDTO;
import com.solverminds.booking.dto.CommodityDTO;
import com.solverminds.booking.dto.v2.ErrorMessage;
import com.solverminds.booking.dto.v2.NestedErrorMessage;
import com.solverminds.booking.entity.EcomBooking;
import com.solverminds.booking.repo.EcomBookingRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Blocks DG edits on agency-accepted bookings ({@code DG_LOCKED} / HTTP 409).
 */
@Service
public class DangerousGoodsLockService {

    public static final String DG_LOCKED = "DG_LOCKED";

    private final EcomBookingRepository ecomBookingRepository;
    private final BookingStatusValidationService bookingStatusValidationService;

    public DangerousGoodsLockService(EcomBookingRepository ecomBookingRepository,
                                     BookingStatusValidationService bookingStatusValidationService) {
        this.ecomBookingRepository = ecomBookingRepository;
        this.bookingStatusValidationService = bookingStatusValidationService;
    }

    /**
     * When the booking is accepted and the request carries any DG payload, return 409 {@code DG_LOCKED}.
     */
    public Optional<ErrorMessage> validateNotLocked(String onlineBookNo, BookingRequestDTO request, String instance) {
        if (onlineBookNo == null || onlineBookNo.trim().isEmpty() || request == null) {
            return Optional.empty();
        }
        Optional<EcomBooking> existing = ecomBookingRepository.findByEbookno(onlineBookNo.trim());
        if (!existing.isPresent()) {
            return Optional.empty();
        }
        if (!bookingStatusValidationService.isAcceptedStatus(existing.get().getEbookStatus())) {
            return Optional.empty();
        }
        if (!requestHasDgPayload(request)) {
            return Optional.empty();
        }
        ErrorMessage error = new ErrorMessage(
                "Dangerous goods cannot be modified after the booking has been accepted by the agency.",
                "Conflict",
                409);
        error.setInstance(instance);
        error.setCode(DG_LOCKED);
        error.addNestedError(new NestedErrorMessage(
                "Dangerous goods are locked after agency acceptance.",
                "#/containers",
                DG_LOCKED));
        return Optional.of(error);
    }

    private static boolean requestHasDgPayload(BookingRequestDTO request) {
        if (request.getCommodities() == null) {
            return false;
        }
        for (CommodityDTO c : request.getCommodities()) {
            if (c != null && DangerousGoodsValidator.hasDangerousGoodsPayload(c.getDangerousGoods())) {
                return true;
            }
        }
        return false;
    }
}
