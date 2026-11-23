package com.solverminds.sibl.dto.validation;

import com.solverminds.sibl.dto.SIBLContainerDTO;
import com.solverminds.sibl.dto.SIBLRequestDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Duplicate container within same SI: container numbers must be unique per SI (trim, uppercase).
 * Same logic as SIValidationService.validateDuplicateContainersWithinSI.
 */
public class SIBLDuplicateContainersValidator implements ConstraintValidator<ValidSIBLDuplicateContainers, SIBLRequestDTO> {

    @Override
    public boolean isValid(SIBLRequestDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null || dto.getContainers() == null || dto.getContainers().isEmpty()) return true;

        Set<String> seen = new HashSet<>();
        Set<String> duplicates = new LinkedHashSet<>();
        for (SIBLContainerDTO c : dto.getContainers()) {
            String no = c.getContainerNo();
            if (no == null || no.trim().isEmpty()) continue;
            String n = no.trim().toUpperCase();
            if (seen.contains(n)) duplicates.add(n);
            seen.add(n);
        }
        if (duplicates.isEmpty()) return true;
        ctx.disableDefaultConstraintViolation();
        for (String dup : duplicates) {
            ctx.buildConstraintViolationWithTemplate("Duplicate Container No " + dup + " within the same Shipping Instruction.")
                .addPropertyNode("containers").addConstraintViolation();
        }
        return false;
    }
}
