package com.charlyghislain.authenticator.domain.domain.filter;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ApplicationFilter {

    @Nullable
    private Long id;
    @Nullable
    private Boolean active;
    @Nullable
    private String name;
    @Nullable
    private String nameContains;
    @Nullable
    private String applicationUrl;

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
    public String getNameContains() {
        return nameContains;
    }

    public void setNameContains(String nameContains) {
        this.nameContains = nameContains;
    }

    @Nullable
    public String getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }
}
