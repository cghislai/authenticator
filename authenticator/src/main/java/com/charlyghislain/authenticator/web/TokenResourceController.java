package com.charlyghislain.authenticator.web;


import com.charlyghislain.authenticator.api.TokenResource;
import com.charlyghislain.authenticator.api.error.AuthenticatorWebError;
import com.charlyghislain.authenticator.api.error.AuthenticatorWebException;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.exception.NoSigningKeyException;
import com.charlyghislain.authenticator.domain.domain.exception.UnauthorizedOperationException;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.ejb.service.ApplicationQueryService;
import com.charlyghislain.authenticator.ejb.service.CallerQueryService;
import com.charlyghislain.authenticator.ejb.service.JwtTokenService;
import com.charlyghislain.authenticator.ejb.service.UserQueryService;
import com.charlyghislain.authenticator.ejb.service.UserUpdateService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

@RolesAllowed({AuthenticatorConstants.ROLE_USER})
public class TokenResourceController implements TokenResource {

    @Inject
    private JwtTokenService tokenService;
    @Inject
    private UserQueryService userQueryService;
    @Inject
    private UserUpdateService userUpdateService;
    @Inject
    private ApplicationQueryService applicationQueryService;
    @Inject
    private CallerQueryService callerQueryService;
    @Context
    private HttpServletRequest httpServletRequest;

    @Override
    public String applicationLogin(String applicationName) {
        User authenticatedUser = callerQueryService.findCallerUser()
                .orElseThrow(this::newUnauthenticatedException);
        Application application = applicationQueryService.findActiveApplicationByName(applicationName)
                .orElseThrow(this::newApplicationNotFoundException);
        UserApplication userApplication = userQueryService.findActiveUserApplication(authenticatedUser, application)
                .orElseGet(() -> this.linkUserWithApplication(authenticatedUser, application));
        try {
            return tokenService.generateUserTokenForApplication(userApplication);
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

    private UserApplication linkUserWithApplication(User authenticatedUser, Application application) {
        try {
            return userUpdateService.linkUserToApplicationOnTokenRequest(authenticatedUser, application);
        } catch (UnauthorizedOperationException e) {
            throw newUnauthenticatedException();
        }
    }

    private AuthenticatorWebException newUnauthenticatedException() {
        return new AuthenticatorWebException(AuthenticatorWebError.UNAUTHENTICATED_ERROR);
    }

    private AuthenticatorWebException newApplicationNotFoundException() {
        return new AuthenticatorWebException(AuthenticatorWebError.APPLICATION_NOT_FOUND);
    }
}
