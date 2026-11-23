package com.solverminds.sibl.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Commodity group, HS code, package type in master; when hazardous=Y, dangerousGoods required (same as Booking ValidCommodityMasterData + hazardous rule).
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SIBLCommodityMasterDataValidator.class)
public @interface ValidSIBLCommodityMasterData {
    String message() default "Invalid commodity master data";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
