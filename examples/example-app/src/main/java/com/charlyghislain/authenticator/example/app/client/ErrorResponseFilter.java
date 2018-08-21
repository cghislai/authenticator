package com.charlyghislain.authenticator.example.app.client;

import com.charlyghislain.authenticator.example.app.rest.ServerEventListeners;
import org.glassfish.jersey.media.sse.OutboundEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.URI;

@Provider
@ApplicationScoped
public class ErrorResponseFilter implements ClientResponseFilter {

    @Inject
    private ServerEventListeners serverEventListeners;


    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        int status = responseContext.getStatus();
        if (status >= 400) {
            URI uri = requestContext.getUri();
            String uriString = uri.toString();
            this.broadcastEvent("error-response", "Request to " + uriString + " got response status " + status);
        }
    }


    private void broadcastEvent(String id, String data) {
        OutboundEvent event = new OutboundEvent.Builder()
                .name("rest-client")
                .mediaType(MediaType.TEXT_PLAIN_TYPE)
                .data(String.class, id + " : " + data)
                .build();

        this.serverEventListeners.getListeners()
                .forEach(list -> list.broadcast(event));
    }
}
