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
import org.checkerframework.checker.nullness.qual.NonNull;

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

    @NonNull
    @Override
    public ApplicationAuthenticatorAuthorizationHealth checkAuthenticatorAuthorizationHealth(@NonNull Application application, String authToken) {
        try {
            AuthorizationResource authorizationResource = restClientProvider.createResource(authToken, application, AuthorizationResource.class);
            WsHealthCheckStatus authenticationCheckStatus = authorizationResource.checkAuthenticatorAuthorization();

            return applicationAuthenticatorAuthorizationHealthConverter
                    .toApplicationAuthenticatorAuthorizationHealth(application, authenticationCheckStatus);
        } catch (Exception e) {
            return applicationAuthenticatorAuthorizationHealthConverter.forAuthorizationError(application, e);
        }
    }

    @NonNull
    @Override
    public ApplicationHealth checkApplicationHealth(@NonNull Application application, String authToken) {
        try {
            HealthResource healthResource = restClientProvider.createResource(authToken, application, HealthResource.class);
            WsHealthCheckResponse authenticationCheckStatus = healthResource.getHealth();

            return applicationHealthConverter.toApplicationHealth(application, authenticationCheckStatus);
        } catch (Exception e) {
            return applicationHealthConverter.forApplicationHealthError(application, e);
        }
    }

}
