package com.charlyghislain.authenticator.management.api.domain;

public class WsApplicationUserWithPassword extends WsApplicationUser {

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
