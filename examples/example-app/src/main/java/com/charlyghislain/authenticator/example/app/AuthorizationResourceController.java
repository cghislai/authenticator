package com.charlyghislain.authenticator.example.app;

import com.charlyghislain.authenticator.application.api.AuthorizationResource;
import com.charlyghislain.authenticator.application.api.domain.WsApplicationRole;
import com.charlyghislain.authenticator.application.api.domain.WsHealthCheckStatus;
import com.charlyghislain.authenticator.application.api.domain.WsHealthStatus;

import javax.annotation.security.RolesAllowed;
import java.util.Collections;
import java.util.List;

public class AuthorizationResourceController implements AuthorizationResource {

    @Override
    @RolesAllowed(ApplicationRoles.AUTHNETICATOR_APP_ROLE)
    public List<WsApplicationRole> listUserRoles(long userId) {
        WsApplicationRole role = new WsApplicationRole();
        role.setName(ApplicationRoles.EXAMPLE_ROLE);
        return Collections.singletonList(role);
    }

    @Override
    @RolesAllowed(ApplicationRoles.AUTHNETICATOR_APP_ROLE)
    public WsHealthCheckStatus checkAuthenticatorAuthorization() {
        WsHealthCheckStatus healthCheckStatus = new WsHealthCheckStatus();
        healthCheckStatus.setName("test-app");
        healthCheckStatus.setState(WsHealthStatus.UP);
        return null;
    }
}
