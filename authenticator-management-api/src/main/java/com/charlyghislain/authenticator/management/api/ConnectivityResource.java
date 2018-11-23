package com.charlyghislain.authenticator.management.api;


import com.charlyghislain.authenticator.management.api.domain.WsApplicationInfo;
import com.charlyghislain.authenticator.management.api.domain.WsHealthCheckStatus;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/me")
@Produces(MediaType.APPLICATION_JSON)
public interface ConnectivityResource {

    @NonNull
    @GET
    @Path("/health")
    WsHealthCheckStatus authenticationHealthCheck();

    @NonNull
    @GET
    @Path("/info")
    WsApplicationInfo getMyInfo();

}
