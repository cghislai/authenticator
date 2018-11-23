package com.charlyghislain.authenticator.application.client.converter;


import com.charlyghislain.authenticator.application.api.domain.WsApplicationRole;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationRole;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationRoleConverter {

    @NonNull
    public ApplicationRole toApplicationRole(@NonNull WsApplicationRole wsApplicationRole, Application application) {
        String name = wsApplicationRole.getName();

        ApplicationRole applicationRole = new ApplicationRole();
        applicationRole.setApplication(application);
        applicationRole.setRoleName(name);
        return applicationRole;
    }
}
