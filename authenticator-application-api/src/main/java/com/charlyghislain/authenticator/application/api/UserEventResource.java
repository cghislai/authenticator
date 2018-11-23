package com.charlyghislain.authenticator.application.api;


import com.charlyghislain.authenticator.application.api.domain.WsApplicationUser;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;

@Path("/event/user")
@Consumes(MediaType.APPLICATION_JSON)
public interface UserEventResource {

    @NonNull
    @POST
    @Path("/added")
    CompletionStage<Void> userAdded(WsApplicationUser wsApplicationUser);

    @NonNull
    @POST
    @Path("/emailVerified")
    CompletionStage<Void> userEmailVerified(WsApplicationUser wsApplicationUser);

    @NonNull
    @POST
    @Path("/removed")
    CompletionStage<Void> userRemoved(Long id);

}
