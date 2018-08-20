package com.charlyghislain.authenticator.web.provider;


import com.charlyghislain.authenticator.api.domain.WsAuthenticatorInfo;
import com.charlyghislain.authenticator.ejb.configuration.ConfigConstants;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class WsAuthenticatorInfoProvider {


    @Inject
    @ConfigProperty(name = ConfigConstants.TOKEN_ISSUER)
    private String tokenIssuer;

    @Inject
    @ConfigProperty(name = ConfigConstants.AUTHENTICATOR_NAME)
    private String authenticatorName;

    @Inject
    @ConfigProperty(name = ConfigConstants.AUTHENTICATOR_URL)
    private String authenticatorUrl;


    public WsAuthenticatorInfo getAuthenticatorInfo() {
        WsAuthenticatorInfo wsAuthenticatorInfo = new WsAuthenticatorInfo();
        wsAuthenticatorInfo.setEndpointUrl(authenticatorUrl);
        wsAuthenticatorInfo.setName(authenticatorName);
        wsAuthenticatorInfo.setTokenIssuer(tokenIssuer);
        return wsAuthenticatorInfo;
    }
}
