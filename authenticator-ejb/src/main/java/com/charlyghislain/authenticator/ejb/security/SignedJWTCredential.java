package com.charlyghislain.authenticator.ejb.security;

import javax.security.enterprise.credential.Credential;

public class SignedJWTCredential implements Credential {

    private final String signedJWT;

    public SignedJWTCredential(String signedJWT) {
        this.signedJWT = signedJWT;
    }

    public String getSignedJWT() {
        return signedJWT;
    }
}