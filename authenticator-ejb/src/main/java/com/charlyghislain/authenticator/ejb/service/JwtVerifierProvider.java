package com.charlyghislain.authenticator.ejb.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.exception.AuthenticatorRuntimeException;
import com.charlyghislain.authenticator.domain.domain.exception.NoSigningKeyException;
import com.charlyghislain.authenticator.ejb.configuration.ConfigConstants;
import com.charlyghislain.authenticator.ejb.util.AuthenticatorManagedError;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
@AuthenticatorManagedError
public class JwtVerifierProvider {

    @EJB
    private RsaKeyPairQueryService rsaKeyPairQueryService;
    @Inject
    private RsaKeyPairConverterService rsaKeyPairConverterService;
    @Inject
    private SigningKKeyPairsProvider signingKKeyPairsProvider;

    @Inject
    @ConfigProperty(name = ConfigConstants.TOKEN_VALIDATION_LEEWAY_SECONDS)
    private Long tokenValidationLeewaySeconds;
    @Inject
    @ConfigProperty(name = ConfigConstants.TOKEN_ISSUER)
    private String tokenIssuer;

    private DefaultRsaKeyProvider defaultRsaKeyProvider;

    @PostConstruct
    public void init() {
        loadDefaultProvider();
    }

    private void loadDefaultProvider() {
        try {
            RsaKeyPair activeKeyPair = signingKKeyPairsProvider.getAuthenticatorSigningKey();
            this.defaultRsaKeyProvider = new DefaultRsaKeyProvider(rsaKeyPairQueryService, rsaKeyPairConverterService, activeKeyPair);
        } catch (NoSigningKeyException e) {
            throw new AuthenticatorRuntimeException("No signing key for the authenticator scope", e);
        }
    }

    @Produces
    @Dependent
    public JWTVerifier produceJwtVerifier() {
        Algorithm algorithm = Algorithm.RSA256(defaultRsaKeyProvider);
        return JWT.require(algorithm)
                .withIssuer(tokenIssuer)
                .acceptLeeway(tokenValidationLeewaySeconds)
                .build();
    }

}
