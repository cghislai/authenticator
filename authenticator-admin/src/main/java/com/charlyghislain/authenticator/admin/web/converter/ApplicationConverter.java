package com.charlyghislain.authenticator.admin.web.converter;


import com.charlyghislain.authenticator.admin.api.domain.WsApplication;
import com.charlyghislain.authenticator.domain.domain.Application;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@ApplicationScoped
public class ApplicationConverter {

    public Application toWsApplication(WsApplication wsApplication) {
        Long id = wsApplication.getId();
        String name = wsApplication.getName();
        String endpointUrl = wsApplication.getEndpointUrl();
        Boolean active = wsApplication.getActive();
        ZonedDateTime creationDateTime = wsApplication.getCreationDateTime();
        Boolean canResetUserPassword = wsApplication.getCanResetUserPassword();
        Boolean canVerifyUserEmail = wsApplication.getCanVerifyUserEmail();
        Boolean addedUsersAreActive = wsApplication.getAddedUsersAreActive();
        Boolean existingUsersAreAddedOnTokenRequest = wsApplication.getExistingUsersAreAddedOnTokenRequest();

        LocalDateTime creationLocalDateTime = creationDateTime.toLocalDateTime();

        Application application = new Application();
        application.setId(id);
        application.setName(name);
        application.setEndpointUrl(endpointUrl);
        application.setActive(active);
        application.setCreationTime(creationLocalDateTime);
        application.setAddedUsersAreActive(addedUsersAreActive);
        application.setCanResetUserPassword(canResetUserPassword);
        application.setCanVerifyUserEmail(canVerifyUserEmail);
        application.setExistingUsersAreAddedOnTokenRequest(existingUsersAreAddedOnTokenRequest);
        return application;
    }
}
