package com.charlyghislain.authenticator.admin.api.domain;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;

public class WsUserApplication {

    @Nullable
    @NullableField
    private Long id;
    @NotNull
    private Long applicationId;
    @NotNull
    private String applicationName;
    @NotNull
    private Long userId;
    @NotNull
    private String userName;
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
