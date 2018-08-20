package com.charlyghislain.authenticator.application.client.converter;


import com.charlyghislain.authenticator.application.api.domain.WsHealthCheckResponse;
import com.charlyghislain.authenticator.application.api.domain.WsHealthStatus;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationHealth;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationHealthConverter {


    public ApplicationHealth forApplicationHealthError(Application application, Throwable throwable) {
        ApplicationHealth applicationHealth = new ApplicationHealth();
        applicationHealth.setApplicationName(application.getName());
        applicationHealth.setReachable(false);
        applicationHealth.setHealthy(false);
        applicationHealth.setError(throwable.getMessage());
        return applicationHealth;
    }

    public ApplicationHealth toApplicationHealth(Application application, WsHealthCheckResponse healthCheckResponse) {
        ApplicationHealth applicationHealth = new ApplicationHealth();
        applicationHealth.setApplicationName(application.getName());
        applicationHealth.setReachable(true);
        applicationHealth.setHealthy(healthCheckResponse.getOutcome() == WsHealthStatus.UP);
        return applicationHealth;
    }


}
