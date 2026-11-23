package com.solverminds.booking.service;

import com.solverminds.booking.exception.InvalidStatusTransitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for validating booking status transitions based on the business state machine.
 * 
 * State Transition Rules:
 * - D (Draft)         → D, S          : Normal edit/submit
 * - S (Submitted)     → S             : Re-submit (EDI: update R→V, then insert new R)
 * - P (In-Progress) + LRP=D → S       : Re-submit (EDI: insert new R only)
 * - P (In-Progress) + LRP=C → BLOCKED : Confirmed in ERP, no edits allowed
 * - P (In-Progress) + LRP=V → BLOCKED : Rejected by ERP, no edits allowed
 * - V (Rejected)      → S             : Re-submit after amendment cancel (EDI: same as S→S)
 * - L (Cancelled)     → BLOCKED       : ERP-only status, no edits allowed
 * - C (Confirmed)     → BLOCKED       : No edits allowed
 * - I (Void)          → BLOCKED       : No edits allowed
 * 
 * Note: 'A' (legacy Active status) is treated as 'P' (In-Progress)
 */
@Service
public class BookingStatusTransitionValidator {

    private static final Logger logger = LoggerFactory.getLogger(BookingStatusTransitionValidator.class);

    /**
     * Result of status transition validation
     */
    public static class TransitionResult {
        private final boolean valid;
        private final boolean requiresEdiUpdate;
        private final EdiUpdateType ediUpdateType;
        private final String errorMessage;

        private TransitionResult(boolean valid, boolean requiresEdiUpdate, EdiUpdateType ediUpdateType, String errorMessage) {
            this.valid = valid;
            this.requiresEdiUpdate = requiresEdiUpdate;
            this.ediUpdateType = ediUpdateType;
            this.errorMessage = errorMessage;
        }

        public static TransitionResult allowed() {
            return new TransitionResult(true, false, null, null);
        }

        public static TransitionResult allowedWithEdi(EdiUpdateType type) {
            return new TransitionResult(true, true, type, null);
        }

        public static TransitionResult blocked(String message) {
            return new TransitionResult(false, false, null, message);
        }

        public boolean isValid() { return valid; }
        public boolean isRequiresEdiUpdate() { return requiresEdiUpdate; }
        public EdiUpdateType getEdiUpdateType() { return ediUpdateType; }
        public String getErrorMessage() { return errorMessage; }
    }

    /**
     * Type of EDI update required for the transition
     */
    public enum EdiUpdateType {
        /** D → S: Initial submit - insert new EDI records (same as POST creation) */
        INITIAL_SUBMIT,
        /** S → S: Update existing R to V, then insert new R with extBookNo */
        RESUBMIT_FROM_SUBMITTED,
        /** P (LRP=D) → S: Insert new R with extBookNo (no R→V update) */
        RESUBMIT_FROM_IN_PROGRESS
    }

    /**
     * Validate if a status transition is allowed.
     * 
     * @param currentEcomStatus Current ECOM booking status (from ecombooking.ebook_status)
     * @param lrpStatus LRP/ERP status (from booking.status in reportDB), can be null
     * @param requestedStatus The status being requested
     * @return TransitionResult indicating if transition is allowed and any EDI actions needed
     */
    public TransitionResult validateTransition(String currentEcomStatus, String lrpStatus, String requestedStatus) {
        logger.debug("Validating transition: current={}, lrp={}, requested={}", 
                currentEcomStatus, lrpStatus, requestedStatus);

        if (requestedStatus == null || requestedStatus.trim().isEmpty()) {
            return TransitionResult.allowed();
        }

        String current = normalizeStatus(currentEcomStatus);
        String requested = normalizeStatus(requestedStatus);
        String lrp = normalizeStatus(lrpStatus);

        if (current == null) {
            return TransitionResult.allowed();
        }

        switch (current) {
            case "D":
                return validateFromDraft(requested);
            case "S":
                return validateFromSubmitted(requested);
            case "P":
            case "A":
                return validateFromInProgress(requested, lrp);
            case "V":
                return validateFromAmendmentCancelled(requested);
            case "L":
                return TransitionResult.blocked(
                    "Booking is Cancelled (L). No status changes or edits are allowed.");
            case "C":
                return TransitionResult.blocked(
                    "Booking is Confirmed (C). No status changes or edits are allowed.");
            case "I":
                return TransitionResult.blocked(
                    "Booking is Void (I). No status changes or edits are allowed.");
            case "T":
                return validateFromTemplate(requested);
            default:
                logger.warn("Unknown current status: {}", current);
                return TransitionResult.allowed();
        }
    }

