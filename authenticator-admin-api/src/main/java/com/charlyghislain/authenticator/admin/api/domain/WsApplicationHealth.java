package com.charlyghislain.authenticator.admin.api.domain;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class WsApplicationHealth implements Serializable {

    private boolean reachable;
    @NotNull
    private String applicationName;
    private boolean applicationHealthy;
    @Nullable
    @NullableField
    private String applicationHealthError;

    private boolean authenticatorAuthorized;
    @NotNull
    private List<String> autenticatorAuthorizationErrors;

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public boolean isApplicationHealthy() {
        return applicationHealthy;
    }

    public void setApplicationHealthy(boolean applicationHealthy) {
        this.applicationHealthy = applicationHealthy;
    }


    public boolean isAuthenticatorAuthorized() {
        return authenticatorAuthorized;
    }

    public void setAuthenticatorAuthorized(boolean authenticatorAuthorized) {
        this.authenticatorAuthorized = authenticatorAuthorized;
    }

    @Nullable
    public String getApplicationHealthError() {
        return applicationHealthError;
    }

    public void setApplicationHealthError(@Nullable String applicationHealthError) {
        this.applicationHealthError = applicationHealthError;
    }

    public List<String> getAutenticatorAuthorizationErrors() {
        return autenticatorAuthorizationErrors;
    }

    public void setAutenticatorAuthorizationErrors(List<String> autenticatorAuthorizationErrors) {
        this.autenticatorAuthorizationErrors = autenticatorAuthorizationErrors;
    }
}
