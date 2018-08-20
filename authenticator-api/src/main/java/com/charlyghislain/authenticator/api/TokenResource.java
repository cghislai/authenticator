package com.charlyghislain.authenticator.api;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/token")
public interface TokenResource {

    @Operation(
            summary = "Obtain a new auth token for the specified application audience",
            operationId = "applicationLogin"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "authorized response",
                    content = {@Content(
                            mediaType = "text/plain"
                    )}
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "not authorized",
                    ref = "#/components/schemas/WsError"
            ),
    })
    @SecurityRequirement(name = "http")
    @GET
    @Path("/{appName}")
    @Produces(MediaType.TEXT_PLAIN)
    String applicationLogin(
            @Parameter(
                    name = "appName",
                    description = "The application name for which to login",
                    required = true
            )
            @PathParam("appName") String appName
    );


    @Operation(
            summary = "Obtain a new auth token for this authenticator audience",
            operationId = "login"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "authorized response",
                    content = {@Content(
                            mediaType = "text/plain"
                    )}
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "not authorized",
                    ref = "#/components/schemas/WsError"
            ),
    })
    @SecurityRequirement(name = "http")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    String login();
}
