package com.khouss.UsersMicroservice.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CniValidator implements ConstraintValidator<ValidCni, String> {

    private static final String CNI_REGEX = "^[12][0-9]{12}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true; // optionnel: non requis
        return value.matches(CNI_REGEX);
    }
}

