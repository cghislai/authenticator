package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.exception.UnauthorizedOperationException;
import com.charlyghislain.authenticator.domain.domain.filter.UserApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.domain.domain.util.ResultList;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Stateless
public class CallerQueryService {

    @Inject
    private Principal callerPrincipal;
    @Inject
    private SecurityContext securityContext;
    @Inject
    private UserQueryService userQueryService;
    @Inject
    private ApplicationQueryService applicationQueryService;

    public Optional<User> findCallerUser() {
        return userQueryService.findUserByName(callerPrincipal.getName());
    }

    public List<UserApplication> findCallerUserApplications() throws UnauthorizedOperationException {
        User user = userQueryService.findUserByName(callerPrincipal.getName())
                .orElseThrow(UnauthorizedOperationException::new);
        UserApplicationFilter userApplicationFilter = new UserApplicationFilter();
        userApplicationFilter.setActive(null);
        userApplicationFilter.setUser(user);
        ResultList<UserApplication> userApplications = userQueryService.findAllUserApplications(userApplicationFilter);
        return userApplications.getResults();
    }

    public Optional<Application> findCallerApplication() {
        Set<Principal> principals = securityContext.getPrincipalsByType(Principal.class);
        return principals.stream()
                .filter(this::isApplicationRestrictionGroup)
                .map(this::getRestrictedApplicationId)
                .findAny()
                .flatMap(applicationQueryService::findActiveApplicationById);
    }

    private Long getRestrictedApplicationId(Principal principal) {
        String name = principal.getName();
        String idString = name.replaceAll(AuthenticatorConstants.ROLE_APPLICATION_RESTRICTION, "");
        return Long.parseLong(idString);
    }

    private boolean isApplicationRestrictionGroup(Principal principal) {
        String name = principal.getName();
        return name.startsWith(AuthenticatorConstants.ROLE_APPLICATION_RESTRICTION);
    }

    public String createApplicationPrincipalName(Application application) {
        return AuthenticatorConstants.APPLICATION_PRINCIPAL_NAME_PREFIX + application.getName();
    }

}
