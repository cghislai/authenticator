package com.charlyghislain.authenticator.domain.domain.filter;

import com.charlyghislain.authenticator.domain.domain.User;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PasswordResetTokenFilter {

    @Nullable
    private Long id;
    @Nullable
    private Boolean active;
    @Nullable
    private String token;
    @Nullable
    private User user;

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Nullable
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Nullable
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Nullable
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
