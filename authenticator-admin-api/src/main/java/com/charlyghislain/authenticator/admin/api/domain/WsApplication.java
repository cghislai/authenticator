package com.charlyghislain.authenticator.admin.api.domain;


import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class WsApplication implements Serializable {

    @Nullable
    @NullableField
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String endpointUrl;
    @NotNull
    private Boolean active;
    @Nullable
    @NullableField
    private ZonedDateTime creationDateTime;
    @NotNull
    private Boolean canResetUserPassword;
    @NotNull
    private Boolean canVerifyUserEmail;
    @NotNull
    private Boolean addedUsersAreActive;
    @NotNull
    private Boolean existingUsersAreAddedOnTokenRequest;

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

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public Boolean getActive() {
        return active;
    }

    @Nullable
    public ZonedDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(ZonedDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public Boolean getCanResetUserPassword() {
        return canResetUserPassword;
    }

    public void setCanResetUserPassword(Boolean canResetUserPassword) {
        this.canResetUserPassword = canResetUserPassword;
    }

    public Boolean getCanVerifyUserEmail() {
        return canVerifyUserEmail;
    }

    public void setCanVerifyUserEmail(Boolean canVerifyUserEmail) {
        this.canVerifyUserEmail = canVerifyUserEmail;
    }

    public Boolean getAddedUsersAreActive() {
        return addedUsersAreActive;
    }

    public void setAddedUsersAreActive(Boolean addedUsersAreActive) {
        this.addedUsersAreActive = addedUsersAreActive;
    }

    public Boolean getExistingUsersAreAddedOnTokenRequest() {
        return existingUsersAreAddedOnTokenRequest;
    }

    public void setExistingUsersAreAddedOnTokenRequest(Boolean existingUsersAreAddedOnTokenRequest) {
        this.existingUsersAreAddedOnTokenRequest = existingUsersAreAddedOnTokenRequest;
    }
}
