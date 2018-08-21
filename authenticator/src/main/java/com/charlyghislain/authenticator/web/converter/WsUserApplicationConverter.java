package com.charlyghislain.authenticator.web.converter;


import com.charlyghislain.authenticator.api.domain.WsUserApplication;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.UserApplication;

public class WsUserApplicationConverter {

    public WsUserApplication toWsUserApplication(UserApplication userApplication) {
        Application application = userApplication.getApplication();


        Long applicationId = application.getId();
        String applicationName = application.getName();

        WsUserApplication wsUserApplication = new WsUserApplication();
        wsUserApplication.setApplicationId(applicationId);
        wsUserApplication.setApplicationName(applicationName);
        return wsUserApplication;
    }
}
