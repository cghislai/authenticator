package com.charlyghislain.authenticator.application.api.domain;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class WsApplicationRole implements Serializable {

    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
