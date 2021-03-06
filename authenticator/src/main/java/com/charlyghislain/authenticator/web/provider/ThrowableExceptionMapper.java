package com.charlyghislain.authenticator.web.provider;

import com.charlyghislain.authenticator.api.domain.WsError;
import com.charlyghislain.authenticator.api.error.AuthenticatorWebError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = LoggerFactory.getLogger(ThrowableExceptionMapper.class);


    @Override
    public Response toResponse(Throwable exception) {
        LOG.warn("Uncaught exception while serving request", exception);

        WsError wsError = new WsError();
        wsError.setCode(AuthenticatorWebError.UNEXPECTED_ERROR.name());

        return Response
                .status(500)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(wsError)
                .build();
    }
}
