package com.charlyghislain.authenticator.management.web;

import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.exception.EmailAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.exception.NameAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.filter.UserApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import com.charlyghislain.authenticator.domain.domain.util.ResultList;
import com.charlyghislain.authenticator.ejb.service.UserQueryService;
import com.charlyghislain.authenticator.ejb.service.UserUpdateService;
import com.charlyghislain.authenticator.management.api.UserResource;
import com.charlyghislain.authenticator.management.api.domain.WsApplicationUser;
import com.charlyghislain.authenticator.management.api.domain.WsEmailVerificationToken;
import com.charlyghislain.authenticator.management.api.domain.WsPagination;
import com.charlyghislain.authenticator.management.api.domain.WsResultList;
import com.charlyghislain.authenticator.management.api.domain.WsUserApplicationFilter;
import com.charlyghislain.authenticator.management.api.error.AuthenticatorManagementWebError;
import com.charlyghislain.authenticator.management.api.error.AuthenticatorManagementWebException;
import com.charlyghislain.authenticator.management.web.converter.UserApplicationFilterConverter;
import com.charlyghislain.authenticator.management.web.converter.UserConverter;
import com.charlyghislain.authenticator.management.web.converter.WsApplicationUserConverter;
import com.charlyghislain.authenticator.management.web.provider.CallerManagedApplication;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;

@RolesAllowed({AuthenticatorConstants.ROLE_APP_MANAGEMENT})
public class UserResourceController implements UserResource {

    @Inject
    private UserUpdateService userUpdateService;
    @Inject
    private UserQueryService userQueryService;
    @Inject
    private WsApplicationUserConverter wsApplicationUserConverter;
    @Inject
    private UserApplicationFilterConverter userApplicationFilterConverter;
    @Inject
    private UserConverter userConverter;

    @Inject
    @CallerManagedApplication
    private Application callerManagedApplication;

    @Override
    public WsApplicationUser createUser(WsApplicationUser wsApplicationUser) {
        User user = userConverter.toUser(wsApplicationUser);
        try {
            UserApplication newUserApplication = userUpdateService.createApplicationUser(callerManagedApplication, user);
            return wsApplicationUserConverter.toWsUserApplication(newUserApplication);
        } catch (EmailAlreadyExistsException e) {
            throw new AuthenticatorManagementWebException(AuthenticatorManagementWebError.EMAIL_ALREADY_EXISTS);
        } catch (NameAlreadyExistsException e) {
            throw new AuthenticatorManagementWebException(AuthenticatorManagementWebError.NAME_ALREADY_EXISTS);
        }
    }

    @Override
    public WsResultList<WsApplicationUser> listUsers(WsUserApplicationFilter filter, WsPagination wsPagination) {
        UserApplicationFilter userApplicationFilter = userApplicationFilterConverter.toUserApplicationFilter(filter);
        Pagination<UserApplication> pagination = userApplicationFilterConverter.toPagination(wsPagination);
        userApplicationFilter.setApplication(callerManagedApplication);

        ResultList<UserApplication> userApplicationResultList = userQueryService.findUserApplications(userApplicationFilter, pagination);
        List<WsApplicationUser> results = userApplicationResultList.map(wsApplicationUserConverter::toWsUserApplication).getResults();
        return new WsResultList<>(results, userApplicationResultList.getTotalCount());
    }

    @Override
    public WsApplicationUser getUser(Long userId) {
        UserApplication userApplication = getUserApplication(userId);
        return wsApplicationUserConverter.toWsUserApplication(userApplication);
    }

    @Override
    public WsApplicationUser updateUser(Long userId, WsApplicationUser wsApplicationUser) {
        UserApplication userApplication = getUserApplication(userId);
        User user = userConverter.toUser(wsApplicationUser);
        UserApplication updatedUserApplication = userUpdateService.updateApplicationUser(userApplication, user);
        return wsApplicationUserConverter.toWsUserApplication(updatedUserApplication);
    }

    @Override
    public void forgetUser(Long userId) {
        UserApplication userApplication = getUserApplication(userId);
        userUpdateService.forgetApplicationUser(userApplication);
    }

    @Override
    public WsEmailVerificationToken getEmailVerificationToken(Long userId) {
        return null;
    }

    @Override
    public void checkEmailVerification(Long userId, String verificationToken) {

    }

    @NotNull
    private UserApplication getUserApplication(Long userId) {
        UserApplicationFilter userApplicationFilter = new UserApplicationFilter();
        userApplicationFilter.setApplication(callerManagedApplication);
        userApplicationFilter.getUserFilter().setId(userId);

        return userQueryService.findUserApplication(userApplicationFilter)
                .orElseThrow(() -> new AuthenticatorManagementWebException(AuthenticatorManagementWebError.USER_NOT_FOUND));
    }
}
