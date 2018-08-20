package com.charlyghislain.authenticator.web.converter;


import com.charlyghislain.authenticator.api.domain.WsApplicationInfo;
import com.charlyghislain.authenticator.domain.domain.Application;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WsApplicationInfoConverter {

    public WsApplicationInfo toWsApplicationInfo(Application application) {
        WsApplicationInfo wsApplicationInfo = new WsApplicationInfo();
        wsApplicationInfo.setEndpointUrl(application.getEndpointUrl());
        wsApplicationInfo.setName(application.getName());
        return wsApplicationInfo;
    }
}
