package com.charlyghislain.authenticator.management.web.provider;

import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.ejb.service.CallerQueryService;
import com.charlyghislain.authenticator.management.api.error.AuthenticatorManagementWebError;
import com.charlyghislain.authenticator.management.api.error.AuthenticatorManagementWebException;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class CallerApplicationProducer {

    @Inject
    private CallerQueryService callerQueryService;

    @Produces
    @RequestScoped
    @CallerManagedApplication
    public Application getCallerManagedApplicationOrThrow() {
        return callerQueryService.findCallerApplication()
                .orElseThrow(() -> new AuthenticatorManagementWebException(AuthenticatorManagementWebError.UNAUTHENTICATED_ERROR));
    }
}
