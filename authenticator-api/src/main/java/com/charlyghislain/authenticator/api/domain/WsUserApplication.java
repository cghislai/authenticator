package com.charlyghislain.authenticator.api.domain;

import javax.validation.constraints.NotNull;

public class WsUserApplication {

    @NotNull
    private Long applicationId;
    @NotNull
    private String applicationName;


    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }


}
