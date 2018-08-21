package com.charlyghislain.authenticator.example.app.health;

import com.charlyghislain.authenticator.example.app.client.AuthenticatorManagementClient;
import com.charlyghislain.authenticator.management.api.domain.WsHealthCheckStatus;
import com.charlyghislain.authenticator.management.api.domain.WsHealthStatus;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Health
@ApplicationScoped
public class AuthenticatorConnectivityHealthCheck implements HealthCheck {

    @Inject
    private AuthenticatorManagementClient authenticatorManagementClient;


    @Override
    public HealthCheckResponse call() {
        WsHealthCheckStatus wsHealthCheckStatus = authenticatorManagementClient.checkConnectivity();
        WsHealthStatus state = wsHealthCheckStatus.getState();

        return HealthCheckResponse.builder()
                .state(state == WsHealthStatus.UP)
                .name("authenticator connectivity")
                .build();
    }
}
