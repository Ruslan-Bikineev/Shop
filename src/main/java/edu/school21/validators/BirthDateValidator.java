package edu.school21.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class BirthDateValidator implements ConstraintValidator<BirthDate, LocalDate> {
    @Override
    public boolean isValid(final LocalDate valueToValidate, final ConstraintValidatorContext context) {
        Period period = Period.between(valueToValidate, LocalDate.now());
        return period.getYears() >= 18;
    }
}