package com.charlyghislain.authenticator.ejb.health;


import com.charlyghislain.authenticator.domain.client.ApplicationHealthClient;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.filter.ApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationAuthenticatorAuthorizationHealth;
import com.charlyghislain.authenticator.ejb.service.ApplicationQueryService;
import com.charlyghislain.authenticator.ejb.service.JwtTokenService;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Health
@ApplicationScoped
public class ApplicationsConnectivityHealth implements HealthCheck {

    @Inject
    private ApplicationHealthClient applicationHealthClient;
    @Inject
    private ApplicationQueryService applicationApplicationQueryService;
    @Inject
    private JwtTokenService tokenService;

    @Override
    public HealthCheckResponse call() {
        ApplicationFilter ApplicationFilter = new ApplicationFilter();
        ApplicationFilter.setActive(true);

        List<ApplicationAuthenticatorAuthorizationHealth> applicationHealths = applicationApplicationQueryService
                .findAllApplications(ApplicationFilter)
                .stream()
                .map(this::checkApplicationHealth)
                .collect(Collectors.toList());
        boolean allhealthy = applicationHealths.stream()
                .allMatch(ApplicationAuthenticatorAuthorizationHealth::isAuthorizationHealthy);

        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.builder()
                .name("application connectivity")
                .state(allhealthy);

        applicationHealths
                .forEach(applicationHealth -> this.appendApplicationData(responseBuilder, applicationHealth));

        return responseBuilder.build();
    }

    private void appendApplicationData(@NonNull HealthCheckResponseBuilder responseBuilder, ApplicationAuthenticatorAuthorizationHealth applicationHealth) {
        String applicationName = applicationHealth.getApplicationName();
        String data;

        if (applicationHealth.isAuthorizationHealthy()) {
            data = "healthy";
        } else {
            boolean reachable = applicationHealth.isReachable();
            String errorString = applicationHealth.getErrors()
                    .stream()
                    .map(error -> error + "; ")
                    .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)
                    .toString();
            if (reachable) {
                errorString = errorString + " (application is reachable)";
            }
            data = errorString;
        }

        responseBuilder.withData(applicationName, data);
    }

    private ApplicationAuthenticatorAuthorizationHealth checkApplicationHealth(Application application) {
        String token = tokenService.generateAuthenticatorTokenForApplication(application);
        return applicationHealthClient.checkAuthenticatorAuthorizationHealth(application, token);
    }
}
