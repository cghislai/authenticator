package com.charlyghislain.authenticator.web;


import com.charlyghislain.authenticator.api.TokenResource;
import com.charlyghislain.authenticator.api.error.AuthenticatorWebError;
import com.charlyghislain.authenticator.api.error.AuthenticatorWebException;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.exception.NoSigningKeyException;
import com.charlyghislain.authenticator.domain.domain.exception.UnauthorizedOperationException;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.ejb.service.ApplicationQueryService;
import com.charlyghislain.authenticator.ejb.service.CallerQueryService;
import com.charlyghislain.authenticator.ejb.service.JwtTokenService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

@RolesAllowed({AuthenticatorConstants.ROLE_USER})
public class TokenResourceController implements TokenResource {

    @Inject
    private JwtTokenService tokenService;
    @Inject
    private ApplicationQueryService applicationQueryService;
    @Inject
    private CallerQueryService callerQueryService;

    @Override
    public String applicationLogin(String applicationName) {
        User authenticatedUser = callerQueryService.findCallerUser()
                .orElseThrow(this::newUnauthenticatedException);
        Application application = applicationQueryService.findActiveApplicationByName(applicationName)
                .orElseThrow(this::newApplicationNotFoundException);
        try {
            return tokenService.generateUserTokenForApplication(authenticatedUser, application);
        } catch (UnauthorizedOperationException e) {
            throw new AuthenticatorWebException(AuthenticatorWebError.UNAUTHORIZED_OPERATION);
        } catch (NoSigningKeyException e) {
            throw new AuthenticatorWebException(AuthenticatorWebError.UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public String login() {
        User authenticatedUser = callerQueryService.findCallerUser()
                .orElseThrow(this::newUnauthenticatedException);
        try {
            return tokenService.generateUserTokenForAuthenticator(authenticatedUser);
        } catch (NoSigningKeyException e) {
            throw new AuthenticatorWebException(AuthenticatorWebError.UNEXPECTED_ERROR, e);
        }
    }


    private AuthenticatorWebException newUnauthenticatedException() {
        return new AuthenticatorWebException(AuthenticatorWebError.UNAUTHENTICATED_ERROR);
    }

    private AuthenticatorWebException newApplicationNotFoundException() {
        return new AuthenticatorWebException(AuthenticatorWebError.APPLICATION_NOT_FOUND);
    }
}
