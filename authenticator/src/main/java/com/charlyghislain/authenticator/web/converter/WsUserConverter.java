package com.charlyghislain.authenticator.web.converter;


import com.charlyghislain.authenticator.api.domain.WsUser;
import com.charlyghislain.authenticator.domain.domain.User;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WsUserConverter {

    public WsUser toWsuser(User user) {
        Long id = user.getId();
        String name = user.getName();
        String email = user.getEmail();
        boolean active = user.isActive();
        boolean admin = user.isAdmin();
        boolean emailVerified = user.isEmailVerified();
        boolean passwordExpired = user.isPasswordExpired();

        WsUser wsUser = new WsUser();
        wsUser.setId(id);
        wsUser.setName(name);
        wsUser.setEmail(email);
        wsUser.setActive(active);
        wsUser.setAdmin(admin);
        wsUser.setEmailVerified(emailVerified);
        wsUser.setPasswordExpired(passwordExpired);
        return wsUser;
    }

}
