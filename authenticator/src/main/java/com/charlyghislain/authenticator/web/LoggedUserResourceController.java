package com.charlyghislain.authenticator.web;


import com.charlyghislain.authenticator.api.LoggedUserResource;
import com.charlyghislain.authenticator.api.domain.WsUser;
import com.charlyghislain.authenticator.api.domain.WsUserApplication;
import com.charlyghislain.authenticator.api.error.AuthenticatorWebError;
import com.charlyghislain.authenticator.api.error.AuthenticatorWebException;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.exception.EmailAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.exception.NameAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.exception.UnauthorizedOperationException;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.ejb.service.ApplicationQueryService;
import com.charlyghislain.authenticator.ejb.service.CallerQueryService;
import com.charlyghislain.authenticator.ejb.service.UserQueryService;
import com.charlyghislain.authenticator.ejb.service.UserUpdateService;
import com.charlyghislain.authenticator.web.converter.WsUserApplicationConverter;
import com.charlyghislain.authenticator.web.converter.WsUserConverter;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@RolesAllowed({AuthenticatorConstants.ROLE_USER})
public class LoggedUserResourceController implements LoggedUserResource {

    @Inject
    private CallerQueryService callerQueryService;
    @Inject
    private UserUpdateService userUpdateService;
    @Inject
    private ApplicationQueryService applicationQueryService;
    @Inject
    private UserQueryService userQueryService;
    @Inject
    private WsUserConverter wsUserConverter;
    @Inject
    private WsUserApplicationConverter wsUserApplicationConverter;

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
            userUpdateService.updateUserPassword(user, password);
        } catch (UnauthorizedOperationException e) {
            throw new AuthenticatorWebException(AuthenticatorWebError.UNAUTHORIZED_OPERATION);
        }
    }

    @Override
    public void updateEmail(String email) {
        User user = callerQueryService.findCallerUser()
                .orElseThrow(this::newUnauthenticatedException);
        try {
            userUpdateService.updateUserEmail(user, email);
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
            userUpdateService.updateUserName(user, name);
        } catch (UnauthorizedOperationException e) {
            throw new AuthenticatorWebException(AuthenticatorWebError.UNAUTHORIZED_OPERATION);
        } catch (NameAlreadyExistsException e) {
            throw new AuthenticatorWebException(AuthenticatorWebError.NAME_ALREADY_EXISTS);
        }
    }

    @Override
    public List<WsUserApplication> listUserApplications() {
        try {
            return callerQueryService.findCallerUserApplications()
                    .stream()
                    .map(wsUserApplicationConverter::toWsUserApplication)
                    .collect(Collectors.toList());
        } catch (UnauthorizedOperationException e) {
            throw newUnauthenticatedException();
        }
    }

    @Override
    public void unlinkApplications(Long applicationId) {
        User user = callerQueryService.findCallerUser()
                .orElseThrow(this::newUnauthenticatedException);
        Application application = applicationQueryService.findApplicationById(applicationId)
                .orElseThrow(this::newUnauthenticatedException);
        UserApplication userApplication = userQueryService.findUserApplication(user, application)
                .orElseThrow(this::newUnauthenticatedException);
        try {
            userUpdateService.unlinkUserToApplication(userApplication);
        } catch (UnauthorizedOperationException e) {
            throw newUnauthenticatedException();
        }
    }

    private AuthenticatorWebException newUnauthenticatedException() {
        return new AuthenticatorWebException(AuthenticatorWebError.UNAUTHENTICATED_ERROR);
    }

}
