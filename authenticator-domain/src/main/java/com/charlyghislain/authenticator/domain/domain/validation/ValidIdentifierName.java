package com.charlyghislain.authenticator.domain.domain.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {IdentifierNameValidator.class})
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ValidIdentifierName {

    String message() default "com.charlyghislain.authenticator.domain.domain.validation.ValidIdentifierName.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
