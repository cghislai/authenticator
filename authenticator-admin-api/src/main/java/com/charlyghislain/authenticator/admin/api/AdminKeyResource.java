package com.charlyghislain.authenticator.admin.api;


import com.charlyghislain.authenticator.admin.api.domain.WsKey;
import com.charlyghislain.authenticator.admin.api.domain.WsKeyFilter;
import com.charlyghislain.authenticator.admin.api.domain.WsPagination;
import com.charlyghislain.authenticator.admin.api.domain.WsResultList;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/key")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AdminKeyResource {

    @NonNull
    @GET
    @Path("/list")
    WsResultList<WsKey> listKeys(@BeanParam WsKeyFilter wsKeyFilter, @BeanParam WsPagination wsPagination);

    @GET
    @Path("/{id}")
    WsKey getKey(@PathParam("id") Long keyId);

    @NonNull
    @POST
    @Path("/")
    WsKey createKey(WsKey wsKey);

    @NonNull
    @PUT
    @Path("/{id}")
    WsKey updateKey(@PathParam("id") Long keyId, WsKey wsKey);

    @NonNull
    @GET
    @Path("/{id}/public")
    @Produces(MediaType.TEXT_PLAIN)
    String getPublicKeyPEM(@PathParam("id") Long keyId);

}
