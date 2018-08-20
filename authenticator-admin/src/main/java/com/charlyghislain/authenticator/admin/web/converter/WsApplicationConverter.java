package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsApplication;
import com.charlyghislain.authenticator.domain.domain.Application;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WsApplicationConverter {

    public WsApplication toWsapplication(Application application) {
        Long id = application.getId();
        String name = application.getName();
        boolean active = application.isActive();
        String endpointUrl = application.getEndpointUrl();

        WsApplication wsApplication = new WsApplication();
        wsApplication.setId(id);
        wsApplication.setActive(active);
        wsApplication.setName(name);
        wsApplication.setEndpointUrl(endpointUrl);
        return wsApplication;
    }

}
