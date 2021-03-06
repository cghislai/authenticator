package com.charlyghislain.authenticator.web.security;


import com.charlyghislain.authenticator.ejb.security.SignedJWTCredential;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.security.enterprise.credential.Credential;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class HttpBearerCredentialProvider implements HttpCredentialProvider {

    private static final String AUtHORIZATION_HEADER_NAME = "Authorization";

    @Override
    public Optional<Credential> extractCredential(@NonNull HttpServletRequest servletRequest) {
        return Optional.ofNullable(servletRequest.getHeader(AUtHORIZATION_HEADER_NAME))
                .flatMap(this::parseAuthorizationHeader)
                .map(SignedJWTCredential::new);
    }

    private Optional<String> parseAuthorizationHeader(String headerValue) {
        boolean isBearer = headerValue.startsWith("Bearer");
        if (!isBearer) {
            return Optional.empty();
        }
        String tokenValue = headerValue.replaceFirst("^Bearer ", "");
        return Optional.of(tokenValue);
    }
}
