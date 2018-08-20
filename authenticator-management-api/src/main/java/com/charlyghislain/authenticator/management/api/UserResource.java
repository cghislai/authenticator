package com.charlyghislain.authenticator.management.api;

import com.charlyghislain.authenticator.management.api.domain.WsApplicationUser;
import com.charlyghislain.authenticator.management.api.domain.WsEmailVerificationToken;
import com.charlyghislain.authenticator.management.api.domain.WsPagination;
import com.charlyghislain.authenticator.management.api.domain.WsResultList;
import com.charlyghislain.authenticator.management.api.domain.WsUserApplicationFilter;

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
public interface UserResource {

    @POST
    WsApplicationUser createUser(WsApplicationUser wsUser);

    @GET
    @Path("/list")
    WsResultList<WsApplicationUser> listUsers(@BeanParam WsUserApplicationFilter filter, @BeanParam WsPagination wsPagination);

    @GET
    @Path("/{id}")
    WsApplicationUser getUser(@PathParam("id") Long userId);

    @PUT
    @Path("/{id}")
    WsApplicationUser updateUser(@PathParam("id") Long userId, WsApplicationUser user);

    @DELETE
    @Path("/{id}")
    void forgetUser(@PathParam("id") Long userId);

    @GET
    @Path("/{id}/email/verificationToken")
    WsEmailVerificationToken getEmailVerificationToken(@PathParam("id") Long userId);

    @POST
    @Path("/{id}/email/verification")
    void checkEmailVerification(@PathParam("id") Long userId,
                                String verificationToken);


}
