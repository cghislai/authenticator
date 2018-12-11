package com.charlyghislain.authenticator.domain.domain.exception;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class ValidationException extends AuthenticatorException {

    private Set<ConstraintViolation<?>> constraintViolations;

    public ValidationException(Set<ConstraintViolation<?>> constraintViolations) {
        this.constraintViolations = constraintViolations;
    }

    public Set<ConstraintViolation<?>> getConstraintViolations() {
        return constraintViolations;
    }
}
