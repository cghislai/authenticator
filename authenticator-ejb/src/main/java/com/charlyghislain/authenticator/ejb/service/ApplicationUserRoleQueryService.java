package com.charlyghislain.authenticator.ejb.service;

import com.charlyghislain.authenticator.domain.client.ApplicationAuthorizationClient;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationRole;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;


@Stateless
public class ApplicationUserRoleQueryService {

    @Inject
    private JwtTokenService tokenService;
    @Inject
    private ApplicationAuthorizationClient applicationAuthorizationClient;


    public List<ApplicationRole> listApplicationUserRoles(@NonNull UserApplication userApplication) {
        String token = tokenService.generateAuthenticatorTokenForApplication(userApplication.getApplication());
        return applicationAuthorizationClient.findUserApplicationRoles(token, userApplication);
    }

}
