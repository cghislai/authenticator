package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.security.Principal;
import java.util.Optional;

@Stateless
public class HealthService {

    @EJB
    private HealthService self;
    @Inject
    private Principal principal;

    @PermitAll
    public HealthCheckResponse checkAuthenticationStatus() {
        Optional<String> principalName = Optional.ofNullable(principal)
                .map(Principal::getName);
        boolean up = principalName.isPresent();
        @Nullable String error = null;
        try {
            up &= self.checkAuthorizedCall();
        } catch (Exception e) {
            up = false;
            error = this.unwrapErrorMessage(e);
        }

        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.builder()
                .name("authorization")
                .state(up);

        Optional.ofNullable(error)
                .ifPresent(e -> responseBuilder.withData("error", e));
        principalName.ifPresent(name -> responseBuilder.withData("principal", name));

        return responseBuilder.build();
    }


    @RolesAllowed({AuthenticatorConstants.ROLE_APPLICATION})
    public boolean checkAuthorizedCall() {
        return true;
    }

    private String unwrapErrorMessage(Throwable e) {
        return Optional.ofNullable(e)
                .map(Throwable::getMessage)
                .orElseGet(() -> this.getCauseMessage(e));
    }

    private String getCauseMessage(Throwable e) {
        return Optional.ofNullable(e)
                .map(Throwable::getCause)
                .map(this::unwrapErrorMessage)
                .orElse(null);
    }
}
