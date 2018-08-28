package com.charlyghislain.authenticator.management.api.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class WsPasswordReset {

    @NotNull
    @Size(min = 8)
    private String password;
    @NotNull
    private String resetToken;

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

}