    /**
     * Validate and throw exception if transition is not allowed.
     */
    public TransitionResult validateAndThrow(String currentEcomStatus, String lrpStatus, String requestedStatus) {
        TransitionResult result = validateTransition(currentEcomStatus, lrpStatus, requestedStatus);
        
        if (!result.isValid()) {
            throw new InvalidStatusTransitionException(
                currentEcomStatus, 
                lrpStatus, 
                requestedStatus, 
                result.getErrorMessage()
            );
        }
        
        return result;
    }

    /**
     * Validate transitions from Draft (D) status.
     * Allowed: D (edit), S (submit with EDI export)
     */
    private TransitionResult validateFromDraft(String requested) {
        if ("D".equals(requested)) {
            return TransitionResult.allowed();
        }
        if ("S".equals(requested)) {
            return TransitionResult.allowedWithEdi(EdiUpdateType.INITIAL_SUBMIT);
        }
        return TransitionResult.blocked(
            String.format("Cannot change Draft (D) booking to status '%s'. Allowed transitions: D (edit draft), S (submit).", requested));
    }

    /**
     * Validate transitions from Amendment Cancelled (V) status.
     * Allowed: S only — same EDI resubmit path as Submitted (S→S).
     */
    private TransitionResult validateFromAmendmentCancelled(String requested) {
        if ("S".equals(requested)) {
            return TransitionResult.allowedWithEdi(EdiUpdateType.RESUBMIT_FROM_SUBMITTED);
        }
        return TransitionResult.blocked(
            String.format(
                "The booking cannot be voided to status '%s'. Only re-submission with status 'S' is allowed.",
                requested));
    }

    /**
     * Validate transitions from Submitted (S) status.
     * Allowed: S (with EDI update: R→V, insert new R)
     */
    private TransitionResult validateFromSubmitted(String requested) {
        if ("S".equals(requested)) {
            return TransitionResult.allowedWithEdi(EdiUpdateType.RESUBMIT_FROM_SUBMITTED);
        }
        return TransitionResult.blocked(
            String.format("Cannot change Submitted (S) booking to status '%s'. Only re-submission (S) is allowed.", requested));
    }

    /**
     * Validate transitions from In-Progress (P/A) status.
     * Depends on LRP status:
     * - LRP=D (ERP Draft): S allowed (insert new R only)
     * - LRP=C (Confirmed): BLOCKED
     * - LRP=V (Rejected): BLOCKED
     */
    private TransitionResult validateFromInProgress(String requested, String lrpStatus) {
        if (lrpStatus == null || lrpStatus.isEmpty()) {
            logger.warn("LRP status is null for In-Progress booking, defaulting to allow re-submit");
            if ("S".equals(requested)) {
                return TransitionResult.allowedWithEdi(EdiUpdateType.RESUBMIT_FROM_IN_PROGRESS);
            }
            return TransitionResult.blocked(
                String.format("Cannot change In-Progress (P) booking to status '%s'. Only re-submission (S) may be allowed.", requested));
        }

        switch (lrpStatus) {
            case "D":
                if ("S".equals(requested)) {
                    return TransitionResult.allowedWithEdi(EdiUpdateType.RESUBMIT_FROM_IN_PROGRESS);
                }
                return TransitionResult.blocked(
                    String.format("Cannot change In-Progress (P) booking with ERP Draft status to '%s'. Only re-submission (S) is allowed.", requested));
            
            case "C":
                return TransitionResult.blocked(
                    "Booking is Confirmed in ERP (LRP=C). No status changes or edits are allowed.");
            
            case "V":
                return TransitionResult.blocked(
                    "Booking is Rejected in ERP (LRP=V). No status changes or edits are allowed.");
            
            default:
                logger.warn("Unknown LRP status: {}, allowing re-submit", lrpStatus);
                if ("S".equals(requested)) {
                    return TransitionResult.allowedWithEdi(EdiUpdateType.RESUBMIT_FROM_IN_PROGRESS);
                }
                return TransitionResult.blocked(
                    String.format("Cannot change In-Progress booking to status '%s'. Only re-submission (S) may be allowed.", requested));
        }
    }

    /**
     * Validate transitions from Template (T) status.
     * Allowed: T, D (create from template)
     */
    private TransitionResult validateFromTemplate(String requested) {
        if ("T".equals(requested) || "D".equals(requested)) {
            return TransitionResult.allowed();
        }
        return TransitionResult.blocked(
            String.format("Cannot change Template (T) booking to status '%s'. Allowed transitions: T (edit template), D (create draft from template).", requested));
    }

    /**
     * Normalize status value - trim and uppercase, handle legacy 'A' as 'P'.
     */
    private String normalizeStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }
        String normalized = status.trim().toUpperCase();
        return normalized;
    }
}
