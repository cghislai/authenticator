package com.charlyghislain.authenticator.application.client;


import com.charlyghislain.authenticator.application.api.UserEventResource;
import com.charlyghislain.authenticator.application.api.domain.WsApplicationUser;
import com.charlyghislain.authenticator.application.client.converter.WsApplicationUserConverter;
import com.charlyghislain.authenticator.application.client.provider.RestClientProvider;
import com.charlyghislain.authenticator.domain.client.ApplicationUserEventsClient;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.UserApplication;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ApplicationUserEventsWsClient implements ApplicationUserEventsClient {

    @Inject
    private RestClientProvider restClientProvider;
    @Inject
    private WsApplicationUserConverter wsApplicationUserConverter;

    @PostConstruct
    public void init() {
    }

    public void notifyUserAdded(String authToken, UserApplication userApplication) {
        WsApplicationUser wsApplicationUser = wsApplicationUserConverter.toWsApplicationUser(userApplication);

        Application application = userApplication.getApplication();
        UserEventResource eventResource = restClientProvider.createResource(authToken, application, UserEventResource.class);
        eventResource.userAdded(wsApplicationUser);
    }

    @Override
    public void notifyEmailVerified(String authToken, UserApplication userApplication) {
        WsApplicationUser wsApplicationUser = wsApplicationUserConverter.toWsApplicationUser(userApplication);

        Application application = userApplication.getApplication();
        UserEventResource eventResource = restClientProvider.createResource(authToken, application, UserEventResource.class);
        eventResource.userEmailVerified(wsApplicationUser);
    }

    public void notifyUserRemoved(String authToken, Application application, Long userId) {
        UserEventResource eventResource = restClientProvider.createResource(authToken, application, UserEventResource.class);
        eventResource.userRemoved(userId);
    }

}
