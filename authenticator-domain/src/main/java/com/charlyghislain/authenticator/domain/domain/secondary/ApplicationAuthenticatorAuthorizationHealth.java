package com.charlyghislain.authenticator.domain.domain.secondary;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApplicationAuthenticatorAuthorizationHealth implements Serializable {

    @NotNull
    private String applicationName;
    private boolean reachable;
    private boolean authorizationHealthy;
    @NotNull
    private List<String> errors = new ArrayList<>();

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }

    public boolean isAuthorizationHealthy() {
        return authorizationHealthy;
    }

    public void setAuthorizationHealthy(boolean authorizationHealthy) {
        this.authorizationHealthy = authorizationHealthy;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
