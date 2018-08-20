package com.charlyghislain.authenticator.management.web.provider;

import com.charlyghislain.authenticator.admin.api.domain.WsError;
import com.charlyghislain.authenticator.admin.api.error.AuthenticatorAdminWebError;
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
        wsError.setCode(AuthenticatorAdminWebError.UNEXPECTED_ERROR.name());

        return Response
                .status(500)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(wsError)
                .build();
    }
}
