package com.charlyghislain.authenticator.application.client;


import com.charlyghislain.authenticator.application.api.AuthorizationResource;
import com.charlyghislain.authenticator.application.api.HealthResource;
import com.charlyghislain.authenticator.application.api.domain.WsHealthCheckResponse;
import com.charlyghislain.authenticator.application.api.domain.WsHealthCheckStatus;
import com.charlyghislain.authenticator.application.client.converter.ApplicationAuthenticatorAuthorizationHealthConverter;
import com.charlyghislain.authenticator.application.client.converter.ApplicationHealthConverter;
import com.charlyghislain.authenticator.application.client.provider.RestClientProvider;
import com.charlyghislain.authenticator.domain.client.ApplicationHealthClient;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationAuthenticatorAuthorizationHealth;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationHealth;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ApplicationHealthWsClient implements ApplicationHealthClient {

    @Inject
    private RestClientProvider restClientProvider;
    @Inject
    private ApplicationHealthConverter applicationHealthConverter;
    @Inject
    private ApplicationAuthenticatorAuthorizationHealthConverter applicationAuthenticatorAuthorizationHealthConverter;

    @PostConstruct
    public void init() {
    }

    @Override
    public ApplicationAuthenticatorAuthorizationHealth checkAuthenticatorAuthorizationHealth(Application application, String authToken) {
        try {
            AuthorizationResource authorizationResource = restClientProvider.createResource(authToken, application, AuthorizationResource.class);
            WsHealthCheckStatus authenticationCheckStatus = authorizationResource.checkAuthenticatorAuthorization();

            return applicationAuthenticatorAuthorizationHealthConverter
                    .toApplicationAuthenticatorAuthorizationHealth(application, authenticationCheckStatus);
        } catch (Exception e) {
            return applicationAuthenticatorAuthorizationHealthConverter.forAuthorizationError(application, e);
        }
    }

    @Override
    public ApplicationHealth checkApplicationHealth(Application application, String authToken) {
        try {
            HealthResource healthResource = restClientProvider.createResource(authToken, application, HealthResource.class);
            WsHealthCheckResponse authenticationCheckStatus = healthResource.getHealth();

            return applicationHealthConverter.toApplicationHealth(application, authenticationCheckStatus);
        } catch (Exception e) {
            return applicationHealthConverter.forApplicationHealthError(application, e);
        }
    }

}
