package com.charlyghislain.authenticator.admin.web;


import com.charlyghislain.authenticator.admin.api.AdminUserResource;
import com.charlyghislain.authenticator.admin.api.domain.WsPagination;
import com.charlyghislain.authenticator.admin.api.domain.WsResultList;
import com.charlyghislain.authenticator.admin.api.domain.WsUser;
import com.charlyghislain.authenticator.admin.api.domain.WsUserApplication;
import com.charlyghislain.authenticator.admin.api.domain.WsUserFilter;
import com.charlyghislain.authenticator.admin.api.error.AuthenticatorAdminWebError;
import com.charlyghislain.authenticator.admin.api.error.AuthenticatorAdminWebException;
import com.charlyghislain.authenticator.admin.web.converter.UserApplicationFilterConverter;
import com.charlyghislain.authenticator.admin.web.converter.UserConverter;
import com.charlyghislain.authenticator.admin.web.converter.UserFilterConverter;
import com.charlyghislain.authenticator.admin.web.converter.WsUserApplicationConverter;
import com.charlyghislain.authenticator.admin.web.converter.WsUserConverter;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.exception.AdminCannotLockHerselfOutException;
import com.charlyghislain.authenticator.domain.domain.exception.EmailAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.exception.NameAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.filter.UserApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.filter.UserFilter;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import com.charlyghislain.authenticator.domain.domain.util.ResultList;
import com.charlyghislain.authenticator.ejb.service.ApplicationQueryService;
import com.charlyghislain.authenticator.ejb.service.UserQueryService;
import com.charlyghislain.authenticator.ejb.service.UserUpdateService;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@RolesAllowed({AuthenticatorConstants.ROLE_ADMIN})
public class AdminUserResourceController implements AdminUserResource {

    @Inject
    private UserQueryService userQueryService;
    @Inject
    private UserUpdateService userUpdateService;
    @Inject
    private ApplicationQueryService applicationQueryService;

    @Inject
    private WsUserConverter wsUserConverter;
    @Inject
    private WsUserApplicationConverter wsUserApplicationConverter;
    @Inject
    private UserFilterConverter userFilterConverter;
    @Inject
    private UserConverter userConverter;
    @Inject
    private UserApplicationFilterConverter userApplicationFilterConverter;

    @NonNull
    @Override
    public WsResultList<WsUser> listUsers(WsUserFilter wsUserFilter, WsPagination wsPagination) {
        Pagination<User> pagination = userFilterConverter.translateWsPagination(wsPagination);
        UserFilter userFilter = userFilterConverter.translateWsUserFilter(wsUserFilter);

        ResultList<User> userResultList = userQueryService.findUsers(userFilter, pagination);
        List<WsUser> wsUserList = userResultList.map(wsUserConverter::toWsuser).getResults();
        return new WsResultList<>(wsUserList, userResultList.getTotalCount());
    }


    @Override
    public WsUser getUser(Long userId) {
        return userQueryService.findUserById(userId)
                .map(wsUserConverter::toWsuser)
                .orElseThrow(this::newNotFoundException);
    }

    @NonNull
    @Override
    public WsUser createUser(WsUser wsUser) {
        User user = userConverter.toUser(wsUser);
        try {
            User createdUser = userUpdateService.createUser(user);
            return wsUserConverter.toWsuser(createdUser);
        } catch (EmailAlreadyExistsException e) {
            throw new AuthenticatorAdminWebException(AuthenticatorAdminWebError.EMAIL_ALREADY_EXISTS);
        } catch (NameAlreadyExistsException e) {
            throw new AuthenticatorAdminWebException(AuthenticatorAdminWebError.NAME_ALREADY_EXISTS);
        }
    }

    @NonNull
    @Override
    public WsUser updateUser(Long userId, WsUser wsUser) {
        User user = userConverter.toUser(wsUser);
        user.setCreationTime(LocalDateTime.now());
        User existingUser = userQueryService.findUserById(userId)
                .orElseThrow(this::newNotFoundException);
        try {
            User updatedUser = userUpdateService.updateUser(existingUser, user);
            return wsUserConverter.toWsuser(updatedUser);
        } catch (AdminCannotLockHerselfOutException e) {
            throw new AuthenticatorAdminWebException(AuthenticatorAdminWebError.LOCKING_ADMIN_OUT);
        } catch (EmailAlreadyExistsException e) {
            throw new AuthenticatorAdminWebException(AuthenticatorAdminWebError.EMAIL_ALREADY_EXISTS);
        } catch (NameAlreadyExistsException e) {
            throw new AuthenticatorAdminWebException(AuthenticatorAdminWebError.NAME_ALREADY_EXISTS);
        }
    }

    @Override
    public void deleteUser(Long userId) {
        User existingUser = userQueryService.findUserById(userId)
                .orElseThrow(this::newNotFoundException);
        userUpdateService.deleteUser(existingUser);
    }

    @Override
    public void updatePassword(Long userId, @NonNull String password) {
        User existingUser = userQueryService.findUserById(userId)
                .orElseThrow(this::newNotFoundException);
        userUpdateService.setUserPassword(existingUser, password);
    }

    @NonNull
    @Override
    public WsResultList<WsUserApplication> listUserApplications(Long userId, @NonNull WsPagination wsPagination) {
        userQueryService.findUserById(userId)
                .orElseThrow(this::newNotFoundException);
        Pagination<UserApplication> pagination = userApplicationFilterConverter.toWsPagination(wsPagination);

        UserApplicationFilter userApplicationFilter = new UserApplicationFilter();
        userApplicationFilter.getUserFilter().setId(userId);

        ResultList<UserApplication> userApplications = userQueryService.findUserApplications(userApplicationFilter, pagination);
        List<WsUserApplication> wsUserApplications = userApplications.map(wsUserApplicationConverter::toWsUserApplication).getResults();
        return new WsResultList<>(wsUserApplications, userApplications.getTotalCount());
    }

    @NonNull
    @Override
    public WsUserApplication activateUserApplication(Long userId, Long applicationId) {
        User user = userQueryService.findUserById(userId)
                .orElseThrow(this::newNotFoundException);
        Application application = applicationQueryService.findApplicationById(applicationId)
                .orElseThrow(this::newNotFoundException);
        UserApplication userApplication = userQueryService.findUserApplication(user, application)
                .orElseThrow(this::newNotFoundException);

        UserApplication updatedUserApplication = userUpdateService.setUserApplicationActive(userApplication, true);
        return wsUserApplicationConverter.toWsUserApplication(updatedUserApplication);
    }

    @NonNull
    @Override
    public WsUserApplication deactivateUserApplication(Long userId, Long applicationId) {
        User user = userQueryService.findUserById(userId)
                .orElseThrow(this::newNotFoundException);
        Application application = applicationQueryService.findApplicationById(applicationId)
                .orElseThrow(this::newNotFoundException);
        UserApplication userApplication = userQueryService.findUserApplication(user, application)
                .orElseThrow(this::newNotFoundException);

        UserApplication updatedUserApplication = userUpdateService.setUserApplicationActive(userApplication, false);
        return wsUserApplicationConverter.toWsUserApplication(updatedUserApplication);
    }

    private AuthenticatorAdminWebException newNotFoundException() {
        return new AuthenticatorAdminWebException(AuthenticatorAdminWebError.USER_NOT_FOUND);
    }
}
