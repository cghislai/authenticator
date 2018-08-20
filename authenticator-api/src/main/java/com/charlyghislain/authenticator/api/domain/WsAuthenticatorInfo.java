package com.charlyghislain.authenticator.api.domain;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class WsAuthenticatorInfo implements Serializable {

    @NotNull
    private String name;
    @NotNull
    private String endpointUrl;
    @NotNull
    private String tokenIssuer;

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

    public String getTokenIssuer() {
        return tokenIssuer;
    }

    public void setTokenIssuer(String tokenIssuer) {
        this.tokenIssuer = tokenIssuer;
    }
}
