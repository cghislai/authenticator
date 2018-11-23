package com.charlyghislain.authenticator.web.security;


import com.charlyghislain.authenticator.web.provider.CrossOriginResourceSharingResponseFilter;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class CompositeHttpAuthenticationMechanism implements HttpAuthenticationMechanism {

    @Inject
    private Instance<HttpCredentialProvider> httpCredentialProviders;
    @Inject
    private IdentityStoreHandler identityStoreHandler;
    @Inject
    private CrossOriginResourceSharingResponseFilter crossOriginResourceSharingResponseFilter;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, @NonNull HttpMessageContext httpMessageContext) throws AuthenticationException {
        CredentialValidationResult validationResult = StreamSupport.stream(httpCredentialProviders.spliterator(), false)
                .map(provider -> provider.extractCredential(request))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .map(identityStoreHandler::validate)
                .orElse(CredentialValidationResult.NOT_VALIDATED_RESULT);
        return this.handleValidationResult(validationResult, httpMessageContext);
    }

    private AuthenticationStatus handleValidationResult(CredentialValidationResult validationResult, @NonNull HttpMessageContext httpMessageContext) {
        if (validationResult.getStatus() == CredentialValidationResult.Status.VALID) {
            // Valid
            return httpMessageContext.notifyContainerAboutLogin(validationResult);
        } else if (httpMessageContext.isProtected() || validationResult.getStatus() == CredentialValidationResult.Status.INVALID) {
            // Invalid
            HttpServletResponse response = httpMessageContext.getResponse();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            crossOriginResourceSharingResponseFilter.filter(response);
            return AuthenticationStatus.SEND_FAILURE;
        } else {
            // Not validated
            return httpMessageContext.doNothing();
        }
    }

}
