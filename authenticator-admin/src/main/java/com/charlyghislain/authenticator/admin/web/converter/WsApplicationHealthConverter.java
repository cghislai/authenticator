package com.charlyghislain.authenticator.admin.web.converter;

import com.charlyghislain.authenticator.admin.api.domain.WsApplicationHealth;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationAuthenticatorAuthorizationHealth;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationHealth;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WsApplicationHealthConverter {

    public WsApplicationHealth toWsApplicationHealth(
            ApplicationHealth applicationHealth,
            ApplicationAuthenticatorAuthorizationHealth authorizationHealth) {

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
