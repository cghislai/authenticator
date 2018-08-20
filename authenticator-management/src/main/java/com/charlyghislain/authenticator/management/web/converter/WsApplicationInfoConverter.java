package com.charlyghislain.authenticator.management.web.converter;


import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.management.api.domain.WsApplicationInfo;

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
