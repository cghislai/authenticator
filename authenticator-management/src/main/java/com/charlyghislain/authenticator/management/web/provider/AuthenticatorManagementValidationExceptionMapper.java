package com.charlyghislain.authenticator.management.web.provider;


import com.charlyghislain.authenticator.management.api.domain.WsError;
import com.charlyghislain.authenticator.management.api.domain.WsValidationError;
import com.charlyghislain.authenticator.management.api.error.AuthenticatorManagementValidationException;
import com.charlyghislain.authenticator.management.api.domain.WsViolationError;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;


@Provider
public class AuthenticatorManagementValidationExceptionMapper implements ExceptionMapper<AuthenticatorManagementValidationException> {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticatorManagementValidationExceptionMapper.class);


    @Override
    public Response toResponse(@NonNull AuthenticatorManagementValidationException exception) {
        ArrayList<WsViolationError> violations = new ArrayList<>(exception.getWsViolationErrors());

        WsValidationError wsValidationError = new WsValidationError();
        wsValidationError.setCode(exception.getCode());
        wsValidationError.setViolations(violations);

        return Response
                .status(406)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(wsValidationError)
                .build();
    }
}
