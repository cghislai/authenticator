package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.exception.AuthenticatorRuntimeException;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationRole;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class AccessQueryService {

    @Inject
    private CallerQueryService callerQueryService;
    @Inject
    private ApplicationUserRoleQueryService applicationUserRoleQueryService;
    @Inject
    private UserQueryService userQueryService;

    public Set<String> findUserApplicationRoles(@NonNull User user, @NonNull Application application) {
        List<ApplicationRole> applicationRoles = userQueryService.findActiveUserApplication(user, application)
                .map(this::findUserApplicationRoles)
                .orElseGet(ArrayList::new);

        boolean admin = user.isAdmin();
        if (applicationRoles.isEmpty() && !admin) {
            return new HashSet<>();
        }

        Set<String> rolesSet = applicationRoles.stream()
                .map(ApplicationRole::getRoleName)
                .collect(Collectors.toSet());
        rolesSet.add(AuthenticatorConstants.ROLE_USER);
        rolesSet.add(AuthenticatorConstants.ROLE_APPLICATION_RESTRICTION + application.getId());

        if (user.isActive()) {
            rolesSet.add(AuthenticatorConstants.ROLE_ACTIVE);
        }

        if (admin) {
            rolesSet.add(AuthenticatorConstants.ROLE_ADMIN);
            rolesSet.add(AuthenticatorConstants.ROLE_APP_MANAGEMENT);
        }

        return rolesSet;
    }


    private List<ApplicationRole> findUserApplicationRoles(UserApplication userApplication) {
        if (!userApplication.isActive()) {
            return new ArrayList<>();
        }
        List<ApplicationRole> applicationRoles = applicationUserRoleQueryService.listApplicationUserRoles(userApplication);
        applicationRoles.forEach(this::checkForbiddenRolesNames);
        return applicationRoles;
    }

    private void checkForbiddenRolesNames(ApplicationRole applicationRole) {
        if (applicationRole.getRoleName().startsWith(AuthenticatorConstants.AUTHENTICATOR_ROLES_PREFIX)) {
            throw new AuthenticatorRuntimeException("Application role names cannot start with " + AuthenticatorConstants.AUTHENTICATOR_ROLES_PREFIX);
        }
    }

    private Boolean hasCallerApplicationScope(Application application) {
        return this.callerQueryService.findCallerApplication()
                .map(application::equals)
                .orElse(false);
    }

}
