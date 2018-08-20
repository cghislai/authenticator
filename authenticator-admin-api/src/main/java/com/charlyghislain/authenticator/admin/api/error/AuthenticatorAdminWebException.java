package com.charlyghislain.authenticator.admin.api.error;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;

public class AuthenticatorAdminWebException extends RuntimeException {

    private int status;
    @NotNull
    private String code;
    @Nullable
    private String description;


    public AuthenticatorAdminWebException(AuthenticatorAdminWebError webError) {
        this.status = webError.getHttpStatus();
        this.code = webError.name();
    }

    public AuthenticatorAdminWebException(AuthenticatorAdminWebError webError, String description) {
        this.status = webError.getHttpStatus();
        this.code = webError.name();
        this.description = description;
    }

    public AuthenticatorAdminWebException(int status, String code, String description) {
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

    public String getDescription() {
        return description;
    }
}
