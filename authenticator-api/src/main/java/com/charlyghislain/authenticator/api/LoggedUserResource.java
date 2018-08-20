package com.charlyghislain.authenticator.api;


import com.charlyghislain.authenticator.api.domain.WsUser;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/me")
public interface LoggedUserResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    WsUser getLoggedUser();

    @PUT
    @Path("/password")
    @Consumes(MediaType.TEXT_PLAIN)
    void updatePassword(String password);

    @PUT
    @Path("/email")
    @Consumes(MediaType.TEXT_PLAIN)
    void updateEmail(String email);

    @PUT
    @Path("/name")
    @Consumes(MediaType.TEXT_PLAIN)
    void updateName(String name);
}
