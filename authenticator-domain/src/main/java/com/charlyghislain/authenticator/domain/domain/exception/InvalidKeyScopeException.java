package com.charlyghislain.authenticator.domain.domain.exception;

public class InvalidKeyScopeException extends AuthenticatorException {
    public InvalidKeyScopeException() {
    }

    public InvalidKeyScopeException(String message) {
        super(message);
    }

    public InvalidKeyScopeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidKeyScopeException(Throwable cause) {
        super(cause);
    }
}
