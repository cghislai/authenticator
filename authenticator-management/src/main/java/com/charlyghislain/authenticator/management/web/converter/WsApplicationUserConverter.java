package com.charlyghislain.authenticator.management.web.converter;


import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.management.api.domain.WsApplicationUser;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WsApplicationUserConverter {

    @NonNull
    public WsApplicationUser toWsUserApplication(@NonNull UserApplication userApplication) {
        User user = userApplication.getUser();
        boolean active = userApplication.isActive();

        Long id = user.getId();
        String name = user.getName();
        String email = user.getEmail();
        boolean emailVerified = user.isEmailVerified();
        boolean passwordExpired = user.isPasswordExpired();


        WsApplicationUser wsUser = new WsApplicationUser();
        wsUser.setId(id);
        wsUser.setName(name);
        wsUser.setEmail(email);
        wsUser.setActive(active);
        wsUser.setEmailVerified(emailVerified);
        wsUser.setPasswordExpired(passwordExpired);
        return wsUser;
    }

}
