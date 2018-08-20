package com.charlyghislain.authenticator.web;


import com.charlyghislain.authenticator.api.InfoResource;
import com.charlyghislain.authenticator.api.domain.WsApplicationInfo;
import com.charlyghislain.authenticator.api.domain.WsAuthenticatorInfo;
import com.charlyghislain.authenticator.api.error.AuthenticatorWebError;
import com.charlyghislain.authenticator.api.error.AuthenticatorWebException;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.exception.NoSigningKeyException;
import com.charlyghislain.authenticator.ejb.service.ApplicationQueryService;
import com.charlyghislain.authenticator.ejb.service.RsaKeyPairConverterService;
import com.charlyghislain.authenticator.ejb.service.SigningKKeyPairsProvider;
import com.charlyghislain.authenticator.web.converter.WsApplicationInfoConverter;
import com.charlyghislain.authenticator.web.provider.WsAuthenticatorInfoProvider;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;

@PermitAll
public class InfoResourceController implements InfoResource {

    @Inject
    private RsaKeyPairConverterService rsaKeyPairConverterService;
    @Inject
    private SigningKKeyPairsProvider signingKKeyPairsProvider;
    @Inject
    private ApplicationQueryService applicationQueryService;
    @Inject
    private WsApplicationInfoConverter wsApplicationInfoConverter;
    @Inject
    private WsAuthenticatorInfoProvider wsAuthenticatorInfoProvider;

    @Override
    public WsAuthenticatorInfo getInfo() {
        return wsAuthenticatorInfoProvider.getAuthenticatorInfo();
    }

    @Override
    public String getActivePublicKey() {
        try {
            RsaKeyPair activeKeyPair = signingKKeyPairsProvider.getAuthenticatorSigningKey();
            return rsaKeyPairConverterService.encodePublicKeyToPem(activeKeyPair);
        } catch (NoSigningKeyException e) {
            throw new AuthenticatorWebException(AuthenticatorWebError.UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public WsApplicationInfo getApplicationInfo(String applicationName) {
        Application application = applicationQueryService.findActiveApplicationByName(applicationName)
                .orElseThrow(this::newApplicationNotFoundException);
        return wsApplicationInfoConverter.toWsApplicationInfo(application);
    }

    @Override
    public String getApplicationSigningPublicKey(String applicationName) {
        Application application = applicationQueryService.findActiveApplicationByName(applicationName)
                .orElseThrow(this::newApplicationNotFoundException);

        try {
            RsaKeyPair signingKey = signingKKeyPairsProvider.getApplicationSigningKey(application);
            return rsaKeyPairConverterService.encodePublicKeyToPem(signingKey);
        } catch (NoSigningKeyException e) {
            throw new AuthenticatorWebException(AuthenticatorWebError.UNEXPECTED_ERROR, e);
        }
    }


    private AuthenticatorWebException newApplicationNotFoundException() {
        return new AuthenticatorWebException(AuthenticatorWebError.APPLICATION_NOT_FOUND);
    }
}
