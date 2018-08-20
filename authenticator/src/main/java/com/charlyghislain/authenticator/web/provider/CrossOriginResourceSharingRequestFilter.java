package com.charlyghislain.authenticator.web.provider;

import com.charlyghislain.authenticator.ejb.configuration.ConfigConstants;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

/**
 * Respond with CORS headers to OPTIONS requests.
 *
 * @author cghislai
 */
@Provider
@PreMatching
@Priority(Priorities.HEADER_DECORATOR)
public class CrossOriginResourceSharingRequestFilter implements ContainerRequestFilter {

    @Inject
    @ConfigProperty(name = ConfigConstants.CORS_ALLOWED_ORIGINS)
    private List<String> corsAllowedOrigins;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        CorsUtils.filterCorsOptionsRequest(requestContext, corsAllowedOrigins);
    }
}
