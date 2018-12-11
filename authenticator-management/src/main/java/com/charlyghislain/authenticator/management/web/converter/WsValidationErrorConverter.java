package com.charlyghislain.authenticator.management.web.converter;

import com.charlyghislain.authenticator.management.api.domain.WsViolationError;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ConstraintViolation;

@ApplicationScoped
public class WsValidationErrorConverter {

    public WsViolationError toWsValidationError(ConstraintViolation constraintViolation) {
        String propertyPath = constraintViolation.getPropertyPath().toString();
        String messageTemplate = constraintViolation.getMessageTemplate();

        WsViolationError validationError = new WsViolationError(propertyPath, messageTemplate);
        return validationError;
    }
}
