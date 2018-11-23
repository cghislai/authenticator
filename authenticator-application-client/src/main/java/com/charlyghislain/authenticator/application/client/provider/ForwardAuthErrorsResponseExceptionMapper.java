package com.charlyghislain.authenticator.application.client.provider;


import com.charlyghislain.authenticator.application.api.domain.WsApplicationError;
import com.charlyghislain.authenticator.domain.domain.exception.ApplicationClientError;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class ForwardAuthErrorsResponseExceptionMapper implements ResponseExceptionMapper<ApplicationClientError> {

    @NonNull
    @Override
    public ApplicationClientError toThrowable(@NonNull Response response) {
        int statusCode = response.getStatus();
        WsApplicationError errorEntity = (WsApplicationError) response.getEntity();
        String code = errorEntity.getCode();
        String description = errorEntity.getDescription();

        return new ApplicationClientError(statusCode, code, description);
    }

    @Override
    public boolean handles(int status, @NonNull MultivaluedMap<String, Object> headers) {
        return headers.keySet().contains(WsApplicationError.APPLICATION_ERROR_HEADER_NAME);
    }
}
