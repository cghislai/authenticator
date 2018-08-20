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

    public Boolean getSigningKey() {
        return signingKey;
    }

    public void setSigningKey(Boolean signingKey) {
        this.signingKey = signingKey;
    }

    public Boolean getForApplication() {
        return forApplication;
    }

    public void setForApplication(Boolean forApplication) {
        this.forApplication = forApplication;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
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
