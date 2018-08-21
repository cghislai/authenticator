package com.charlyghislain.authenticator.api;

import com.charlyghislain.authenticator.api.domain.WsPasswordReset;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/password")
public interface PasswordResource {

    @POST
    @Path("/reset")
    @Consumes(MediaType.APPLICATION_JSON)
    void resetPassword(WsPasswordReset wsPasswordReset);
}
