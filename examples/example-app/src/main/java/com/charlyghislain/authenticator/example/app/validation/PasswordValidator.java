package com.charlyghislain.authenticator.example.app.validation;


import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(@Nullable String value, ConstraintValidatorContext context) {
        if (value == null || value.length() < 8) {
            return false;
        }
        boolean onlyAlphaNumeric = CharacterValidationUtils.allMatch(value, CharacterSequences.ALPHANUMERIC);
        return !onlyAlphaNumeric;
    }
}
