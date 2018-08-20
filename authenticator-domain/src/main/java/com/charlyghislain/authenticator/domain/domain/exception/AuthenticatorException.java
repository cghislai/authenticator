package com.charlyghislain.authenticator.domain.domain.exception;

public class AuthenticatorException extends Exception {
    public AuthenticatorException() {
    }

    public AuthenticatorException(String message) {
        super(message);
    }

    public AuthenticatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticatorException(Throwable cause) {
        super(cause);
    }

    public AuthenticatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
