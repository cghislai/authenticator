package com.charlyghislain.authenticator.ejb.util;

import com.charlyghislain.authenticator.domain.domain.exception.AuthenticatorRuntimeException;
import com.charlyghislain.authenticator.ejb.service.ErrorService;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@AuthenticatorManagedError
@Priority(Interceptor.Priority.APPLICATION)
public class ErrorInterceptor {

    @Inject
    private ErrorService errorService;

    @AroundInvoke
    public Object invokeGuarded(InvocationContext context) throws Exception {
        try {
            return context.proceed();
        } catch (RuntimeException e) {
            AuthenticatorRuntimeException exceptionToRethrow = errorService.handleError(e);
            throw exceptionToRethrow;
        }
    }
}
