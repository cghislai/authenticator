package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsKey;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.ejb.service.ApplicationQueryService;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

@ApplicationScoped
public class KeyConverter {

    @Inject
    private ApplicationQueryService applicationQueryService;

    @NonNull
    public RsaKeyPair toRsaKeyPair(@NonNull WsKey wsKey) {
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
        Optional.ofNullable(id).ifPresent(key::setId);
        Optional.ofNullable(name).ifPresent(key::setName);
        key.setActive(active);
        key.setSigningKey(signingKey);
        key.setForApplicationSecrets(forApplicationSecrets);
        creationLocalDateTimeOptional.ifPresent(key::setCreationTime);
        applicationOptional.ifPresent(key::setApplication);
        return key;
    }
}
