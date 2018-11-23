package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsApplication;
import com.charlyghislain.authenticator.domain.domain.Application;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

@ApplicationScoped
public class ApplicationConverter {

    @NonNull
    public Application toWsApplication(@NonNull WsApplication wsApplication) {
        Long id = wsApplication.getId();
        String name = wsApplication.getName();
        String endpointUrl = wsApplication.getEndpointUrl();
        Boolean active = wsApplication.getActive();
        ZonedDateTime creationDateTime = wsApplication.getCreationDateTime();
        Boolean canResetUserPassword = wsApplication.getCanResetUserPassword();
        Boolean canVerifyUserEmail = wsApplication.getCanVerifyUserEmail();
        Boolean addedUsersAreActive = wsApplication.getAddedUsersAreActive();
        Boolean existingUsersAreAddedOnTokenRequest = wsApplication.getExistingUsersAreAddedOnTokenRequest();

        Optional<LocalDateTime> creationLocalDateTime = Optional.ofNullable(creationDateTime)
                .map(ZonedDateTime::toLocalDateTime);

        Application application = new Application();
        Optional.ofNullable(id).ifPresent(application::setId);
        Optional.ofNullable(name).ifPresent(application::setName);
        Optional.ofNullable(endpointUrl).ifPresent(application::setEndpointUrl);
        Optional.ofNullable(active).ifPresent(application::setActive);
        creationLocalDateTime.ifPresent(application::setCreationTime);
        Optional.ofNullable(canResetUserPassword).ifPresent(application::setCanResetUserPassword);
        Optional.ofNullable(canVerifyUserEmail).ifPresent(application::setCanVerifyUserEmail);
        Optional.ofNullable(addedUsersAreActive).ifPresent(application::setAddedUsersAreActive);
        Optional.ofNullable(existingUsersAreAddedOnTokenRequest).ifPresent(application::setExistingUsersAreAddedOnTokenRequest);
        return application;
    }
}
