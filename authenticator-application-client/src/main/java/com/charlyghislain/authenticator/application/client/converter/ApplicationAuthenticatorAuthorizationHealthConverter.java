package com.charlyghislain.authenticator.application.client.converter;

import com.charlyghislain.authenticator.application.api.domain.WsHealthCheckStatus;
import com.charlyghislain.authenticator.application.api.domain.WsHealthStatus;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationAuthenticatorAuthorizationHealth;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ApplicationAuthenticatorAuthorizationHealthConverter {

    public static final String HEALTH_DATA_ERROR_KEY = "error";

    public ApplicationAuthenticatorAuthorizationHealth forAuthorizationError(Application application, Throwable e) {
        List<String> errorMessages = Collections.singletonList(e.getMessage());

        ApplicationAuthenticatorAuthorizationHealth providerAuthorizationHealth = new ApplicationAuthenticatorAuthorizationHealth();
        providerAuthorizationHealth.setApplicationName(application.getName());
        providerAuthorizationHealth.setReachable(isReachable(e));
        providerAuthorizationHealth.setErrors(errorMessages);
        return providerAuthorizationHealth;
    }

    public ApplicationAuthenticatorAuthorizationHealth toApplicationAuthenticatorAuthorizationHealth(Application application,
                                                                                                     WsHealthCheckStatus checkStatus) {
        boolean authorizationHealthy = checkStatus.getState() == WsHealthStatus.UP;
        List<String> errorMessages = checkStatus.getData().entrySet()
                .stream()
                .filter(e -> e.getKey().equals(HEALTH_DATA_ERROR_KEY))
                .map(e -> (String) e.getValue())
                .collect(Collectors.toList());

        ApplicationAuthenticatorAuthorizationHealth authorizationHealth = new ApplicationAuthenticatorAuthorizationHealth();
        authorizationHealth.setApplicationName(application.getName());
        authorizationHealth.setReachable(true);
        authorizationHealth.setErrors(errorMessages);
        authorizationHealth.setAuthorizationHealthy(authorizationHealthy);

        return authorizationHealth;
    }


    private boolean isReachable(Throwable e) {
        if (e instanceof WebApplicationException) {
            WebApplicationException webApplicationException = (WebApplicationException) e;
            return webApplicationException.getResponse() != null;
        } else {
            return false;
        }
    }

}
