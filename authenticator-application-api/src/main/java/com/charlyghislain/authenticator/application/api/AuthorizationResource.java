package com.charlyghislain.authenticator.application.api;


import com.charlyghislain.authenticator.application.api.domain.WsApplicationRole;
import com.charlyghislain.authenticator.application.api.domain.WsHealthCheckStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/authorization")
public interface AuthorizationResource {

    /**
     * None of the roles returned here may start with <pre>authenticator.</pre>This is a reserved prefix.
     *
     * @param userId the user id for which to retrieve the roles
     * @return a list of roles the user belongs to
     */
    @GET
    @Path("/user/{userId}/role/list")
    @Produces(MediaType.APPLICATION_JSON)
    List<WsApplicationRole> listUserRoles(@PathParam("userId") long userId);


    @GET
    @Path("/health")
    WsHealthCheckStatus checkAuthenticatorAuthorization();


}
