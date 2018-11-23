package com.charlyghislain.authenticator.domain.domain.validation;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(@Nullable String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        try {
            InternetAddress emailAddress = new InternetAddress(value);
            emailAddress.validate();
        } catch (AddressException e) {
            return false;
        }
        return true;
    }
}
