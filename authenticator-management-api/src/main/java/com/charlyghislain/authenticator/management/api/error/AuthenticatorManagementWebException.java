package com.charlyghislain.authenticator.management.api.error;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;

public class AuthenticatorManagementWebException extends RuntimeException {

    private int status;
    @NotNull
    private String code;
    @Nullable
    private String description;


    public AuthenticatorManagementWebException(AuthenticatorManagementWebError webError) {
        this.status = webError.getHttpStatus();
        this.code = webError.name();
    }

    public AuthenticatorManagementWebException(AuthenticatorManagementWebError webError, Throwable cause) {
        super(cause);
        this.status = webError.getHttpStatus();
        this.code = webError.name();
    }

    public AuthenticatorManagementWebException(AuthenticatorManagementWebError webError, String description) {
        this.status = webError.getHttpStatus();
        this.code = webError.name();
        this.description = description;
    }

    public AuthenticatorManagementWebException(int status, String code, String description) {
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
