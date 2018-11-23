package com.charlyghislain.authenticator.application.client;


import com.charlyghislain.authenticator.application.api.AuthorizationResource;
import com.charlyghislain.authenticator.application.client.converter.ApplicationRoleConverter;
import com.charlyghislain.authenticator.application.client.provider.RestClientProvider;
import com.charlyghislain.authenticator.domain.client.ApplicationAuthorizationClient;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationRole;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ApplicationAuthorizationWsClient implements ApplicationAuthorizationClient {

    @Inject
    private RestClientProvider restClientProvider;
    @Inject
    private ApplicationRoleConverter applicationRoleConverter;

    @PostConstruct
    public void init() {
    }

    @Override
    public List<ApplicationRole> findUserApplicationRoles(String authToken, @NonNull UserApplication userApplication) {
        Application application = userApplication.getApplication();
        User user = userApplication.getUser();
        Long userId = user.getId();

        AuthorizationResource resource = restClientProvider.createResource(authToken, application, AuthorizationResource.class);
        return resource.listUserRoles(userId)
                .stream()
                .map(wsRole -> applicationRoleConverter.toApplicationRole(wsRole, application))
                .collect(Collectors.toList());
    }

}
