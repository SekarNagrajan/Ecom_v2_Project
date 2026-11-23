package com.solverminds.sibl.dto.validation;

import com.solverminds.sibl.dto.SIBLRequestDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * POL (loadPortId) and POD (dischargePortId) must be different (same as Booking DifferentPortsValidator).
 */
public class SIBLPolPodValidator implements ConstraintValidator<ValidSIBLPolPod, SIBLRequestDTO> {

    @Override
    public boolean isValid(SIBLRequestDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true;
        String pol = dto.getLoadPortId();
        String pod = dto.getDischargePortId();
        if (pol == null || pod == null) return true;
        if (pol.trim().isEmpty() || pod.trim().isEmpty()) return true;
        if (pol.equalsIgnoreCase(pod)) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate("POL and POD cannot be the same")
                .addPropertyNode("loadPortId").addConstraintViolation();
            return false;
        }
        return true;
    }
}
