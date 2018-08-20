package com.charlyghislain.authenticator.management.web.provider;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class CorsUtils {

    public static final String CORS_ALLOWED_ORIGINS_WILDCARD = "*";

    public static boolean isOriginValid(String origin, List<String> allowedOrigins) {
        if (allowedOrigins == null || allowedOrigins.isEmpty()) {
            return false;
        }
        boolean isAllowed = allowedOrigins.stream()
                .map(allowedOrigin -> CorsUtils.isOriginAllowed(origin, allowedOrigin))
                .filter(allowed -> allowed)
                .findFirst()
                .orElse(false);
        return isAllowed;
    }

    public static void filterCorsOptionsRequest(ContainerRequestContext requestContext, List<String> allowedOriging) {
        String method = requestContext.getMethod();
        if (!HttpMethod.OPTIONS.equals(method)) {
            return;
        }
        String origin = requestContext.getHeaderString("Origin");
        boolean originValid = CorsUtils.isOriginValid(origin, allowedOriging);
        if (originValid) {
            // Origin & credential headers are appended by a response filter which will be called as well.
            Response okResponse = Response.ok()
                    .header("Access-Control-Allow-Methods", "OPTIONS, GET, POST, PUT, DELETE")
                    .header("Access-Control-Allow-Headers", "content-type, accept, accept-charset, authorization, X-Requested-With")
                    .header("Access-Control-Expose-Headers", "accept-ranges, content-encoding, content-length")
                    .build();
            requestContext.abortWith(okResponse);
        }
    }

    private static boolean isOriginAllowed(String origin, String allowedOrigin) {
        if (allowedOrigin == null || origin == null) {
            return false;
        }
        if (allowedOrigin.equals(CORS_ALLOWED_ORIGINS_WILDCARD)) {
            return true;
        }
        try {
            URI allowedUri = new URI(allowedOrigin);
            URI originUri = new URI(origin);
            URI relativized = allowedUri.relativize(originUri);

            return !relativized.equals(originUri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
