package com.solverminds.booking.service;

import com.solverminds.booking.exception.BookingCancelAccessDeniedException;
import com.solverminds.booking.exception.BookingCancellationConflictException;
import com.solverminds.booking.exception.BookingNotFoundForCancelException;
import com.solverminds.booking.repo.BookingCancelJdbcRepository;
import com.solverminds.booking.repo.BookingCancelJdbcRepository.EcomBookingCancelRow;
import com.solverminds.booking.repo.BookingCancelJdbcRepository.LrpBookingRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Cancel booking service — three keys, three variable names:
 *
 *   ebookNo   = BKON...   from API path
 *               → ecombooking READ/WRITE
 *               → edidocstatus READ/WRITE (direct link; always read for cancel rules)
 *
 *   extBookNo = from ecombooking.extBookNo
 *               → booking JOIN query (to resolve bookNo)
 *               → booking remarks READ
 *
 *   bookNo    = booking.book_no from JOIN result
 *               → booking WRITE (cancel)
 *               → plotdomaster CHECK
 *               → bl CHECK
 *               → operationdetail CHECK
 *               → usp_emsBookingContMovement EXEC
 *
 * Flow: if {@code extBookNo} is empty → PATH A when ecom {@code S} and EDI {@code R}; else EDI {@code P} or not cancellable.
 * If {@code extBookNo} is set → PATH B when LRP booking status is D (DRAFT); else EDI {@code P} or not cancellable.
 */
@Service
public class BookingCancelService {

    private static final Logger logger =
            LoggerFactory.getLogger(BookingCancelService.class);

    private static final String USER_UP = "Ecom";   // hardcoded — matches legacy

    private final BookingCancelJdbcRepository repo;
    private final BookingValidationService    validationService;

    public BookingCancelService(BookingCancelJdbcRepository repo,
                                BookingValidationService validationService) {
        this.repo              = repo;
        this.validationService = validationService;
    }

    public void cancelBookingRequest(String ebookNo,
                                     String reason,
                                     String loginName,
                                     String customerCode) {

        Objects.requireNonNull(ebookNo, "ebookNo");
        String ecomKey      = ebookNo.trim();
        String cancelUser   = loginName    != null ? loginName.trim()    : "";
        String custCode     = customerCode != null ? customerCode.trim() : "";
        String cancelReason = reason       != null ? reason.trim()       : "";

        if (ecomKey.isEmpty()) {
            throw new BookingNotFoundForCancelException(ecomKey);
        }
        if (custCode.isEmpty() || cancelUser.isEmpty()) {
            throw new BookingCancelAccessDeniedException("Access denied");
        }

        // ── READ ecombooking (ECOMDB) — key: ebookNo ────────────────────
        EcomBookingCancelRow ecomRow = repo
                .findByEcomBookNo(ecomKey)
                .orElseThrow(() -> new BookingNotFoundForCancelException(ecomKey));

        // ── Customer ownership check ─────────────────────────────────────
        if (!custCode.equalsIgnoreCase(nullSafe(ecomRow.custCode))) {
            logger.warn("Cancel denied: customer mismatch ebookNo={}", ecomKey);
            throw new BookingCancelAccessDeniedException(
                    "Access denied to booking: " + ecomKey);
        }

        String eBookStatus = nullSafe(ecomRow.eBookStatus).trim().toUpperCase(Locale.ROOT);
        String extBookNo   = nullSafe(ecomRow.extBookNo).trim();

        logger.info("Cancel: ebookNo={} eBookStatus={} extBookNo={}",
                ecomKey, eBookStatus, extBookNo);

        // ── READ edidocstatus (EDI) — key: ebookNo (always; not gated on extBookNo) ─────
        String ediStatus = repo.getEdiStatus(ecomKey)
                .map(r -> nullSafe(r.status).trim().toUpperCase(Locale.ROOT))
                .orElse("");

        // ── READ booking (LRP) via JOIN — key: extBookNo (when present) ──────────────
        String lrpStatus = "";
        String bookNo    = "";   // booking.book_no — the LRP operational key
        if (!extBookNo.isEmpty()) {
            LrpBookingRow lrpRow = repo
                    .getLrpBookingStatus(extBookNo)
                    .orElse(null);
            if (lrpRow != null) {
                lrpStatus = nullSafe(lrpRow.lrpStatus).trim().toUpperCase(Locale.ROOT);
                bookNo    = nullSafe(lrpRow.bookNo).trim();
            }
        }

        logger.info("Resolved: ediStatus={} lrpStatus={} bookNo={}",
                ediStatus, lrpStatus, bookNo);

        // ══════════════════════════════════════════════════════════════
        // DECISION TREE
        // 1) extBookNo empty: ecombooking 'S' + edidocstatus 'R' → PATH A; else check 'P'; else error
        // 2) extBookNo set: LRP booking row with status D (DRAFT) → PATH B; else check 'P'; else error
        // ══════════════════════════════════════════════════════════════

        if (extBookNo.isEmpty()) {
            // PATH A — submitted in ECOM, EDI received (no ERP / ext number yet)
            if ("S".equals(eBookStatus) && "R".equals(ediStatus)) {
                logger.info("PATH A: eBookStatus=S ediStatus=R ebookNo={} (no extBookNo)", ecomKey);
                executePathA(ecomKey, bookNo, cancelReason);
                return;
            }
            if ("P".equals(ediStatus)) {
                throw new BookingCancellationConflictException(
                        "EDI_PROCESSED",
                        "Booking already processed.");
            }
            throw new BookingCancellationConflictException(
                    "BOOKING_NOT_CANCELLABLE",
                    "Something went wrong. " +
                    "Booking cannot be cancelled in its current state.");
        }

        // extBookNo present — PATH B only when LRP row is DRAFT (DB stores as 'D')
        if ("D".equals(lrpStatus)) {
            logger.info("PATH B: lrpStatus=D (DRAFT) bookNo={}", bookNo);
            executePathB(ecomKey, extBookNo, bookNo, cancelReason);
            return;
        }
        if ("P".equals(ediStatus)) {
            throw new BookingCancellationConflictException(
                    "EDI_PROCESSED",
                    "Booking already processed.");
        }
        throw new BookingCancellationConflictException(
                "BOOKING_NOT_CANCELLABLE",
                "Something went wrong. " +
                "Booking cannot be cancelled in its current state.");
    }

