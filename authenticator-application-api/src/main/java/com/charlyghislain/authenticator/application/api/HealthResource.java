package com.charlyghislain.authenticator.application.api;


import com.charlyghislain.authenticator.application.api.domain.WsHealthCheckResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public interface HealthResource {

    @GET
    WsHealthCheckResponse getHealth();

}
