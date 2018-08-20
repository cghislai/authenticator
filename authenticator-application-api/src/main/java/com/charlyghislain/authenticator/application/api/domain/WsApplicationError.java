package com.charlyghislain.authenticator.application.api.domain;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class WsApplicationError implements Serializable {

    public static final String APPLICATION_ERROR_HEADER_NAME = "X-Authenticator-Application-Error";

    @NotNull
    private String code;
    @Nullable
    @NullableField
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
