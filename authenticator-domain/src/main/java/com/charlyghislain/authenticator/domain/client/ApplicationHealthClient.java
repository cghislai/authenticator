package com.charlyghislain.authenticator.domain.client;


import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationAuthenticatorAuthorizationHealth;
import com.charlyghislain.authenticator.domain.domain.secondary.ApplicationHealth;

public interface ApplicationHealthClient {

    ApplicationAuthenticatorAuthorizationHealth checkAuthenticatorAuthorizationHealth(Application application, String authToken);

    ApplicationHealth checkApplicationHealth(Application application, String authToken);
}
