package com.solverminds.booking.dto.validation;

import com.solverminds.booking.dto.RouteDTO;
import com.svm.ecom.common.enums.TransportMode;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator that ensures:
 * 1. POL (loadPortId) and POD (dischargePortId) are not the same
 * 2. Vessel code is required when serviceType is F (Feeder) or M (Mainline)
 * 3. Voyage code is required when serviceType is F (Feeder) or M (Mainline)
 */
public class DifferentPortsValidator implements ConstraintValidator<DifferentPorts, RouteDTO> {

    // Error codes (from ErrorCodes class)
    private static final int VESSEL_CODE_REQUIRED = 4114;
    private static final int VOYAGE_CODE_REQUIRED = 4115;

    @Override
    public void initialize(DifferentPorts constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(RouteDTO route, ConstraintValidatorContext context) {
        if (route == null) {
            return true; // Let other validators handle null objects
        }

        boolean isValid = true;

        // Rule 1: POL and POD must be different
        String loadPortId = route.getLoadPortId();
        String dischargePortId = route.getDischargePortId();

        if (loadPortId != null && !loadPortId.trim().isEmpty() &&
            dischargePortId != null && !dischargePortId.trim().isEmpty()) {
            if (loadPortId.trim().equalsIgnoreCase(dischargePortId.trim())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("POL and POD cannot be the same")
                       .addConstraintViolation();
                isValid = false;
            }
        }

        // Rules 2 & 3: Vessel code and Voyage code are required when serviceType is F (Feeder) or M (Mainline)
        TransportMode serviceType = route.getServiceType();

        if (serviceType != null && (serviceType == TransportMode.F || serviceType == TransportMode.M)) {
            // Rule 2: Vessel code required for F/M
            String vesselCode = route.getVesselCode();
            if (vesselCode == null || vesselCode.trim().isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Vessel name must be selected when serviceType is F (Feeder) or M (Mainline)")
                       .addPropertyNode("vesselCode")
                       .addConstraintViolation();
                isValid = false;
            }

            // Rule 3: Voyage code required for F/M
            String voyageCode = route.getVoyageCode();
            if (voyageCode == null || voyageCode.trim().isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Voyage number is required when serviceType is F (Feeder) or M (Mainline)")
                       .addPropertyNode("voyageCode")
                       .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}
