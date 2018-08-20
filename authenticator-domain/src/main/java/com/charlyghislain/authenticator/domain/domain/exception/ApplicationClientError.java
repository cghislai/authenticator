package com.charlyghislain.authenticator.domain.domain.exception;

public class ApplicationClientError extends AuthenticatorRuntimeException {

    private int statusCode;
    private String errorCode;
    private String description;

    public ApplicationClientError(int statusCode, String errorCode, String description) {
        super(description);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.description = description;
    }
}
