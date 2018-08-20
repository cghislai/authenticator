package com.charlyghislain.authenticator.domain.domain.validation;


import com.charlyghislain.authenticator.domain.domain.util.CharacterSequences;
import com.charlyghislain.authenticator.domain.domain.util.CharacterValidationUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.length() < 8) {
            return false;
        }
        boolean onlyAlphaNumeric = CharacterValidationUtils.allMatch(value, CharacterSequences.ALPHANUMERIC);
        return !onlyAlphaNumeric;
    }
}