    // ── PATH A ────────────────────────────────────────────────────
    // ebook_status='S' AND edi_status='R'
    //
    // Key usage:
    //   ebookNo → VOID edidocstatus
    //   ebookNo → UPDATE ecombooking (incl. declined_remarks with API cancel reason)
    //   bookNo  → only used to decide 'V' vs 'I' for ecombooking status
    private void executePathA(String ebookNo, String bookNo, String cancelReason) {

        // Void EDI — key: ebookNo
        repo.voidEdiStatus(ebookNo);   // ← ebookNo (direct link)

        // Update ecombooking — key: ebookNo
        // 'V' = had LRP reference (bookNo resolved),  'I' = no LRP
        String newStatus = bookNo.isEmpty() ? "I" : "V";
        repo.cancelEcomBookingPathA(ebookNo, newStatus, cancelReason);   // reason → declined_remarks

        logger.info("PATH A complete: ebookNo={} → ebook_status={}", ebookNo, newStatus);
    }

    // ── PATH B ────────────────────────────────────────────────────
    // lrpStatus='D'
    //
    // Key usage:
    //   bookNo    → validate (CRO/BL/Op/EMS)
    //   extBookNo → get existing LRP remarks
    //   bookNo    → cancel LRP booking (UPDATE booking)
    //   ebookNo   → VOID edidocstatus, cancel ecombooking: ebook_status='V' + declined_remarks (same as Path A)
    private void executePathB(String ebookNo,
                               String extBookNo,
                               String bookNo,
                               String cancelReason) {

        // Validate — key: bookNo
        // String result = validationService.validate(bookNo);   // ← bookNo

        // switch (result) {
        //     case BookingValidationService.CRO:
        //         throw new BookingCancellationConflictException(
        //                 "CRO_EXISTS",
        //                 "CRO is done for this Booking");

        //     case BookingValidationService.BL:
        //         throw new BookingCancellationConflictException(
        //                 "BL_EXISTS",
        //                 "BL is available for this Booking");

        //     case BookingValidationService.OPERATION:
        //         throw new BookingCancellationConflictException(
        //                 "OPERATION_EXISTS",
        //                 "Load Planned is done for this Booking");

        //     case BookingValidationService.EMS:
        //         throw new BookingCancellationConflictException(
        //                 "EMS_EXISTS",
        //                 "CRO and following Container already Sent to Shipper. " +
        //                 "You cannot cancel this booking");

        //     case BookingValidationService.VALID:
        //         break;

        //     default:
        //         throw new BookingCancellationConflictException(
        //                 "VALIDATION_ERROR",
        //                 "Booking validation could not be completed");
        // }

        // Get existing LRP remarks — key: extBookNo
        // String existingRemarks = nullSafe(repo.getLrpBookingRemarks(extBookNo));   // ← extBookNo
        // String mergedRemarks   = buildCancelRemarks(existingRemarks, cancelReason);

        // Cancel LRP booking — key: bookNo
        // repo.cancelLrpBooking(bookNo, cancelReason, USER_UP, mergedRemarks);   // ← bookNo
        // logger.info("PATH B: LRP booking cancelled bookNo={}", bookNo);

        // Cancel ecombooking — key: ebookNo (aligned with Path A: void EDI, reason → declined_remarks)
        repo.voidEdiStatus(ebookNo);
        repo.cancelEcomBookingPathA(ebookNo, "V", cancelReason);
        logger.info("PATH B complete: ecombooking {} → V", ebookNo);
    }

    /**
     * Remarks format — exact legacy pattern preserved including "cancled" typo.
     *
     *   empty:   " Booking cancled by : Ecom :- yyyy/MM/dd hh:mm: <reason>"
     *   exists:  "<existing>\n\n Booking cancled by : Ecom :- yyyy/MM/dd hh:mm: <reason>"
     */
    private static String buildCancelRemarks(String existing, String reason) {
        String dateStr = new SimpleDateFormat("yyyy/MM/dd hh:mm").format(new Date());
        String note    = " Booking cancled by : " + USER_UP +
                         " :- " + dateStr + ": " + reason;
        String trimmed = nullSafe(existing).trim();
        return trimmed.isEmpty() ? note : trimmed + "\n\n" + note;
    }

    private static String nullSafe(String s) {
        return s != null ? s : "";
    }
}
