package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsKey;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.ejb.service.ApplicationQueryService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

@ApplicationScoped
public class KeyConverter {

    @Inject
    private ApplicationQueryService applicationQueryService;

    public RsaKeyPair toRsaKeyPair(WsKey wsKey) {
        Long id = wsKey.getId();
        String name = wsKey.getName();
        boolean active = wsKey.isActive();
        boolean signingKey = wsKey.isSigningKey();
        boolean forApplicationSecrets = wsKey.isForApplicationSecrets();
        ZonedDateTime creationDateTime = wsKey.getCreationDateTime();
        Long applicationId = wsKey.getApplicationId();

        Optional<LocalDateTime> creationLocalDateTimeOptional = Optional.ofNullable(creationDateTime)
                .map(ZonedDateTime::toLocalDateTime);
        Optional<Application> applicationOptional = Optional.ofNullable(applicationId)
                .flatMap(applicationQueryService::findApplicationById);

        RsaKeyPair key = new RsaKeyPair();
        key.setId(id);
        key.setName(name);
        key.setActive(active);
        key.setSigningKey(signingKey);
        key.setForApplicationSecrets(forApplicationSecrets);
        key.setCreationTime(creationLocalDateTimeOptional.orElse(null));
        key.setApplication(applicationOptional.orElse(null));
        return key;
    }
}
