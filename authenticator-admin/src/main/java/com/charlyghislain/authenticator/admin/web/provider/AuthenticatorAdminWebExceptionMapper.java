package com.charlyghislain.authenticator.admin.web.provider;


import com.charlyghislain.authenticator.admin.api.domain.WsError;
import com.charlyghislain.authenticator.admin.api.error.AuthenticatorAdminWebException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class AuthenticatorAdminWebExceptionMapper implements ExceptionMapper<AuthenticatorAdminWebException> {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticatorAdminWebExceptionMapper.class);


    @Override
    public Response toResponse(AuthenticatorAdminWebException exception) {
        String code = exception.getCode();
        int status = exception.getStatus();
        String description = exception.getDescription();

        if (status >= 500) {
            LOG.warn("Uncaught unexpected exception", exception);
        }

        WsError wsError = new WsError();
        wsError.setCode(code);
        wsError.setDescription(description);

        return Response
                .status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(wsError)
                .build();
    }
}
