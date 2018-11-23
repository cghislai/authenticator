package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.EmailVerificationToken;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.exception.UnauthorizedOperationException;
import com.charlyghislain.authenticator.domain.domain.filter.EmailVerificationTokenFilter;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.ejb.configuration.ConfigConstants;
import com.charlyghislain.authenticator.ejb.util.RandomUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Stateless
public class EmailVerificationUpdateService {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private EmailVerificationQueryService emailVerificationQueryService;
    @Inject
    private ApplicationEventService applicationEventService;
    @Inject
    @ConfigProperty(name = ConfigConstants.EMAIL_VERIFICATION_TOKEN_VALIDITY_DAYS)
    private Long emailVerificationTokenValidityDays;


    @RolesAllowed(AuthenticatorConstants.ROLE_APPLICATION)
    public EmailVerificationToken createNewVerificationToken(UserApplication userApplication) {
        this.removeAllUserApplicationTokens(userApplication);

        String token = RandomUtils.generatePasswordString();

        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setCreationTime(LocalDateTime.now());
        emailVerificationToken.setExpirationTime(this.getNewTokenExpirationTime());
        emailVerificationToken.setToken(token);
        emailVerificationToken.setUserApplication(userApplication);

        return this.saveToken(emailVerificationToken);
    }


    @RolesAllowed(AuthenticatorConstants.ROLE_APPLICATION)
    public void validateUserEmail(@NonNull UserApplication userApplication, String token) throws UnauthorizedOperationException {
        Application application = userApplication.getApplication();
        checkApplicationCanVerifyUserEmail(application);
        emailVerificationQueryService.findActiveEmailVerificationTokenForUserApplication(userApplication, token)
                .ifPresent(this::validateUserEmail);
    }


    public void removeAllUserTokens(User user) {
        EmailVerificationTokenFilter tokenFilter = new EmailVerificationTokenFilter();
        tokenFilter.setUser(user);
        emailVerificationQueryService.findAllEmailVerificationTokens(tokenFilter)
                .forEach(this::removeVerificationToken);
    }

    private void checkApplicationCanVerifyUserEmail(Application application) throws UnauthorizedOperationException {
        boolean canVerifyUserEmail = application.isCanVerifyUserEmail();
        if (!canVerifyUserEmail) {
            throw new UnauthorizedOperationException();
        }
    }

    public void removeAllUserApplicationTokens(UserApplication userApplication) {
        EmailVerificationTokenFilter tokenFilter = new EmailVerificationTokenFilter();
        tokenFilter.setUserApplication(userApplication);
        emailVerificationQueryService.findAllEmailVerificationTokens(tokenFilter)
                .forEach(this::removeVerificationToken);
    }

    private void validateUserEmail(EmailVerificationToken emailVerificationToken) {
        UserApplication userApplication = emailVerificationToken.getUserApplication();
        User user = userApplication.getUser();

        user.setEmailVerified(true);
        User managedUser = saveUser(user);

        this.applicationEventService.notifiyEmailVerified(managedUser);
        this.removeAllUserApplicationTokens(userApplication);
    }


    private LocalDateTime getNewTokenExpirationTime() {
        return LocalDateTime.now()
                .plus(emailVerificationTokenValidityDays, ChronoUnit.DAYS);
    }


    private void removeVerificationToken(EmailVerificationToken emailVerificationToken) {
        EmailVerificationToken managedToken = entityManager.merge(emailVerificationToken);
        entityManager.remove(managedToken);
    }

    private EmailVerificationToken saveToken(EmailVerificationToken emailVerificationToken) {
        return entityManager.merge(emailVerificationToken);
    }

    private User saveUser(User user) {
        return entityManager.merge(user);
    }
}
