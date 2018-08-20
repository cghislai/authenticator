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
import com.charlyghislain.authenticator.ejb.service.UserQueryService;
import com.charlyghislain.authenticator.ejb.service.UserUpdateService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.List;

@RolesAllowed({AuthenticatorConstants.ROLE_ADMIN})
public class AdminUserResourceController implements AdminUserResource {

    @Inject
    private UserQueryService userQueryService;
    @Inject
    private UserUpdateService userUpdateService;

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

    @Override
    public WsUser createUser(WsUser wsUser) {
        User user = userConverter.translateWsUser(wsUser);
        try {
            User createdUser = userUpdateService.createUser(user);
            return wsUserConverter.toWsuser(createdUser);
        } catch (EmailAlreadyExistsException e) {
            throw new AuthenticatorAdminWebException(AuthenticatorAdminWebError.EMAIL_ALREADY_EXISTS);
        } catch (NameAlreadyExistsException e) {
            throw new AuthenticatorAdminWebException(AuthenticatorAdminWebError.NAME_ALREADY_EXISTS);
        }
    }

    @Override
    public WsUser updateUser(Long userId, WsUser wsUser) {
        User user = userConverter.translateWsUser(wsUser);
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
    public void updatePassword(Long userId, String password) {
        User existingUser = userQueryService.findUserById(userId)
                .orElseThrow(this::newNotFoundException);
        userUpdateService.setUserPassword(existingUser, password);
    }

    @Override
    public WsResultList<WsUserApplication> listUserApplications(Long userId, WsPagination wsPagination) {
        userQueryService.findUserById(userId)
                .orElseThrow(this::newNotFoundException);
        Pagination<UserApplication> pagination = userApplicationFilterConverter.translateWsPagination(wsPagination);

        UserApplicationFilter userApplicationFilter = new UserApplicationFilter();
        userApplicationFilter.getUserFilter().setId(userId);

        ResultList<UserApplication> userApplications = userQueryService.findUserApplications(userApplicationFilter, pagination);
        List<WsUserApplication> wsUserApplications = userApplications.map(wsUserApplicationConverter::toWsUserApplication).getResults();
        return new WsResultList<>(wsUserApplications, userApplications.getTotalCount());
    }

    private AuthenticatorAdminWebException newNotFoundException() {
        return new AuthenticatorAdminWebException(AuthenticatorAdminWebError.USER_NOT_FOUND);
    }
}
