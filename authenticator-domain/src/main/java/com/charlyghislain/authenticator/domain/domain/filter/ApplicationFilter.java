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
}
