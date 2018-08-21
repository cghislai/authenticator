package com.charlyghislain.authenticator.admin.api;


import com.charlyghislain.authenticator.admin.api.domain.WsPagination;
import com.charlyghislain.authenticator.admin.api.domain.WsResultList;
import com.charlyghislain.authenticator.admin.api.domain.WsUser;
import com.charlyghislain.authenticator.admin.api.domain.WsUserApplication;
import com.charlyghislain.authenticator.admin.api.domain.WsUserFilter;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AdminUserResource {

    @GET
    @Path("/list")
    WsResultList<WsUser> listUsers(@BeanParam WsUserFilter wsUserFilter, @BeanParam WsPagination wsPagination);

    @GET
    @Path("/{id}")
    WsUser getUser(@PathParam("id") Long userId);

    @POST
    @Path("/")
    WsUser createUser(WsUser wsUser);

    @PUT
    @Path("/{id}")
    WsUser updateUser(@PathParam("id") Long userId, WsUser wsUser);

    @PUT
    @Path("/{id}/password")
    @Consumes(MediaType.TEXT_PLAIN)
    void updatePassword(@PathParam("id") Long userId, String password);

    @GET
    @Path("/{id}/application/list")
    WsResultList<WsUserApplication> listUserApplications(@PathParam("id") Long userId,
                                                         @BeanParam WsPagination wsPagination);

    @PUT
    @Path("/{id}/application/{appId}/state/active")
    WsUserApplication activateUserApplication(@PathParam("id") Long userId, @PathParam("appId") Long applicationId);

    @DELETE
    @Path("/{id}/application/{appId}/state/active")
    WsUserApplication deactivateUserApplication(@PathParam("id") Long userId, @PathParam("appId") Long applicationId);

}
