package com.charlyghislain.authenticator.ejb.service;

import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.exception.AuthenticatorRuntimeException;
import com.charlyghislain.authenticator.ejb.util.AuthenticatorManagedError;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@AuthenticatorManagedError
public class DefaultRsaKeyProvider implements RSAKeyProvider {

    private RsaKeyPairQueryService rsaKeyPairQueryService;
    private RsaKeyPairConverterService rsaKeyPairConverterService;
    private RsaKeyPair signingKeyPair;

    private RSAPrivateKey signingPrivateKey;

    DefaultRsaKeyProvider(RsaKeyPairQueryService rsaKeyPairQueryService, RsaKeyPairConverterService rsaKeyPairConverterService,
                          RsaKeyPair signingKeyPair) {
        this.rsaKeyPairQueryService = rsaKeyPairQueryService;
        this.rsaKeyPairConverterService = rsaKeyPairConverterService;
        this.signingKeyPair = signingKeyPair;
        this.signingPrivateKey = this.rsaKeyPairConverterService.loadPrivateKey(signingKeyPair);
    }

    @Override
    public RSAPublicKey getPublicKeyById(String keyId) {
        return rsaKeyPairQueryService.findActiveRsaKeyPairByName(keyId)
                .map(this.rsaKeyPairConverterService::loadPublicKey)
                .orElseThrow(() -> new AuthenticatorRuntimeException("Cannot find any key with id " + keyId));
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return signingPrivateKey;
    }

    @Override
    public String getPrivateKeyId() {
        return signingKeyPair.getName();
    }


}
