package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsUser;
import com.charlyghislain.authenticator.domain.domain.User;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@ApplicationScoped
public class UserConverter {
    @NonNull
    public User toUser(@NonNull WsUser wsUser) {
        Long id = wsUser.getId();
        String name = wsUser.getName();
        String email = wsUser.getEmail();
        boolean active = wsUser.isActive();
        boolean admin = wsUser.isAdmin();
        boolean emailVerified = wsUser.isEmailVerified();
        boolean passwordExpired = wsUser.isPasswordExpired();
        ZonedDateTime creationDateTime = wsUser.getCreationDateTime();

        Optional<LocalDateTime> creationLocalDateTime = Optional.ofNullable(creationDateTime)
                .map(z -> z.withZoneSameInstant(ZoneId.systemDefault()))
                .map(ZonedDateTime::toLocalDateTime);

        User user = new User();
        Optional.ofNullable(id).ifPresent(user::setId);
        Optional.ofNullable(name).ifPresent(user::setName);
        Optional.ofNullable(email).ifPresent(user::setEmail);
        user.setActive(active);
        user.setAdmin(admin);
        user.setEmailVerified(emailVerified);
        user.setPasswordExpired(passwordExpired);
        creationLocalDateTime.ifPresent(user::setCreationTime);
        return user;
    }
}
