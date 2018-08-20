package com.charlyghislain.authenticator.domain.domain.secondary;

import com.charlyghislain.authenticator.domain.domain.Application;

import javax.validation.constraints.NotNull;

public class ApplicationRole {

    @NotNull
    private Application application;
    @NotNull
    private String roleName;

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}
