package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsUserApplication;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import org.checkerframework.checker.nullness.qual.NonNull;

public class WsUserApplicationConverter {

    @NonNull
    public WsUserApplication toWsUserApplication(@NonNull UserApplication userApplication) {
        Long id = userApplication.getId();
        User user = userApplication.getUser();
        Application application = userApplication.getApplication();
        boolean active = userApplication.isActive();

        Long userId = user.getId();
        String userName = user.getName();

        Long applicationId = application.getId();
        String applicationName = application.getName();

        WsUserApplication wsUserApplication = new WsUserApplication();
        wsUserApplication.setId(id);
        wsUserApplication.setApplicationId(applicationId);
        wsUserApplication.setApplicationName(applicationName);
        wsUserApplication.setUserId(userId);
        wsUserApplication.setUserName(userName);
        wsUserApplication.setActive(active);
        return wsUserApplication;
    }
}
