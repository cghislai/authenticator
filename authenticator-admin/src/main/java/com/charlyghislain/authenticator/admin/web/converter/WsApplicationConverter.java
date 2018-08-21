package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsApplication;
import com.charlyghislain.authenticator.domain.domain.Application;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@ApplicationScoped
public class WsApplicationConverter {

    public WsApplication toWsapplication(Application application) {
        Long id = application.getId();
        String name = application.getName();
        boolean active = application.isActive();
        String endpointUrl = application.getEndpointUrl();
        LocalDateTime creationTime = application.getCreationTime();
        boolean canResetUserPassword = application.isCanResetUserPassword();
        boolean canVerifyUserEmail = application.isCanVerifyUserEmail();
        boolean addedUsersAreActive = application.isAddedUsersAreActive();
        boolean existingUsersAreAddedOnTokenRequest = application.isExistingUsersAreAddedOnTokenRequest();

        ZonedDateTime creationZonedDateTime = creationTime.atZone(ZoneId.systemDefault());

        WsApplication wsApplication = new WsApplication();
        wsApplication.setId(id);
        wsApplication.setActive(active);
        wsApplication.setName(name);
        wsApplication.setEndpointUrl(endpointUrl);
        wsApplication.setCreationDateTime(creationZonedDateTime);
        wsApplication.setCanResetUserPassword(canResetUserPassword);
        wsApplication.setCanVerifyUserEmail(canVerifyUserEmail);
        wsApplication.setAddedUsersAreActive(addedUsersAreActive);
        wsApplication.setExistingUsersAreAddedOnTokenRequest(existingUsersAreAddedOnTokenRequest);
        return wsApplication;
    }

}
