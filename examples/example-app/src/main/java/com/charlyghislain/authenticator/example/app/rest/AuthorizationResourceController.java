package com.charlyghislain.authenticator.example.app.rest;

import com.charlyghislain.authenticator.application.api.AuthorizationResource;
import com.charlyghislain.authenticator.application.api.domain.WsApplicationRole;
import com.charlyghislain.authenticator.application.api.domain.WsHealthCheckStatus;
import com.charlyghislain.authenticator.application.api.domain.WsHealthStatus;
import com.charlyghislain.authenticator.example.app.ApplicationRoles;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.security.RolesAllowed;
import java.util.Collections;
import java.util.List;

public class AuthorizationResourceController implements AuthorizationResource {

    @NonNull
    @Override
    @RolesAllowed(ApplicationRoles.AUTHENTICATOR_APP_ROLE)
    public List<WsApplicationRole> listUserRoles(long userId) {
        WsApplicationRole role = new WsApplicationRole();
        role.setName(ApplicationRoles.EXAMPLE_ROLE);
        return Collections.singletonList(role);
    }

    @NonNull
    @Override
    @RolesAllowed(ApplicationRoles.AUTHENTICATOR_APP_ROLE)
    public WsHealthCheckStatus checkAuthenticatorAuthorization() {
        WsHealthCheckStatus healthCheckStatus = new WsHealthCheckStatus();
        healthCheckStatus.setName("test-app");
        healthCheckStatus.setState(WsHealthStatus.UP);
        return healthCheckStatus;
    }
}
