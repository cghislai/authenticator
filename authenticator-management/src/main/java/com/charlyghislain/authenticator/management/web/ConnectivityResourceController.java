package com.charlyghislain.authenticator.management.web;

import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.ejb.service.HealthService;
import com.charlyghislain.authenticator.management.api.ConnectivityResource;
import com.charlyghislain.authenticator.management.api.domain.WsApplicationInfo;
import com.charlyghislain.authenticator.management.api.domain.WsHealthCheckStatus;
import com.charlyghislain.authenticator.management.web.converter.WsApplicationInfoConverter;
import com.charlyghislain.authenticator.management.web.converter.WsHealthCheckStatusConverter;
import com.charlyghislain.authenticator.management.web.provider.CallerManagedApplication;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

public class ConnectivityResourceController implements ConnectivityResource {

    @Inject
    private HealthService healthService;
    @Inject
    private WsHealthCheckStatusConverter wsHealthCheckStatusConverter;
    @Inject
    private WsApplicationInfoConverter wsApplicationInfoConverter;
    @Inject
    @CallerManagedApplication
    private Application callerManagedApplication;

    @NonNull
    @Override
    @PermitAll
    public WsHealthCheckStatus authenticationHealthCheck() {
        HealthCheckResponse healthCheckResponse = healthService.checkAuthenticationStatus();
        return wsHealthCheckStatusConverter.toWsHealthCheckStatus(healthCheckResponse);
    }

    @NonNull
    @Override
    @RolesAllowed(AuthenticatorConstants.ROLE_APP_MANAGEMENT)
    public WsApplicationInfo getMyInfo() {
        return wsApplicationInfoConverter.toWsApplicationInfo(callerManagedApplication);
    }

}
