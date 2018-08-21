package com.charlyghislain.authenticator.api.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class WsPasswordReset {

    @NotNull
    @Size(min = 8)
    private String password;
    @NotNull
    private String resetToken;
    @NotNull
    private Long userId;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
