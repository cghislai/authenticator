package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.PasswordResetToken;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.filter.PasswordResetTokenFilter;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.ejb.configuration.ConfigConstants;
import com.charlyghislain.authenticator.ejb.util.RandomUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Stateless
public class PasswordResetTokenUpdateService {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private PasswordResetTokenQueryService passwordResetTokenQueryService;
    @Inject
    @ConfigProperty(name = ConfigConstants.PASSWORD_RESET_TOKEN_VALIDITY_DAYS)
    private Long passwordResetTokenValidityDays;


    @RolesAllowed(AuthenticatorConstants.ROLE_APPLICATION)
    public PasswordResetToken createNewResetToken(User user) {
        this.removeAllUserTokens(user);

        String token = RandomUtils.generatePasswordString();

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setCreationTime(LocalDateTime.now());
        passwordResetToken.setExpirationTime(this.getNewTokenExpirationTime());
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);

        return this.saveToken(passwordResetToken);
    }

    public boolean validatePasswordResetToken(User user, String token) {
        return passwordResetTokenQueryService.findActivePasswordResetTokenForUser(user, token).isPresent();
    }

    public void removeAllUserTokens(User user) {
        PasswordResetTokenFilter tokenFilter = new PasswordResetTokenFilter();
        tokenFilter.setUser(user);
        passwordResetTokenQueryService.findAllPasswordResetTokens(tokenFilter)
                .forEach(this::removeResetToken);
    }

    private LocalDateTime getNewTokenExpirationTime() {
        return LocalDateTime.now()
                .plus(passwordResetTokenValidityDays, ChronoUnit.DAYS);
    }

    private void removeResetToken(PasswordResetToken passwordResetToken) {
        PasswordResetToken managedToken = entityManager.merge(passwordResetToken);
        entityManager.remove(managedToken);
    }

    private PasswordResetToken saveToken(PasswordResetToken passwordResetToken) {
        return entityManager.merge(passwordResetToken);
    }

}
