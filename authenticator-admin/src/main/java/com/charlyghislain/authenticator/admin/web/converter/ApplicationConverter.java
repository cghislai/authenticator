package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsApplication;
import com.charlyghislain.authenticator.domain.domain.Application;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationConverter {

    public Application toWsApplication(WsApplication input) {
        if (input == null) {
            return null;
        }
        Application result = new Application();
        result.setId(input.getId());
        result.setName(input.getName());
        result.setEndpointUrl(input.getEndpointUrl());
        result.setActive(input.isActive());
        return result;
    }
}
