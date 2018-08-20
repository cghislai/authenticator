package com.charlyghislain.authenticator.domain.domain.validation;


import com.charlyghislain.authenticator.domain.domain.util.CharacterSequences;
import com.charlyghislain.authenticator.domain.domain.util.CharacterValidationUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IdentifierNameValidator implements ConstraintValidator<ValidIdentifierName, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return CharacterValidationUtils.allMatch(value, CharacterSequences.ALPHANUMERIC_WITH_DASH_UNDERSCORE);
    }
}
