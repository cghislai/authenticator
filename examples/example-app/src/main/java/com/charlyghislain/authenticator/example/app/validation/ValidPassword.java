package com.charlyghislain.authenticator.example.app.validation;


import org.checkerframework.checker.nullness.qual.NonNull;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {PasswordValidator.class})
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    @NonNull String message() default "com.charlyghislain.authenticator.domain.domain.validation.ValidPassword.message";

    @NonNull Class<?>[] groups() default {};

    @NonNull Class<? extends Payload>[] payload() default {};

}
