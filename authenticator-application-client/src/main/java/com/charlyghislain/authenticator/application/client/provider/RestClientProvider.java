package com.charlyghislain.authenticator.application.client.provider;

import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.exception.AuthenticatorRuntimeException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@ApplicationScoped
public class RestClientProvider {

    public <T> T createResource(String authToken, @NonNull Application application, Class<T> resourceType) {
        URL endpointURL = getApplicationEndpointUrl(application);
        RestClientBuilder clientBuilder = RestClientBuilder.newBuilder()
                .baseUrl(endpointURL)
                .register(ForwardAuthErrorsResponseExceptionMapper.class);

        Optional.ofNullable(authToken)
                .map(JwtTokenProvider::new)
                .ifPresent(clientBuilder::register);

        return clientBuilder.build(resourceType);
    }


    private URL getApplicationEndpointUrl(Application application) {
        String endpointUrl = application.getEndpointUrl();
        try {
            return new URL(endpointUrl);
        } catch (MalformedURLException e) {
            throw new AuthenticatorRuntimeException("Invalid url: " + endpointUrl, e);
        }
    }
}
