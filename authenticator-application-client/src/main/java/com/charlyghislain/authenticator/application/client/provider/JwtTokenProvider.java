package com.charlyghislain.authenticator.application.client.provider;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

public class JwtTokenProvider implements ClientRequestFilter {

    private final String token;

    public JwtTokenProvider(String token) {
        this.token = token;
    }

    @Override
    public void filter(@NonNull ClientRequestContext requestContext) throws IOException {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        headers.add("Authorization", "Bearer " + token);
    }
}
