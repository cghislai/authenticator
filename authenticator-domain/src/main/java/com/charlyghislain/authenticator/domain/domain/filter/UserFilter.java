package com.charlyghislain.authenticator.domain.domain.filter;

import org.checkerframework.checker.nullness.qual.Nullable;

public class UserFilter {


    @Nullable
    private Long id;
    @Nullable
    private Boolean active;
    @Nullable
    private String name;
    @Nullable
    private String email;
    @Nullable
    private String nameContains;
    @Nullable
    private Boolean passwordExpired;
    @Nullable
    private Boolean admin;

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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Nullable
    public String getNameContains() {
        return nameContains;
    }

    public void setNameContains(String nameContains) {
        this.nameContains = nameContains;
    }

    @Nullable
    public Boolean getPasswordExpired() {
        return passwordExpired;
    }

    public void setPasswordExpired(Boolean passwordExpired) {
        this.passwordExpired = passwordExpired;
    }

    @Nullable
    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
