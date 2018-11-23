package com.charlyghislain.authenticator.example.app.rest;

import com.charlyghislain.authenticator.application.api.UserEventResource;
import com.charlyghislain.authenticator.application.api.domain.WsApplicationUser;
import com.charlyghislain.authenticator.example.app.ApplicationRoles;
import com.charlyghislain.authenticator.example.app.client.AuthenticatorManagementClient;
import com.charlyghislain.authenticator.management.api.domain.WsEmailVerificationToken;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.glassfish.jersey.media.sse.OutboundEvent;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@RolesAllowed(ApplicationRoles.AUTHENTICATOR_APP_ROLE)
public class UserEventResourceController implements UserEventResource {

    private static final String USER_ADDED_EVENT_ID = "user-added";
    private static final String EMAIL_VERIFIED_EVENT_ID = "email-verified";

    @Inject
    private ServerEventListeners serverEventListeners;
    @Inject
    private AuthenticatorManagementClient managementClient;

    @NonNull
    @Override
    public CompletionStage<Void> userAdded(@NonNull WsApplicationUser wsApplicationUser) {
        this.broadcastEvent(USER_ADDED_EVENT_ID, "id: " + wsApplicationUser.getId());

        boolean emailVerified = wsApplicationUser.isEmailVerified();

        if (emailVerified) {
            this.broadcastEvent(USER_ADDED_EVENT_ID, "Email already verified");
            setUserActive(USER_ADDED_EVENT_ID, wsApplicationUser);
        } else {
            this.broadcastEvent(USER_ADDED_EVENT_ID, "Email needs verification");
            startEmailVerification(USER_ADDED_EVENT_ID, wsApplicationUser);
        }

        return CompletableFuture.completedFuture(null);
    }


    @NonNull
    @Override
    public CompletionStage<Void> userEmailVerified(@NonNull WsApplicationUser wsApplicationUser) {
        this.broadcastEvent(EMAIL_VERIFIED_EVENT_ID, "id: " + wsApplicationUser.getId());

        setUserActive(EMAIL_VERIFIED_EVENT_ID, wsApplicationUser);

        return CompletableFuture.completedFuture(null);
    }

    @NonNull
    @Override
    public CompletionStage<Void> userRemoved(Long id) {
        this.broadcastEvent("user-removed", "id: " + id);
        return CompletableFuture.completedFuture(null);
    }


    private void broadcastEvent(String id, String data) {
        OutboundEvent event = new OutboundEvent.Builder()
                .name("events-rs")
                .mediaType(MediaType.TEXT_PLAIN_TYPE)
                .data(String.class, id + " : " + data)
                .build();

        this.serverEventListeners.getListeners()
                .stream()
                .forEach(list -> list.broadcast(event));
    }


    private void setUserActive(String eventId, WsApplicationUser wsApplicationUser) {
        boolean active = wsApplicationUser.isActive();
        Long userId = Optional.ofNullable(wsApplicationUser.getId()).orElseThrow(IllegalStateException::new);
        if (!active) {
            this.broadcastEvent(eventId, "User not yet active. Setting active");
            managementClient.setUserActive(userId);
            this.broadcastEvent(eventId, "User is active");
        } else {
            this.broadcastEvent(eventId, "User is already active");
        }
    }

    private void startEmailVerification(String eventId, WsApplicationUser wsApplicationUser) {
        WsEmailVerificationToken mailVerificationToken = managementClient.getMailVerificationToken(wsApplicationUser.getId());
        this.broadcastEvent(eventId, "Got email verification token " + mailVerificationToken.getToken());

        if (new Random().nextBoolean()) {
            this.broadcastEvent(eventId, "Pretending mail is verified");
            managementClient.verifyMailVerificationToken(wsApplicationUser.getId(), mailVerificationToken.getToken());
        } else {
            this.broadcastEvent(eventId, "Not verifying mail (random branch)");
        }
    }
}
