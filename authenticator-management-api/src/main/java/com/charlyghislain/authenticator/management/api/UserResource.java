package com.charlyghislain.authenticator.management.api;

import com.charlyghislain.authenticator.management.api.domain.*;
import org.checkerframework.checker.nullness.qual.NonNull;

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

    @NonNull
    @POST
    WsApplicationUser createUser(WsApplicationUserWithPassword wsUser);

    @NonNull
    @GET
    @Path("/list")
    WsResultList<WsApplicationUser> listUsers(@BeanParam WsUserApplicationFilter filter, @BeanParam WsPagination wsPagination);

    @NonNull
    @GET
    @Path("/{id}")
    WsApplicationUser getUser(@PathParam("id") Long userId);

    @NonNull
    @PUT
    @Path("/{id}")
    WsApplicationUser updateUser(@PathParam("id") Long userId, WsApplicationUser user);

    @NonNull
    @PUT
    @Path("/{id}/password")
    WsApplicationUser updateUserPassword(@PathParam("id") Long userId, String password);

    @NonNull
    @GET
    @Path("/{id}/password/resetToken")
    WsPasswordResetToken createNewPasswordResetToken(@PathParam("id") Long userId);

    @POST
    @Path("/{id}/password/reset")
    void resetUserPassword(@PathParam("id") Long userId, WsPasswordReset wsPasswordReset);

    @DELETE
    @Path("/{id}")
    void forgetUser(@PathParam("id") Long userId);

    @NonNull
    @GET
    @Path("/{id}/email/verificationToken")
    WsEmailVerificationToken getEmailVerificationToken(@PathParam("id") Long userId);

    @POST
    @Path("/{id}/email/verification")
    void checkEmailVerification(@PathParam("id") Long userId, String verificationToken);

}
