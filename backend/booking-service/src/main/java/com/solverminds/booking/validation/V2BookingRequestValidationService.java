package com.solverminds.booking.validation;

import com.solverminds.booking.dto.BookingRequestDTO;
import com.solverminds.booking.dto.validation.ValidationGroups;
import com.solverminds.booking.dto.v2.BookingRequestUpsertPayloadV2;
import com.solverminds.booking.dto.v2.ErrorMessage;
import com.solverminds.booking.dto.v2.NestedErrorMessage;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * V2 booking create/update validation aligned with {@link com.solverminds.booking.api.BookingController} (V1):
 * <p>
 * V1 uses {@code @Valid @RequestBody BookingRequestDTO} (Default group). Here the same rules apply to the
 * <i>mapped</i> {@link BookingRequestDTO} via {@link Validator#validate(Object, Class[])}, and violation paths are
 * converted to V2 JSON pointers via {@link V1BookingViolationToV2PointerMapper} so clients see V2 field names.
 * </p>
 * <p><b>Recommended call order in {@link com.solverminds.booking.api.BookingControllerV2}</b></p>
 * <ol>
 *   <li>{@code @Valid} on {@code BookingRequestUpsertPayloadV2} — Spring MVC (same as V1 style for the wire model);
 *       failures → {@link com.solverminds.booking.api.BookingV2ExceptionHandler}.</li>
 *   <li><i>Update only:</i> {@link #validateUpdatePathMatchesPayloadBookingIds} — V1 path vs {@code body.onlineBookNo} parity.</li>
 *   <li><i>Normalize payload</i> (e.g. clear dry-container reefer), then <i>map V2 → {@link BookingRequestDTO}</i>.</li>
 *   <li>{@link #validateV1BookingRequestDto} — Default group (create / update shared rules).</li>
 *   <li><i>Update only:</i> {@link #validateV1BookingRequestDtoForUpdate} — {@code Default} + {@link ValidationGroups.UpdateBooking}
 *       (e.g. {@code containerNo} required). Applied for all PUT updates; business may scope this to amendment-only if needed.</li>
 *   <li><i>Agency resolution, persistence</i> — after validation, as in V1.</li>
 * </ol>
 */
@Service
public class V2BookingRequestValidationService {

    private final Validator validator;
    private final DangerousGoodsValidator dangerousGoodsValidator;

    public V2BookingRequestValidationService(Validator validator,
                                             DangerousGoodsValidator dangerousGoodsValidator) {
        this.validator = validator;
        this.dangerousGoodsValidator = dangerousGoodsValidator;
    }

    /**
     * Validate the mapped {@link BookingRequestDTO} (V1 rules; same as {@code @Valid} on V1 API). Violation paths are translated to V2 pointers.
     */
    public Optional<ErrorMessage> validateV1BookingRequestDto(BookingRequestDTO v1, String instance) {
        if (v1 == null) {
            return Optional.empty();
        }
        Set<ConstraintViolation<BookingRequestDTO>> violations = validator.validate(v1, Default.class);
        Optional<ErrorMessage> dgError = dangerousGoodsValidator.validate(v1, instance);
        if (violations.isEmpty()) {
            return dgError;
        }
        ErrorMessage error = baseValidationError(instance);
        for (ConstraintViolation<BookingRequestDTO> v : violations) {
            String relative = V1BookingViolationToV2PointerMapper.toV2RelativePointer(v.getPropertyPath(), v1);
            String pointer = relative.isEmpty() ? "#" : "#/" + relative;
            String msg = v.getMessage() != null ? v.getMessage() : "Invalid value";
            error.addNestedError(new NestedErrorMessage(msg, pointer, constraintAnnotationSimpleName(v)));
        }
        mergeDgErrors(error, dgError);
        return Optional.of(error);
    }

    /**
     * Validate mapped {@link BookingRequestDTO} for booking update (PUT): {@link Default} plus {@link ValidationGroups.UpdateBooking}.
     */
    public Optional<ErrorMessage> validateV1BookingRequestDtoForUpdate(BookingRequestDTO v1, String instance) {
        if (v1 == null) {
            return Optional.empty();
        }
        Set<ConstraintViolation<BookingRequestDTO>> violations =
                validator.validate(v1, Default.class, ValidationGroups.UpdateBooking.class);
        Optional<ErrorMessage> dgError = dangerousGoodsValidator.validate(v1, instance);
        if (violations.isEmpty()) {
            return dgError;
        }
        ErrorMessage error = baseValidationError(instance);
        for (ConstraintViolation<BookingRequestDTO> v : violations) {
            String relative = V1BookingViolationToV2PointerMapper.toV2RelativePointer(v.getPropertyPath(), v1);
            String pointer = relative.isEmpty() ? "#" : "#/" + relative;
            String msg = v.getMessage() != null ? v.getMessage() : "Invalid value";
            error.addNestedError(new NestedErrorMessage(msg, pointer, constraintAnnotationSimpleName(v)));
        }
        mergeDgErrors(error, dgError);
        return Optional.of(error);
    }

    private static void mergeDgErrors(ErrorMessage target, Optional<ErrorMessage> dgError) {
        if (!dgError.isPresent() || dgError.get().getErrors() == null) {
            return;
        }
        for (NestedErrorMessage n : dgError.get().getErrors()) {
            target.addNestedError(n);
        }
    }

    /**
     * V1 update parity ({@code BookingController#update}): when the body includes {@code onlineBookNo}, it must match
     * the path. V2 optional {@code bookingRequestId} / {@code id} must match {@code pathBookingId} when present.
     * Field pointers use V2 JSON names.
     */
    public Optional<ErrorMessage> validateUpdatePathMatchesPayloadBookingIds(
            String pathBookingId,
            BookingRequestUpsertPayloadV2 body,
            String instance) {
        if (pathBookingId == null || body == null) {
            return Optional.empty();
        }
        String path = pathBookingId.trim();
        List<NestedErrorMessage> nested = new ArrayList<>();
        String bookingRequestId = body.getBookingRequestId();
        if (bookingRequestId != null && !bookingRequestId.trim().isEmpty()
                && !path.equalsIgnoreCase(bookingRequestId.trim())) {
            nested.add(new NestedErrorMessage(
                "Path booking id and bookingRequestId in the body must match.",
                "#/bookingRequestId",
                "BOOKING_ID_MISMATCH"));
        }
        String id = body.getId();
        if (id != null && !id.trim().isEmpty() && !path.equalsIgnoreCase(id.trim())) {
            nested.add(new NestedErrorMessage(
                "Path booking id and id in the body must match.",
                "#/id",
                "BOOKING_ID_MISMATCH"));
        }
        if (nested.isEmpty()) {
            return Optional.empty();
        }
        ErrorMessage error = baseValidationError(instance);
        error.setDetail("Booking identifier in the path and body must match.");
        for (NestedErrorMessage n : nested) {
            error.addNestedError(n);
        }
        return Optional.of(error);
    }

    private static ErrorMessage baseValidationError(String instance) {
        ErrorMessage error = new ErrorMessage(
            "Booking data validation failed. Please review the provided data.",
            "Validation Failed",
            400);
        error.setInstance(instance);
        error.setCode("VALIDATION_FAILED");
        error.setType(ErrorMessage.TYPE_VALIDATION_FAILED);
        return error;
    }

    private static String constraintAnnotationSimpleName(ConstraintViolation<?> v) {
        try {
            Annotation ann = v.getConstraintDescriptor().getAnnotation();
            return ann.annotationType().getSimpleName();
        } catch (RuntimeException e) {
            return "CONSTRAINT_VIOLATION";
        }
    }
}
