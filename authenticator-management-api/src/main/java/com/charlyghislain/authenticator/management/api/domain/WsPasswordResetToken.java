package com.charlyghislain.authenticator.management.api.domain;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public class WsPasswordResetToken {

    @NotNull
    private String token;
    @NotNull
    private ZonedDateTime creationTime;
    @NotNull
    private ZonedDateTime expirationTime;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ZonedDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(ZonedDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public ZonedDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(ZonedDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }
}
