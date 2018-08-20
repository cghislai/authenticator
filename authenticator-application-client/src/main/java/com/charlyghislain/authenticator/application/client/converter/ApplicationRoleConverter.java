package com.charlyghislain.authenticator.application.client.converter;


import com.charlyghislain.authenticator.application.api.domain.WsApplicationRole;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationRole;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationRoleConverter {

    public ApplicationRole toApplicationRole(WsApplicationRole wsApplicationRole, Application application) {
        String name = wsApplicationRole.getName();

        ApplicationRole applicationRole = new ApplicationRole();
        applicationRole.setApplication(application);
        applicationRole.setRoleName(name);
        return applicationRole;
    }
}
