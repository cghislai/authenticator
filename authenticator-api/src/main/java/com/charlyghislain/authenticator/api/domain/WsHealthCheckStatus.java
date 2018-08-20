package com.charlyghislain.authenticator.api.domain;

import java.util.Map;

public class WsHealthCheckStatus {

    private String name;
    private WsHealthStatus state;
    private Map<String, Object> data;

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
