package com.charlyghislain.authenticator.api.error;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;

public class AuthenticatorWebException extends RuntimeException {

    private int status;
    @NotNull
    private String code;
    @Nullable
    private String description;


    public AuthenticatorWebException(AuthenticatorWebError webError) {
        this.status = webError.getHttpStatus();
        this.code = webError.name();
    }

    public AuthenticatorWebException(AuthenticatorWebError webError, Throwable cause) {
        super(cause);
        this.status = webError.getHttpStatus();
        this.code = webError.name();
    }

    public AuthenticatorWebException(AuthenticatorWebError webError, String description) {
        this.status = webError.getHttpStatus();
        this.code = webError.name();
        this.description = description;
    }

    public AuthenticatorWebException(int status, String code, String description) {
        this.status = status;
        this.code = code;
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    @Nullable
    public String getDescription() {
        return description;
    }
}
