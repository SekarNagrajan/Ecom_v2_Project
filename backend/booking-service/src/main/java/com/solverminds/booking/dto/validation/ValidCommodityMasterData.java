package com.solverminds.booking.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validation annotation to check if commodity-related codes exist in the master data cache.
 * Validates:
 * - commodityGroup (against commodity table)
 * - hsCode (against commodity table, only if provided)
 * - packageType (against package table, only if provided)
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CommodityMasterDataValidator.class)
public @interface ValidCommodityMasterData {
    String message() default "Invalid commodity master data";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

