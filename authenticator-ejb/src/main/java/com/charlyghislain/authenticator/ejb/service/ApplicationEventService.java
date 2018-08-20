package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.client.ApplicationUserEventsClient;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.UserApplication;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class ApplicationEventService {

    @Inject
    private JwtTokenService tokenService;
    @Inject
    private ApplicationUserEventsClient userEventsClient;

    public void onUserAdded(UserApplication userApplication) {
        Application application = userApplication.getApplication();
        String tokenForApplication = tokenService.generateAuthenticatorTokenForApplication(application);

        userEventsClient.notifyUserAdded(tokenForApplication, userApplication);
    }

    public void notifyUserRemoved(Application application, Long userId) {
        String tokenForApplication = tokenService.generateAuthenticatorTokenForApplication(application);

        userEventsClient.notifyUserRemoved(tokenForApplication, application, userId);
    }
}
