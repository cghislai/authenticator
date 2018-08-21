package com.charlyghislain.authenticator.example.app.rest;

import com.charlyghislain.authenticator.example.app.ApplicationRoles;
import com.charlyghislain.authenticator.example.app.client.AuthenticatorManagementClient;
import com.charlyghislain.authenticator.example.app.domain.WsPasswordResetTokenWithUid;
import com.charlyghislain.authenticator.example.app.domain.WsRegistration;
import com.charlyghislain.authenticator.management.api.domain.WsApplicationUser;
import com.charlyghislain.authenticator.management.api.domain.WsPasswordResetToken;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Path("/example")
@RolesAllowed(ApplicationRoles.EXAMPLE_ROLE)
@RequestScoped
public class ExampleResourceController {

    public static final String REGISTRATION_EVENT_ID = "registration";
    public static final String PASSWORD_RECOVERY_EVENT_ID = "password-recovery";
    @Inject
    private Instance<JsonWebToken> jsonWebToken;
    @Inject
    @Claim("uid")
    private Instance<Long> uid;
    @Inject
    private AuthenticatorManagementClient managementClient;
    @Inject
    private ServerEventListeners serverEventListeners;

    @GET
    @Path("/jwt")
    public Map<String, Object> getToken() {
        JsonWebToken jwt = this.jsonWebToken.get();
        return jwt.getClaimNames()
                .stream()
                .filter(k -> !k.equals("raw_token"))
                .collect(Collectors.toMap(
                        Function.identity(),
                        key -> this.getClaim(jwt, key)
                ));
    }

    @GET
    @Path("/user")
    public WsApplicationUser getUser() {
        this.broadcastEvent("getUser", "fetching user " + this.uid.get());
        return this.managementClient.getUser(this.uid.get());
    }

    @GET
    @Path("/appName")
    @PermitAll
    @Produces("text/plain")
    public String getAppName() {
        this.broadcastEvent("getAppName", "fetching app info");
        return this.managementClient.getMyAppInfo().getName();
    }


    @GET
    @Path("/events")
    @PermitAll
    @Produces("text/event-stream; charset=utf-8")
    public EventOutput listenEvents(@Context HttpServletResponse servletResponse) {
        servletResponse.setHeader("Connection", "Keep-Alive");
        EventOutput eventOutput = new EventOutput();
        SseBroadcaster sseBroadcaster = new SseBroadcaster();
        sseBroadcaster.add(eventOutput);
        serverEventListeners.addListener(sseBroadcaster);

        return eventOutput;
    }


    @POST
    @Path("/user")
    @PermitAll
    public WsApplicationUser register(@Valid WsRegistration registration) throws IOException {
        this.broadcastEvent(REGISTRATION_EVENT_ID, "new registration for user " + registration.name);

        this.broadcastEvent(REGISTRATION_EVENT_ID, "checking duplicate mail");
        this.managementClient.findUserByMail(registration.email)
                .ifPresent(this::throwUserWithSameMailExists);

        this.broadcastEvent(REGISTRATION_EVENT_ID, "checking duplicate name");
        this.managementClient.findUserByName(registration.name)
                .ifPresent(this::throwUserWithSameNameExists);

        this.broadcastEvent(REGISTRATION_EVENT_ID, "Creating inactive user");
        WsApplicationUser wsApplicationUser = new WsApplicationUser();
        wsApplicationUser.setEmail(registration.email);
        wsApplicationUser.setName(registration.name);
        wsApplicationUser.setActive(false);
        WsApplicationUser createdUser = this.managementClient.createUser(wsApplicationUser);
        this.broadcastEvent(REGISTRATION_EVENT_ID, "Create user " + createdUser.getId());

        this.broadcastEvent(REGISTRATION_EVENT_ID, "Setting user password");
        WsApplicationUser updatedUser = this.managementClient.setPassword(createdUser, registration.password);

        this.broadcastEvent(REGISTRATION_EVENT_ID, "user created. Active: " + updatedUser.isActive());
        return updatedUser;
    }


    @POST
    @Path("/user/password")
    @PermitAll
    @Consumes(MediaType.TEXT_PLAIN)
    public WsPasswordResetTokenWithUid recoverPassword(String userName) throws IOException {
        this.broadcastEvent(PASSWORD_RECOVERY_EVENT_ID, "new password recovery for user " + userName);

        this.broadcastEvent(PASSWORD_RECOVERY_EVENT_ID, "finding user");
        WsApplicationUser wsApplicationUser = this.managementClient.findUserByName(userName)
                .orElseThrow(NotFoundException::new);

        this.broadcastEvent(PASSWORD_RECOVERY_EVENT_ID, "Fetching password reset token");
        WsPasswordResetToken resetToken = this.managementClient.createPasswordResetToken(wsApplicationUser.getId());

        this.broadcastEvent(PASSWORD_RECOVERY_EVENT_ID, "Pretending mail has been sent and token is in query param from mail link - returning it right away");

        WsPasswordResetTokenWithUid passwordResetTokenWithUid = new WsPasswordResetTokenWithUid();
        passwordResetTokenWithUid.setToken(resetToken.getToken());
        passwordResetTokenWithUid.setUserId(wsApplicationUser.getId());
        return passwordResetTokenWithUid;
    }

    private void broadcastEvent(String id, String data) {
        OutboundEvent event = new OutboundEvent.Builder()
                .name("example-rs")
                .mediaType(MediaType.TEXT_PLAIN_TYPE)
                .data(String.class, id + " : " + data)
                .build();

        this.serverEventListeners.getListeners()
                .stream()
                .forEach(list -> list.broadcast(event));
    }

    private void throwUserWithSameNameExists(WsApplicationUser wsApplicationUser) {
        broadcastEvent("duplicate", "user exist with same name: id " + wsApplicationUser.getId());
        throw new WebApplicationException("User with this name already exists", Response.Status.NOT_ACCEPTABLE);
    }

    private void throwUserWithSameMailExists(WsApplicationUser wsApplicationUser) {
        broadcastEvent("duplicate", "user exist with same mail: id " + wsApplicationUser.getId());
        throw new WebApplicationException("User with this mail already exists", Response.Status.NOT_ACCEPTABLE);
    }


    private Object getClaim(JsonWebToken jwt, String key) {
        Object value = jwt.getClaim(key);
        if (value instanceof String) {
            return value;
        }
        if (value instanceof Collection) {
            return ((Collection) value).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return value.toString();
    }
}
