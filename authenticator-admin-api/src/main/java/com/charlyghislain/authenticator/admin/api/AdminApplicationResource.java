package com.charlyghislain.authenticator.admin.api;


import com.charlyghislain.authenticator.admin.api.domain.WsApplication;
import com.charlyghislain.authenticator.admin.api.domain.WsApplicationFilter;
import com.charlyghislain.authenticator.admin.api.domain.WsApplicationHealth;
import com.charlyghislain.authenticator.admin.api.domain.WsPagination;
import com.charlyghislain.authenticator.admin.api.domain.WsResultList;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/application")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AdminApplicationResource {

    @NonNull
    @GET
    @Path("/list")
    WsResultList<WsApplication> listApplications(@BeanParam WsApplicationFilter wsApplicationFilter,
                                                 @BeanParam WsPagination wsPagination);

    @GET
    @Path("/{id}")
    WsApplication getApplication(@PathParam("id") Long applicationId);

    @NonNull
    @PUT
    @Path("/{id}")
    WsApplication updateApplication(@PathParam("id") Long applicationId, @Valid WsApplication wsApplication);

    @DELETE
    @Path("/{id}")
    void deleteApplication(@PathParam("id") Long applicationId);

    @NonNull
    @POST
    @Path("/")
    WsApplication createApplication(@Valid WsApplication wsApplication);

    @GET
    @Path("/{id}/secretToken")
    @Produces(MediaType.TEXT_PLAIN)
    String createApplicationToken(@PathParam("id") Long applicationId);

    @NonNull
    @GET
    @Path("/{id}/health")
    WsApplicationHealth getApplicationHealth(@PathParam("id") Long applicationId);

}
