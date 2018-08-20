package com.charlyghislain.authenticator.api;

import com.charlyghislain.authenticator.api.domain.WsApplicationInfo;
import com.charlyghislain.authenticator.api.domain.WsAuthenticatorInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("info")
public interface InfoResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    WsAuthenticatorInfo getInfo();

    @GET
    @Path("/publicKey")
    @Produces(MediaType.TEXT_PLAIN)
    String getActivePublicKey();

    @GET
    @Path("/app/{appName}/info")
    @Produces(MediaType.APPLICATION_JSON)
    WsApplicationInfo getApplicationInfo(@PathParam("appName") String appName);

    @GET
    @Path("/app/{appName}/publicKey")
    @Produces(MediaType.TEXT_PLAIN)
    String getApplicationSigningPublicKey(@PathParam("appName") String appName);

}
