package com.charlyghislain.authenticator.management.web.converter;


import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.management.api.domain.WsApplicationUser;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class UserConverter {

    @NonNull
    public User toUser(@NonNull WsApplicationUser wsApplicationUser) {
        Long id = wsApplicationUser.getId();
        String name = wsApplicationUser.getName();
        String email = wsApplicationUser.getEmail();
        boolean active = wsApplicationUser.isActive();
        boolean passwordExpired = wsApplicationUser.isPasswordExpired();
        boolean emailVerified = wsApplicationUser.isEmailVerified();

        User user = new User();
        Optional.ofNullable(id).ifPresent(user::setId);
        user.setName(name);
        user.setEmail(email);
        user.setActive(active);
        user.setPasswordExpired(passwordExpired);
        user.setEmailVerified(emailVerified);

        return user;
    }
}
