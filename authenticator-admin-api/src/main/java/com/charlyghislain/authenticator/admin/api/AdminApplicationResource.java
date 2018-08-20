package com.charlyghislain.authenticator.admin.api;


import com.charlyghislain.authenticator.admin.api.domain.WsApplication;
import com.charlyghislain.authenticator.admin.api.domain.WsApplicationFilter;
import com.charlyghislain.authenticator.admin.api.domain.WsApplicationHealth;
import com.charlyghislain.authenticator.admin.api.domain.WsPagination;
import com.charlyghislain.authenticator.admin.api.domain.WsResultList;

import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/application")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AdminApplicationResource {

    @GET
    @Path("/list")
    WsResultList<WsApplication> listApplications(@BeanParam WsApplicationFilter wsApplicationFilter,
                                                 @BeanParam WsPagination wsPagination);

    @GET
    @Path("/{id}")
    WsApplication getApplication(@PathParam("id") Long applicationId);

    @PUT
    @Path("/{id}")
    WsApplication updateApplication(@PathParam("id") Long applicationId, @Valid WsApplication wsApplication);

    @POST
    @Path("/")
    WsApplication createApplication(@Valid WsApplication wsApplication);

    @GET
    @Path("/{id}/secretToken")
    @Produces(MediaType.TEXT_PLAIN)
    String createApplicationToken(@PathParam("id") Long applicationId);

    @GET
    @Path("/{id}/health")
    WsApplicationHealth getApplicationHealth(@PathParam("id") Long applicationId);

}
