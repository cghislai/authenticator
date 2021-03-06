package com.charlyghislain.authenticator.admin.web.converter;

import com.charlyghislain.authenticator.admin.api.domain.WsApplicationHealth;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationAuthenticatorAuthorizationHealth;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationHealth;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WsApplicationHealthConverter {

    @NonNull
    public WsApplicationHealth toWsApplicationHealth(
            @NonNull ApplicationHealth applicationHealth,
            @NonNull ApplicationAuthenticatorAuthorizationHealth authorizationHealth) {

        boolean reachable = applicationHealth.isReachable() || authorizationHealth.isReachable();

        WsApplicationHealth wsApplicationHealth = new WsApplicationHealth();
        wsApplicationHealth.setReachable(reachable);
        wsApplicationHealth.setApplicationName(applicationHealth.getApplicationName());
        wsApplicationHealth.setApplicationHealthy(applicationHealth.isHealthy());
        wsApplicationHealth.setApplicationHealthError(applicationHealth.getError());
        wsApplicationHealth.setAuthenticatorAuthorized(authorizationHealth.isAuthorizationHealthy());
        wsApplicationHealth.setAutenticatorAuthorizationErrors(authorizationHealth.getErrors());
        return wsApplicationHealth;
    }
}
