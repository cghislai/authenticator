package com.charlyghislain.authenticator.application.client.converter;


import com.charlyghislain.authenticator.application.api.domain.WsHealthCheckResponse;
import com.charlyghislain.authenticator.application.api.domain.WsHealthStatus;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationHealth;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationHealthConverter {


    @NonNull
    public ApplicationHealth forApplicationHealthError(@NonNull Application application, @NonNull Throwable throwable) {
        ApplicationHealth applicationHealth = new ApplicationHealth();
        applicationHealth.setApplicationName(application.getName());
        applicationHealth.setReachable(false);
        applicationHealth.setHealthy(false);
        applicationHealth.setError(throwable.getMessage());
        return applicationHealth;
    }

    @NonNull
    public ApplicationHealth toApplicationHealth(@NonNull Application application, @NonNull WsHealthCheckResponse healthCheckResponse) {
        ApplicationHealth applicationHealth = new ApplicationHealth();
        applicationHealth.setApplicationName(application.getName());
        applicationHealth.setReachable(true);
        applicationHealth.setHealthy(healthCheckResponse.getOutcome() == WsHealthStatus.UP);
        return applicationHealth;
    }


}
