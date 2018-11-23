package com.charlyghislain.authenticator.web.provider;


import com.charlyghislain.authenticator.ejb.configuration.ConfigConstants;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * Append CORS headers to responses.
 *
 * @author cghislai
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class CrossOriginResourceSharingResponseFilter implements ContainerResponseFilter {

    @Inject
    private HttpServletRequest httpServletRequest;

    @Inject
    @ConfigProperty(name = ConfigConstants.CORS_ALLOWED_ORIGINS)
    private List<String> corsAllowedOrigins;

    @Override
    public void filter(@NonNull ContainerRequestContext requestContext, @NonNull ContainerResponseContext response) {
        String origin = requestContext.getHeaderString("Origin");
        boolean originValid = CorsUtils.isOriginValid(origin, corsAllowedOrigins);
        if (originValid) {
            MultivaluedMap<String, Object> headerMap = response.getHeaders();
            headerMap.putSingle("Access-Control-Allow-Origin", origin);
            headerMap.putSingle("Access-Control-Allow-Credentials", "true");
        }
    }

    public void filter(@NonNull HttpServletResponse servletResponse) {
        String origin = httpServletRequest.getHeader("Origin");
        boolean originValid = CorsUtils.isOriginValid(origin, corsAllowedOrigins);
        if (originValid) {
            servletResponse.addHeader("Access-Control-Allow-Origin", origin);
            servletResponse.addHeader("Access-Control-Allow-Credentials", "true");
        }
    }


    public void filter(@NonNull Response response) {
        String origin = httpServletRequest.getHeader("Origin");
        boolean originValid = CorsUtils.isOriginValid(origin, corsAllowedOrigins);
        if (originValid) {
            MultivaluedMap<String, Object> headerMap = response.getHeaders();
            headerMap.putSingle("Access-Control-Allow-Origin", origin);
            headerMap.putSingle("Access-Control-Allow-Credentials", "true");
        }
    }

}
