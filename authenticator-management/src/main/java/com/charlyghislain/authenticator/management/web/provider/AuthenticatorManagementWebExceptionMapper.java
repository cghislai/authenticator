package com.charlyghislain.authenticator.management.web.provider;


import com.charlyghislain.authenticator.management.api.domain.WsError;
import com.charlyghislain.authenticator.management.api.error.AuthenticatorManagementWebException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class AuthenticatorManagementWebExceptionMapper implements ExceptionMapper<AuthenticatorManagementWebException> {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticatorManagementWebExceptionMapper.class);


    @Override
    public Response toResponse(AuthenticatorManagementWebException exception) {
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
