package com.charlyghislain.authenticator.application.client.converter;


import com.charlyghislain.authenticator.application.api.domain.WsApplicationUser;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.UserApplication;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserApplicationConverter {

    public UserApplication toUserApplication(WsApplicationUser wsApplicationUser, Application application) {
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
        user.setPasswordExpired(passwordExpired);
        user.setEmailVerified(emailVerified);

        UserApplication userApplication = new UserApplication();
        userApplication.setActive(active);
        userApplication.setUser(user);
        userApplication.setApplication(application);
        return userApplication;
    }
}
