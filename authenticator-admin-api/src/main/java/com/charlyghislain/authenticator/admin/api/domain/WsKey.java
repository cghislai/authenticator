package com.charlyghislain.authenticator.admin.api.domain;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class WsKey {

    @Nullable
    @NullableField
    private Long id;
    @NotNull
    private String name;
    private boolean active;
    private boolean forApplicationSecrets;
    @NotNull
    private LocalDateTime creationDateTime;

    private Long applicationId;

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

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }
}
