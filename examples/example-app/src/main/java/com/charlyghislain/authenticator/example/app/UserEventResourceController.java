package com.charlyghislain.authenticator.example.app;

import com.charlyghislain.authenticator.application.api.UserEventResource;
import com.charlyghislain.authenticator.application.api.domain.WsApplicationUser;

import javax.annotation.security.RolesAllowed;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@RolesAllowed(ApplicationRoles.AUTHNETICATOR_APP_ROLE)
public class UserEventResourceController implements UserEventResource {

    @Override
    public CompletionStage<Void> userAdded(WsApplicationUser wsApplicationUser) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<Void> userEmailVerified(WsApplicationUser wsApplicationUser) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<Void> userRemoved(Long id) {
        return CompletableFuture.completedFuture(null);
    }
}
