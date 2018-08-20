package com.charlyghislain.authenticator.management.api.domain;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;

public class WsApplicationUser {

    @Nullable
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String email;
    private boolean active;
    private boolean emailVerified;
    private boolean passwordExpired;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isPasswordExpired() {
        return passwordExpired;
    }

    public void setPasswordExpired(boolean passwordExpired) {
        this.passwordExpired = passwordExpired;
    }
}
