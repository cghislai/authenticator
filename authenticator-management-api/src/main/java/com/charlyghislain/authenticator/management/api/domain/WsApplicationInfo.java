package com.charlyghislain.authenticator.management.api.domain;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class WsApplicationInfo implements Serializable {

    @NotNull
    private String name;
    @NotNull
    private String endpointUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

}
