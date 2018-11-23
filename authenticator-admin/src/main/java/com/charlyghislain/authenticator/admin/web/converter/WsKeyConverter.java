package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsKey;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@ApplicationScoped
public class WsKeyConverter {

    @NonNull
    public WsKey toWskey(@NonNull RsaKeyPair key) {
        Long id = key.getId();
        String name = key.getName();
        boolean active = key.isActive();
        Optional<Application> application = key.getApplication();
        LocalDateTime creationTime = key.getCreationTime();
        boolean forApplicationSecrets = key.isForApplicationSecrets();
        boolean signingKey = key.isSigningKey();

        Optional<Long> applicationId = application.map(Application::getId);
        Optional<ZonedDateTime> creationZonedDateTime = Optional.ofNullable(creationTime)
                .map(t -> t.atZone(ZoneId.systemDefault()));

        WsKey wsKey = new WsKey();
        Optional.ofNullable(id).ifPresent(wsKey::setId);
        wsKey.setActive(active);
        Optional.ofNullable(name).ifPresent(wsKey::setName);
        applicationId.ifPresent(wsKey::setApplicationId);
        creationZonedDateTime.ifPresent(wsKey::setCreationDateTime);
        wsKey.setForApplicationSecrets(forApplicationSecrets);
        wsKey.setSigningKey(signingKey);
        return wsKey;
    }

}
