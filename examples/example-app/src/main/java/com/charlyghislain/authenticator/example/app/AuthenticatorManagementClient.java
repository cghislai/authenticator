package com.charlyghislain.authenticator.example.app;

import com.charlyghislain.authenticator.management.api.UserResource;
import com.charlyghislain.authenticator.management.api.domain.WsApplicationUser;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;

@ApplicationScoped
public class AuthenticatorManagementClient {

    @Inject
    @ConfigProperty(name = "appSecret")
    private String appSecretToken;

    @Inject
    @ConfigProperty(name = "authenticatorManagementUrl")
    private String authenticatorManagementUrl;

    private UserResource managementUserResource;

    @PostConstruct
    public void init() {
        this.managementUserResource = RestClientBuilder.newBuilder()
                .baseUri(URI.create(this.authenticatorManagementUrl))
                .register(new JwtTokenProvider(this.appSecretToken))
                .build(UserResource.class);
    }

    public WsApplicationUser getUser(long id) {
        return this.managementUserResource.getUser(id);
    }
}
