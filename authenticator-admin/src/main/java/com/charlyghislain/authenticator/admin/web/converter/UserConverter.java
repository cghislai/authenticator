package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsUser;
import com.charlyghislain.authenticator.domain.domain.User;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserConverter {

    public User translateWsUser(WsUser input) {
        if (input == null) {
            return null;
        }
        User result = new User();
        result.setId(input.getId());
        result.setName(input.getName());
        result.setEmail(input.getEmail());
//TODO
//result.setPassword(input.getPassword());
        result.setActive(input.isActive());
        result.setAdmin(input.isAdmin());
        result.setPasswordExpired(input.isPasswordExpired());
        result.setEmailVerified(input.isEmailVerified());
//result.setUserApplications(input.getUserApplications());
        return result;
    }
}
