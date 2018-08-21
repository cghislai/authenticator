package com.charlyghislain.authenticator.api;


import com.charlyghislain.authenticator.api.domain.WsUser;
import com.charlyghislain.authenticator.api.domain.WsUserApplication;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

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

    @GET
    @Path("/application/list")
    List<WsUserApplication> listUserApplications();

    @DELETE
    @Path("/application/{appId}")
    void unlinkApplications(@PathParam("appId") Long applicationId);

}
