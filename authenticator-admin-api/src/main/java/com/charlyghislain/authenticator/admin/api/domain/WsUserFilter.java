package com.charlyghislain.authenticator.admin.api.domain;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.ws.rs.QueryParam;

public class WsUserFilter {

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
    @QueryParam("name")
    private String name;
    @Nullable
    @NullableField
    @QueryParam("email")
    private String email;
    @Nullable
    @NullableField
    @QueryParam("nameContains")
    private String nameContains;
    @Nullable
    @NullableField
    @QueryParam("passwordExpired")
    private Boolean passwordExpired;
    @Nullable
    @NullableField
    @QueryParam("admin")
    private Boolean admin;

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

    public String getNameContains() {
        return nameContains;
    }

    public void setNameContains(String nameContains) {
        this.nameContains = nameContains;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getPasswordExpired() {
        return passwordExpired;
    }

    public void setPasswordExpired(Boolean passwordExpired) {
        this.passwordExpired = passwordExpired;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
