package com.charlyghislain.authenticator.domain.domain.filter;

import com.charlyghislain.authenticator.domain.domain.Application;
import org.checkerframework.checker.nullness.qual.Nullable;

public class KeyFilter {

    @Nullable
    private Long id;
    @Nullable
    private Boolean active;
    @Nullable
    private Boolean signingKey;
    @Nullable
    private Boolean forApplicationSecrets;
    @Nullable
    private Boolean forApplication;
    @Nullable
    private Application application;
    @Nullable
    private String name;
    @Nullable
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
    public Boolean getForApplicationSecrets() {
        return forApplicationSecrets;
    }

    public void setForApplicationSecrets(Boolean forApplicationSecrets) {
        this.forApplicationSecrets = forApplicationSecrets;
    }

    @Nullable
    public Boolean getSigningKey() {
        return signingKey;
    }

    public void setSigningKey(Boolean signingKey) {
        this.signingKey = signingKey;
    }

    @Nullable
    public Boolean getForApplication() {
        return forApplication;
    }

    public void setForApplication(Boolean forApplication) {
        this.forApplication = forApplication;
    }

    @Nullable
    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
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
