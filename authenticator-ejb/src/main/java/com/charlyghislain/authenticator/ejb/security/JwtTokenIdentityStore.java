package com.charlyghislain.authenticator.ejb.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.ejb.service.JwtTokenService;

import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static javax.security.enterprise.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;

/**
 * While multiple authentication mechanisms cannot be comboined, the MP-JWT spec cannot be used
 * so the identityStore is not available.
 */
public class JwtTokenIdentityStore implements IdentityStore {

    @Inject
    private JwtTokenService tokenService;

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (!(credential instanceof SignedJWTCredential)) {
            return NOT_VALIDATED_RESULT;
        }
        SignedJWTCredential signedJWTCredential = (SignedJWTCredential) credential;
        try {
            DecodedJWT decodedJWT = tokenService.validateToken(signedJWTCredential.getSignedJWT());
            String principalName = decodedJWT.getClaim(AuthenticatorConstants.MP_JWT_USER_PRINCIPAL_CLAIM_NAME)
                    .asString();
            List<String> groupsNameList = decodedJWT.getClaim(AuthenticatorConstants.MP_JWT_GROUPS_CLAIM_NAME)
                    .asList(String.class);
            Set<String> groupNames = new HashSet<>(groupsNameList);
            return new CredentialValidationResult(principalName, groupNames);
        } catch (Exception e) {
            return INVALID_RESULT;
        }
    }
}
