package com.charlyghislain.authenticator.admin.api.domain;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.ws.rs.QueryParam;
import java.io.Serializable;

public class WsApplicationFilter implements Serializable {

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
    @QueryParam("nameContains")
    private String nameContains;

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
}
