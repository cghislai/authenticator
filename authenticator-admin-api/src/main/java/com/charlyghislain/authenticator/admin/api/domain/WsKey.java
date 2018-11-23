package com.charlyghislain.authenticator.admin.api.domain;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public class WsKey {

    @Nullable
    @NullableField
    private Long id;
    @NotNull
    private String name;
    private boolean active;
    private boolean signingKey;
    private boolean forApplicationSecrets;
    @Nullable
    @NullableField
    private ZonedDateTime creationDateTime;
    @Nullable
    @NullableField
    private Long applicationId;

    @Nullable
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isForApplicationSecrets() {
        return forApplicationSecrets;
    }

    public void setForApplicationSecrets(boolean forApplicationSecrets) {
        this.forApplicationSecrets = forApplicationSecrets;
    }

    @Nullable
    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    @Nullable
    public ZonedDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(ZonedDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public boolean isSigningKey() {
        return signingKey;
    }

    public void setSigningKey(boolean signingKey) {
        this.signingKey = signingKey;
    }
}
