package com.charlyghislain.authenticator.example.app.domain;

public class WsPasswordResetTokenWithUid {
    String token;
    Long userId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
