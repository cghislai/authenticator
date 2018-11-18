package com.charlyghislain.authenticator.domain.domain.exception;

public class UnauthorizedOperationException extends AuthenticatorException {
    public UnauthorizedOperationException() {
    }

    public UnauthorizedOperationException(String message) {
        super(message);
    }

    public UnauthorizedOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedOperationException(Throwable cause) {
        super(cause);
    }
}
