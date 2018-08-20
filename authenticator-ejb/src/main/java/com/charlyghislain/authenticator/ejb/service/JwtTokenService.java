package com.charlyghislain.authenticator.ejb.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.exception.AuthenticatorRuntimeException;
import com.charlyghislain.authenticator.domain.domain.exception.NoSigningKeyException;
import com.charlyghislain.authenticator.domain.domain.exception.UnauthorizedOperationException;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.domain.domain.util.CharacterSequences;
import com.charlyghislain.authenticator.ejb.configuration.ConfigConstants;
import com.charlyghislain.authenticator.ejb.util.RandomUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Stateless
public class JwtTokenService {

    @Inject
    private SigningKKeyPairsProvider signingKKeyPairsProvider;
    @Inject
    private RsaKeyPairConverterService rsaKeyPairConverterService;
    @Inject
    private AccessQueryService accessQueryService;
    @Inject
    private CallerQueryService callerQueryService;
    @Inject
    private JWTVerifier jwtVerifier;

    @Inject
    @ConfigProperty(name = ConfigConstants.TOKEN_ISSUER)
    private String tokenIssuer;
    @Inject
    @ConfigProperty(name = ConfigConstants.TOKEN_VALIDITY_MINUTES)
    private Long tokenValidityMinutes;
    @Inject
    @ConfigProperty(name = ConfigConstants.APPLICATION_SECRET_TOKEN_VALIDITY_DAYS)
    private Long applicationSecretTokenValidityDays;


    @RolesAllowed({AuthenticatorConstants.ROLE_USER})
    public String generateUserTokenForApplication(User user, Application application) throws NoSigningKeyException, UnauthorizedOperationException {
        RsaKeyPair activeApplicationKey = signingKKeyPairsProvider.getApplicationSigningKey(application);
        Algorithm algorithm = getAlgorithm(activeApplicationKey);

        String[] audience = this.getApplicationAudience(application);
        String[] userRoles = findUserApplicationRoles(application, user);

        Date expirationDate = this.getNewTokenExpirationDate();
        Date issuedDate = new Date();
        String tokenId = this.generateNewTokenId(application.getName());

        Long userId = user.getId();
        String userName = user.getName();

        String token = JWT.create()
                .withIssuer(tokenIssuer)
                .withKeyId(activeApplicationKey.getName())
                .withSubject(userName)
                .withExpiresAt(expirationDate)
                .withIssuedAt(issuedDate)
                .withJWTId(tokenId)
                .withAudience(audience)
                .withClaim(AuthenticatorConstants.MP_JWT_USER_PRINCIPAL_CLAIM_NAME, userName)
                .withArrayClaim(AuthenticatorConstants.MP_JWT_GROUPS_CLAIM_NAME, userRoles)
                .withClaim(AuthenticatorConstants.USER_ID_CLAIM_NAME, userId)
                .sign(algorithm);
        return token;
    }

    @RolesAllowed({AuthenticatorConstants.ROLE_USER})
    public String generateUserTokenForAuthenticator(User user) throws NoSigningKeyException {
        RsaKeyPair activeProviderKey = signingKKeyPairsProvider.getAuthenticatorSigningKey();
        Algorithm algorithm = getAlgorithm(activeProviderKey);

        String[] audience = this.getMyAudience();
        String[] userRoles = findUserProviderRoles(user);

        Date expirationDate = this.getNewTokenExpirationDate();
        Date issuedDate = new Date();
        String tokenId = this.generateNewTokenId("");

        Long userId = user.getId();
        String userName = user.getName();

        String token = JWT.create()
                .withIssuer(tokenIssuer)
                .withKeyId(activeProviderKey.getName())
                .withSubject(userName)
                .withExpiresAt(expirationDate)
                .withIssuedAt(issuedDate)
                .withJWTId(tokenId)
                .withAudience(audience)
                .withClaim(AuthenticatorConstants.MP_JWT_USER_PRINCIPAL_CLAIM_NAME, userName)
                .withArrayClaim(AuthenticatorConstants.MP_JWT_GROUPS_CLAIM_NAME, userRoles)
                .withClaim(AuthenticatorConstants.USER_ID_CLAIM_NAME, userId)
                .sign(algorithm);
        return token;
    }

    @RolesAllowed({AuthenticatorConstants.ROLE_ADMIN})
    public String generateApplicationTokenForAuthenticator(Application application) throws NoSigningKeyException {
        RsaKeyPair keyForApplicationSecrets = signingKKeyPairsProvider.getActiveAuthenticatorKeyForApplicationSecrets();
        Algorithm algorithm = getAlgorithm(keyForApplicationSecrets);
        Long applicationId = application.getId();

        String[] audience = this.getMyAudience();
        String[] applicationRoles = new String[]{
                AuthenticatorConstants.ROLE_APPLICATION,
                AuthenticatorConstants.ROLE_APPLICATION_RESTRICTION + applicationId,
                AuthenticatorConstants.ROLE_APP_MANAGEMENT,
                AuthenticatorConstants.ROLE_ACTIVE
        };

        Date expirationDate = this.getApplicationSecretTokenExpirationDate();
        Date issuedDate = new Date();
        String tokenId = this.generateNewTokenId("");
        String principalName = callerQueryService.createApplicationPrincipalName(application);

        String token = JWT.create()
                .withIssuer(tokenIssuer)
                .withKeyId(keyForApplicationSecrets.getName())
                .withSubject(principalName)
                .withExpiresAt(expirationDate)
                .withIssuedAt(issuedDate)
                .withJWTId(tokenId)
                .withAudience(audience)
                .withClaim(AuthenticatorConstants.MP_JWT_USER_PRINCIPAL_CLAIM_NAME, principalName)
                .withArrayClaim(AuthenticatorConstants.MP_JWT_GROUPS_CLAIM_NAME, applicationRoles)
                .withClaim(AuthenticatorConstants.APPLICATION_ID_CLAIM_NAME, applicationId)
                .sign(algorithm);
        return token;
    }

