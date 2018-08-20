package com.charlyghislain.authenticator.admin.api.domain;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.ws.rs.QueryParam;

public class WsKeyFilter {

    @Nullable
    @NullableField
    @QueryParam("id")
    private Long id;
    @Nullable
    @NullableField
    @QueryParam("active")
    private Boolean active;
    @Nullable
    @NullableField
    @QueryParam("forApplicationSecrets")
    private Boolean forApplicationSecrets;
    @Nullable
    @NullableField
    @QueryParam("forApplication")
    private Boolean forApplication;
    @Nullable
    @NullableField
    @QueryParam("applicationId")
    private Long applicationId;
    @Nullable
    @NullableField
    @QueryParam("name")
    private String name;
    @Nullable
    @NullableField
    @QueryParam("nameContains")
    private String nameContains;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getForApplicationSecrets() {
        return forApplicationSecrets;
    }

    public void setForApplicationSecrets(Boolean forApplicationSecrets) {
        this.forApplicationSecrets = forApplicationSecrets;
    }

    public Boolean getForApplication() {
        return forApplication;
    }

    public void setForApplication(Boolean forApplication) {
        this.forApplication = forApplication;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameContains() {
        return nameContains;
    }

    public void setNameContains(String nameContains) {
        this.nameContains = nameContains;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
}
