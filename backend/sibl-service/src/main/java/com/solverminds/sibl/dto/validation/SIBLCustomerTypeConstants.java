package com.solverminds.sibl.dto.validation;

import com.svm.ecom.common.enums.CustomerType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Allowed customer types for SI Create and SI Update (customers block).
 * Only these custTypeId values are accepted: SH, CN, NT, N1, N2, FW, SL, BY.
 */
public final class SIBLCustomerTypeConstants {

    /** Customer types allowed in SI create/update customers block. */
    public static final Set<CustomerType> ALLOWED_FOR_SI = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    CustomerType.SH,
                    CustomerType.CN,
                    CustomerType.NT,
                    CustomerType.N1,
                    CustomerType.N2,
                    CustomerType.FW,
                    CustomerType.SL,
                    CustomerType.BY
            )));

    /** Required customer types: Shipper, Consignee, Notify. */
    public static final Set<CustomerType> REQUIRED_FOR_SI = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(CustomerType.SH, CustomerType.CN, CustomerType.NT)));

    /** Human-readable list for error messages. */
    public static final String ALLOWED_VALUES_STRING = ALLOWED_FOR_SI.stream()
            .map(CustomerType::getValue)
            .sorted()
            .collect(Collectors.joining(", "));

    private SIBLCustomerTypeConstants() {}
}
