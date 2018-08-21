package com.charlyghislain.authenticator.example.app.domain;

import com.charlyghislain.authenticator.example.app.validation.ValidPassword;

import javax.validation.constraints.NotNull;

public class WsRegistration {
    @NotNull
    public String name;
    @NotNull
    public String email;
    @NotNull
    @ValidPassword
    public String password;
}
