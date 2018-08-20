package com.charlyghislain.authenticator.management.web.converter;


import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.management.api.domain.WsApplicationUser;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserConverter {

    public User toUser(WsApplicationUser wsApplicationUser) {
        Long id = wsApplicationUser.getId();
        String name = wsApplicationUser.getName();
        String email = wsApplicationUser.getEmail();
        boolean active = wsApplicationUser.isActive();
        boolean passwordExpired = wsApplicationUser.isPasswordExpired();
        boolean emailVerified = wsApplicationUser.isEmailVerified();

        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setActive(active);
        user.setPasswordExpired(passwordExpired);
        user.setEmailVerified(emailVerified);

        return user;
    }
}
