package com.charlyghislain.authenticator.domain.domain.exception;

public class AuthenticatorRuntimeException extends RuntimeException {
    public AuthenticatorRuntimeException() {
    }

    public AuthenticatorRuntimeException(String message) {
        super(message);
    }

    public AuthenticatorRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticatorRuntimeException(Throwable cause) {
        super(cause);
    }

    public AuthenticatorRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