    // @RolesAllowed({AuthenticatorConstants.ROLE_ADMIN, AuthenticatorConstants.ROLE_APP}) // No RunAs for healthChecks
    @PermitAll
    public String generateAuthenticatorTokenForApplication(Application application) {
        RsaKeyPair activeApplicationKey = null;
        try {
            activeApplicationKey = signingKKeyPairsProvider.getApplicationSigningKey(application);
        } catch (NoSigningKeyException e) {
            throw new AuthenticatorRuntimeException("No signing key to create authenticator token for application " + application.getName(), e);
        }
        Algorithm algorithm = getAlgorithm(activeApplicationKey);

        String[] audience = this.getMyApplicationAudiences(application);
        String[] applicationRoles = new String[]{
                AuthenticatorConstants.ROLE_APPLICATION
        };

        Date expirationDate = this.getNewTokenExpirationDate();
        Date issuedDate = new Date();
        String applicationName = application.getName();
        String tokenId = this.generateNewTokenId(applicationName);

        String token = JWT.create()
                .withIssuer(tokenIssuer)
                .withKeyId(activeApplicationKey.getName())
                .withSubject(AuthenticatorConstants.AUTHENTICAOTOR_PRINCIPAL_NAME)
                .withExpiresAt(expirationDate)
                .withIssuedAt(issuedDate)
                .withJWTId(tokenId)
                .withAudience(audience)
                .withClaim(AuthenticatorConstants.MP_JWT_USER_PRINCIPAL_CLAIM_NAME, AuthenticatorConstants.AUTHENTICAOTOR_PRINCIPAL_NAME)
                .withArrayClaim(AuthenticatorConstants.MP_JWT_GROUPS_CLAIM_NAME, applicationRoles)
                .sign(algorithm);
        return token;
    }

    public DecodedJWT validateToken(String token) throws UnauthorizedOperationException {
        try {
            return jwtVerifier.verify(token);
        } catch (JWTVerificationException verificationException) {
            throw new UnauthorizedOperationException();
        }
    }

    private String[] findUserApplicationRoles(Application application, User user) throws UnauthorizedOperationException {
        Set<String> userApplicationScopedRoles = accessQueryService.findUserApplicationRoles(user, application);
        if (userApplicationScopedRoles.isEmpty()) {
            throw new UnauthorizedOperationException();
        }
        return userApplicationScopedRoles.toArray(new String[0]);
    }


    private String[] findUserProviderRoles(User user) {
        List<String> roleNames = new ArrayList<>();
        roleNames.add(AuthenticatorConstants.ROLE_USER);
        if (user.isActive()) {
            roleNames.add(AuthenticatorConstants.ROLE_ACTIVE);
        }
        if (user.isAdmin()) {
            roleNames.add(AuthenticatorConstants.ROLE_ADMIN);
        }
        return roleNames.toArray(new String[0]);
    }

    private String[] getApplicationAudience(Application application) {
        // TODO: use valid urls: application apps + comp auth app
        return new String[]{
        };
    }

    private String[] getMyAudience() {
        return new String[]{
                tokenIssuer
        };
    }

    private String[] getMyApplicationAudiences(Application application) {
        String applicationEndPoint = application.getEndpointUrl();
        return new String[]{
                tokenIssuer,
                applicationEndPoint,
        };
    }


    private String generateNewTokenId(String applicationName) {
        Random random = new Random();
        return applicationName + ":" + RandomUtils.generateString(random, CharacterSequences.ALPHANUMERIC, 16);
    }

    private Date getNewTokenExpirationDate() {
        Instant instant = ZonedDateTime.now()
                .plusMinutes(tokenValidityMinutes)
                .toInstant();
        Date expirationDate = Date.from(instant);
        return expirationDate;
    }

    private Date getApplicationSecretTokenExpirationDate() {
        Instant instant = ZonedDateTime.now()
                .plusDays(applicationSecretTokenValidityDays)
                .toInstant();
        Date expirationDate = Date.from(instant);
        return expirationDate;
    }

    private Algorithm getAlgorithm(RsaKeyPair activeApplicationKey) {
        RSAPrivateKey privateKey = rsaKeyPairConverterService.loadPrivateKey(activeApplicationKey);
        RSAPublicKey publicKey = rsaKeyPairConverterService.loadPublicKey(activeApplicationKey);
        return Algorithm.RSA256(publicKey, privateKey);
    }
}
