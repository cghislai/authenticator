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

    @Nullable
    public Boolean getActive() {
        return active;
    }

    public void setActive(@Nullable Boolean active) {
        this.active = active;
    }

    @NonNull
    public ApplicationFilter getApplicationFilter() {
        return applicationFilter;
    }

    public void setApplicationFilter(@NonNull ApplicationFilter applicationFilter) {
        this.applicationFilter = applicationFilter;
    }

    @NonNull
    public UserFilter getUserFilter() {
        return userFilter;
    }

    public void setUserFilter(@NonNull UserFilter userFilter) {
        this.userFilter = userFilter;
    }

    @Nullable
    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    @Nullable
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
