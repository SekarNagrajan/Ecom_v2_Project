package com.solverminds.booking.dto.validation;

import com.solverminds.booking.dto.CommodityDTO;
import com.solverminds.booking.dto.v2.BookingContainerCommodityV2;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CargoVolumeRequiresVolumeUnitValidator implements ConstraintValidator<CargoVolumeRequiresVolumeUnit, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value instanceof CommodityDTO) {
            CommodityDTO c = (CommodityDTO) value;
            if (c.getCargoVolume() != null && c.getVolumnUnit() == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addPropertyNode("volumnUnit")
                        .addConstraintViolation();
                return false;
            }
            return true;
        }
        if (value instanceof BookingContainerCommodityV2) {
            BookingContainerCommodityV2 c = (BookingContainerCommodityV2) value;
            if (c.getCargoVolume() != null && c.getCargoVolumeUnit() == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addPropertyNode("cargoVolumeUnit")
                        .addConstraintViolation();
                return false;
            }
            return true;
        }
        return true;
    }
}
