package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsUser;
import com.charlyghislain.authenticator.domain.domain.User;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@ApplicationScoped
public class WsUserConverter {

    @NonNull
    public WsUser toWsuser(@NonNull User user) {
        Long id = user.getId();
        String name = user.getName();
        String email = user.getEmail();
        boolean active = user.isActive();
        boolean admin = user.isAdmin();
        boolean emailVerified = user.isEmailVerified();
        boolean passwordExpired = user.isPasswordExpired();
        LocalDateTime creationTime = user.getCreationTime();

        ZonedDateTime creationZonedDateTime = creationTime.atZone(ZoneId.systemDefault());

        WsUser wsUser = new WsUser();
        wsUser.setId(id);
        wsUser.setName(name);
        wsUser.setEmail(email);
        wsUser.setActive(active);
        wsUser.setAdmin(admin);
        wsUser.setEmailVerified(emailVerified);
        wsUser.setPasswordExpired(passwordExpired);
        wsUser.setCreationDateTime(creationZonedDateTime);
        return wsUser;
    }

}
