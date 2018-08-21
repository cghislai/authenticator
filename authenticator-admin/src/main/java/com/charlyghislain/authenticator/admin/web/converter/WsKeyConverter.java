package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsKey;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
        boolean signingKey = key.isSigningKey();

        Long applicationId = application.map(Application::getId)
                .orElse(null);
        ZonedDateTime crationZonedDateTime = creationTime.atZone(ZoneId.systemDefault());

        WsKey wsKey = new WsKey();
        wsKey.setId(id);
        wsKey.setActive(active);
        wsKey.setName(name);
        wsKey.setApplicationId(applicationId);
        wsKey.setCreationDateTime(crationZonedDateTime);
        wsKey.setForApplicationSecrets(forApplicationSecrets);
        wsKey.setSigningKey(signingKey);
        return wsKey;
    }

}
