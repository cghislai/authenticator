package com.charlyghislain.authenticator.application.api;


import com.charlyghislain.authenticator.application.api.domain.WsApplicationUser;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;

@Path("/event/user")
@Consumes(MediaType.APPLICATION_JSON)
public interface UserEventResource {

    @POST
    @Path("/added")
    CompletionStage<Void> userAdded(WsApplicationUser wsApplicationUser);

    @POST
    @Path("/emailVerified")
    CompletionStage<Void> userEmailVerified(WsApplicationUser wsApplicationUser);

    @POST
    @Path("/removed")
    CompletionStage<Void> userRemoved(Long id);

}
