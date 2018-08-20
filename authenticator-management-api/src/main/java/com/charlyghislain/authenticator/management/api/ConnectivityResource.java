package com.charlyghislain.authenticator.management.api;


import com.charlyghislain.authenticator.management.api.domain.WsApplicationInfo;
import com.charlyghislain.authenticator.management.api.domain.WsHealthCheckStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/me")
@Produces(MediaType.APPLICATION_JSON)
public interface ConnectivityResource {

    @GET
    @Path("/health")
    WsHealthCheckStatus authenticationHealthCheck();

    @GET
    @Path("/info")
    WsApplicationInfo getMyInfo();

}
