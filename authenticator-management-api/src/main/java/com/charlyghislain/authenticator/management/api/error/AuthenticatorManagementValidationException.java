package com.charlyghislain.authenticator.management.api.error;

import com.charlyghislain.authenticator.management.api.domain.WsViolationError;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class AuthenticatorManagementValidationException extends RuntimeException {

    @NotNull
    private String code;
    private final Set<WsViolationError> wsViolationErrors;

    public AuthenticatorManagementValidationException(@NotNull String code, Set<WsViolationError> wsViolationErrors) {
        this.code = code;
        this.wsViolationErrors = wsViolationErrors;
    }

    public Set<WsViolationError> getWsViolationErrors() {
        return wsViolationErrors;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
