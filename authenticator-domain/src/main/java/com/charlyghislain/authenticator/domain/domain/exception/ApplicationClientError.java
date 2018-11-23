package com.charlyghislain.authenticator.domain.domain.exception;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ApplicationClientError extends AuthenticatorRuntimeException {

    private int statusCode;
    private String errorCode;
    @Nullable
    private String description;

    public ApplicationClientError(int statusCode, String errorCode, @Nullable String description) {
        super();
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.description = description;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }
}
