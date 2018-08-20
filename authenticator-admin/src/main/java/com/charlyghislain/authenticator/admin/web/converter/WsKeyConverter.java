package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsKey;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class WsKeyConverter {

    public WsKey toWskey(RsaKeyPair key) {
        Long id = key.getId();
        String name = key.getName();
        boolean active = key.isActive();
        Optional<Application> application = key.getApplication();
        LocalDateTime creationTime = key.getCreationTime();
        boolean forApplicationSecrets = key.isForApplicationSecrets();

        Long applicationId = application.map(Application::getId)
                .orElse(null);

        WsKey wsKey = new WsKey();
        wsKey.setId(id);
        wsKey.setActive(active);
        wsKey.setName(name);
        wsKey.setApplicationId(applicationId);
        wsKey.setCreationDateTime(creationTime);
        wsKey.setForApplicationSecrets(forApplicationSecrets);
        return wsKey;
    }

}
