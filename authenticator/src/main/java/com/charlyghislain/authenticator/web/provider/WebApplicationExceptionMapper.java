package com.charlyghislain.authenticator.web.provider;

import com.charlyghislain.authenticator.api.domain.WsError;
import com.charlyghislain.authenticator.api.error.AuthenticatorWebError;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    private static final Logger LOG = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);


    @Override
    public Response toResponse(@NonNull WebApplicationException exception) {
        LOG.warn("Uncaught exception while serving request", exception);

        WsError wsError = new WsError();
        wsError.setCode(AuthenticatorWebError.UNEXPECTED_ERROR.name());

        return Response
                .status(exception.getResponse().getStatus())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(wsError)
                .build();
    }
}
