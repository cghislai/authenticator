package com.charlyghislain.authenticator.example.app.client;

import com.charlyghislain.authenticator.management.api.ConnectivityResource;
import com.charlyghislain.authenticator.management.api.UserResource;
import com.charlyghislain.authenticator.management.api.domain.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;
import java.util.Optional;

@ApplicationScoped
public class AuthenticatorManagementClient {

    @Inject
    @ConfigProperty(name = "appSecret")
    private String appSecretToken;

    @Inject
    @ConfigProperty(name = "authenticatorManagementUrl")
    private String authenticatorManagementUrl;

    @Inject
    private ErrorResponseFilter errorResponseFilter;

    private UserResource managementUserResource;
    private ConnectivityResource connectivityResource;

    @PostConstruct
    public void init() {
        this.managementUserResource = RestClientBuilder.newBuilder()
                .baseUri(URI.create(this.authenticatorManagementUrl))
                .register(new JwtTokenProvider(this.appSecretToken))
                .register(errorResponseFilter)
                .build(UserResource.class);
        this.connectivityResource = RestClientBuilder.newBuilder()
                .baseUri(URI.create(this.authenticatorManagementUrl))
                .register(new JwtTokenProvider(this.appSecretToken))
                .register(errorResponseFilter)
                .build(ConnectivityResource.class);
    }

    public WsHealthCheckStatus checkConnectivity() {
        return this.connectivityResource.authenticationHealthCheck();
    }

    public WsApplicationInfo getMyAppInfo() {
        return this.connectivityResource.getMyInfo();
    }

    public WsApplicationUser getUser(long id) {
        return this.managementUserResource.getUser(id);
    }

    public WsApplicationUser createUser(WsApplicationUserWithPassword applicationUser) {
        return this.managementUserResource.createUser(applicationUser);
    }


    public WsPasswordResetToken createPasswordResetToken(Long userId) {
        return this.managementUserResource.createNewPasswordResetToken(userId);
    }

    public WsApplicationUser setPassword(@NonNull WsApplicationUser applicationUser, String password) {
        return this.managementUserResource.updateUserPassword(applicationUser.getId(), password);
    }


    public WsEmailVerificationToken getMailVerificationToken(Long userId) {
        return this.managementUserResource.getEmailVerificationToken(userId);
    }

    public void verifyMailVerificationToken(Long userId, String token) {
        this.managementUserResource.checkEmailVerification(userId, token);
    }

    public void setUserActive(Long userId) {
        WsApplicationUser user = this.managementUserResource.getUser(userId);
        user.setActive(true);
        this.managementUserResource.updateUser(userId, user);
    }


    public Optional<WsApplicationUser> findUserByMail(String email) {
        WsUserApplicationFilter wsUserApplicationFilter = new WsUserApplicationFilter();
        wsUserApplicationFilter.setUserEmail(email);
        WsPagination wsPagination = new WsPagination(1);
        WsResultList<WsApplicationUser> resultList = this.managementUserResource.listUsers(wsUserApplicationFilter, wsPagination);

        if (resultList.getTotalCount() > 0) {
            return resultList.getResults().stream().findAny();
        }
        return Optional.empty();
    }

    public Optional<WsApplicationUser> findUserByName(String name) {
        WsUserApplicationFilter wsUserApplicationFilter = new WsUserApplicationFilter();
        wsUserApplicationFilter.setUserName(name);
        WsPagination wsPagination = new WsPagination(1);
        WsResultList<WsApplicationUser> resultList = this.managementUserResource.listUsers(wsUserApplicationFilter, wsPagination);

        if (resultList.getTotalCount() > 0) {
            return resultList.getResults().stream().findAny();
        }
        return Optional.empty();
    }
}
