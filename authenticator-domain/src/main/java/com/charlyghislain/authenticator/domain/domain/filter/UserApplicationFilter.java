package com.charlyghislain.authenticator.domain.domain.filter;

import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.User;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class UserApplicationFilter {


    @Nullable
    private Boolean active;
    @Nullable
    private Application application;
    @Nullable
    private User user;
    @NonNull
    private ApplicationFilter applicationFilter = new ApplicationFilter();
    @NonNull
    private UserFilter userFilter = new UserFilter();

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ApplicationFilter getApplicationFilter() {
        return applicationFilter;
    }

    public void setApplicationFilter(ApplicationFilter applicationFilter) {
        this.applicationFilter = applicationFilter;
    }

    public UserFilter getUserFilter() {
        return userFilter;
    }

    public void setUserFilter(UserFilter userFilter) {
        this.userFilter = userFilter;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
