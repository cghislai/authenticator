package com.charlyghislain.authenticator.application.api.domain;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class WsHealthCheckStatus {

    @NotNull
    private String name;
    @NotNull
    private WsHealthStatus state;
    @NotNull
    private Map<String, Object> data = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WsHealthStatus getState() {
        return state;
    }

    public void setState(WsHealthStatus state) {
        this.state = state;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
