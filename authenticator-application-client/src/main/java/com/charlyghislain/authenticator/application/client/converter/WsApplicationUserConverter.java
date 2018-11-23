package com.charlyghislain.authenticator.application.client.converter;


import com.charlyghislain.authenticator.application.api.domain.WsApplicationUser;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WsApplicationUserConverter {

    @NonNull
    public WsApplicationUser toWsApplicationUser(@NonNull UserApplication userApplication) {
        boolean active = userApplication.isActive();
        User user = userApplication.getUser();

        Long id = user.getId();
        String name = user.getName();
        String email = user.getEmail();
        String password = user.getPassword();
        boolean admin = user.isAdmin();
        boolean passwordExpired = user.isPasswordExpired();
        boolean emailVerified = user.isEmailVerified();

        WsApplicationUser wsApplicationUser = new WsApplicationUser();
        wsApplicationUser.setId(id);
        wsApplicationUser.setName(name);
        wsApplicationUser.setEmail(email);
        wsApplicationUser.setActive(active);
        wsApplicationUser.setPasswordExpired(passwordExpired);
        wsApplicationUser.setEmailVerified(emailVerified);
        return wsApplicationUser;

    }
}
