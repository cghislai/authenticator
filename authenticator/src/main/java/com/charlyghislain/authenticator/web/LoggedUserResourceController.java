package com.charlyghislain.authenticator.web;


import com.charlyghislain.authenticator.api.LoggedUserResource;
import com.charlyghislain.authenticator.api.domain.WsUser;
import com.charlyghislain.authenticator.api.error.AuthenticatorWebError;
import com.charlyghislain.authenticator.api.error.AuthenticatorWebException;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.exception.EmailAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.exception.NameAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.exception.UnauthorizedOperationException;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.ejb.service.CallerQueryService;
import com.charlyghislain.authenticator.ejb.service.UserUpdateService;
import com.charlyghislain.authenticator.web.converter.WsUserConverter;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

@RolesAllowed({AuthenticatorConstants.ROLE_USER})
public class LoggedUserResourceController implements LoggedUserResource {

    @Inject
    private CallerQueryService callerQueryService;
    @Inject
    private UserUpdateService userConverter;
    @Inject
    private WsUserConverter wsUserConverter;

    @Override
    public WsUser getLoggedUser() {
        return callerQueryService.findCallerUser()
                .map(wsUserConverter::toWsuser)
                .orElseThrow(this::newUnauthenticatedException);
    }

    @Override
    public void updatePassword(String password) {
        User user = callerQueryService.findCallerUser()
                .orElseThrow(this::newUnauthenticatedException);
        try {
            userConverter.updateUserPassword(user, password);
        } catch (UnauthorizedOperationException e) {
            throw new AuthenticatorWebException(AuthenticatorWebError.UNAUTHORIZED_OPERATION);
        }
    }

    @Override
    public void updateEmail(String email) {
        User user = callerQueryService.findCallerUser()
                .orElseThrow(this::newUnauthenticatedException);
        try {
            userConverter.updateUserEmail(user, email);
        } catch (EmailAlreadyExistsException e) {
            throw new AuthenticatorWebException(AuthenticatorWebError.EMAIL_ALREADY_EXISTS);
        } catch (UnauthorizedOperationException e) {
            throw new AuthenticatorWebException(AuthenticatorWebError.UNAUTHORIZED_OPERATION);
        }
    }

    @Override
    public void updateName(String name) {
        User user = callerQueryService.findCallerUser()
                .orElseThrow(this::newUnauthenticatedException);
        try {
            userConverter.updateUserName(user, name);
        } catch (UnauthorizedOperationException e) {
            throw new AuthenticatorWebException(AuthenticatorWebError.UNAUTHORIZED_OPERATION);
        } catch (NameAlreadyExistsException e) {
            throw new AuthenticatorWebException(AuthenticatorWebError.NAME_ALREADY_EXISTS);
        }
    }

    private AuthenticatorWebException newUnauthenticatedException() {
        return new AuthenticatorWebException(AuthenticatorWebError.UNAUTHENTICATED_ERROR);
    }

}
