package com.charlyghislain.authenticator.ejb.service;

import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.exception.EmailAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.exception.NameAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.filter.KeyFilter;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.ejb.configuration.ConfigConstants;
import com.charlyghislain.authenticator.ejb.util.RandomUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.security.enterprise.identitystore.PasswordHash;
import java.util.Optional;


@Stateless
//@RunAs(AuthenticatorConstants.ROLE_ADMIN)
public class DefaultResourcesService {
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultResourcesService.class);

    @Inject
    private RsaKeyPairQueryService rsaKeyPairQueryService;
    @Inject
    private RsaKeyPairUpdateService rsaKeyPairUpdateService;
    @Inject
    private UserQueryService userQueryService;
    @Inject
    private UserUpdateService userUpdateService;
    @Inject
    private PasswordHash passwordHash;

    public void createDefaultResources() {
        createDefaultKeyPair();
        createDefaultKeyPairForApplicationSecrets();
        createAdminUser();
    }

    private void createDefaultKeyPair() {
        KeyFilter KeyFilter = new KeyFilter();
        KeyFilter.setActive(true);
        KeyFilter.setName(AuthenticatorConstants.DEFAULT_KEY_PAIR_NAME);
        Optional<RsaKeyPair> existingDefaultKeyPair = rsaKeyPairQueryService.findRsaKeyPair(KeyFilter);
        if (existingDefaultKeyPair.isPresent()) {
            return;
        }
        LOGGER.info("Creating default key pair");
        RsaKeyPair rsaKeyPair = new RsaKeyPair();
        rsaKeyPair.setActive(true);
        rsaKeyPair.setSigningKey(true);
        rsaKeyPair.setApplication(null);
        rsaKeyPair.setName(AuthenticatorConstants.DEFAULT_KEY_PAIR_NAME);
        try {
            rsaKeyPairUpdateService.createNewKey(rsaKeyPair);
        } catch (NameAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
    }


    private void createDefaultKeyPairForApplicationSecrets() {
        KeyFilter KeyFilter = new KeyFilter();
        KeyFilter.setActive(true);
        KeyFilter.setName(AuthenticatorConstants.DEFAULT_KEY_PAIR_FOR_SECRETS_NAME);
        Optional<RsaKeyPair> existingDefaultKeyPair = rsaKeyPairQueryService.findRsaKeyPair(KeyFilter);
        if (existingDefaultKeyPair.isPresent()) {
            return;
        }
        LOGGER.info("Creating default key pair to sign long-lived application secrets");
        RsaKeyPair rsaKeyPair = new RsaKeyPair();
        rsaKeyPair.setActive(true);
        rsaKeyPair.setSigningKey(true);
        rsaKeyPair.setApplication(null);
        rsaKeyPair.setName(AuthenticatorConstants.DEFAULT_KEY_PAIR_FOR_SECRETS_NAME);
        rsaKeyPair.setForApplicationSecrets(true);
        try {
            rsaKeyPairUpdateService.createNewKey(rsaKeyPair);
        } catch (NameAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
    }


    private void createAdminUser() {
        Optional<User> existingDefaultUser = userQueryService.findUserByName(AuthenticatorConstants.DEFAULT_ADMIN_USER_NAME);
        if (existingDefaultUser.isPresent()) {
            return;
        }
        LOGGER.info("Creating default admin user");

        Config config = ConfigProvider.getConfig();
        String adminPassword = config.getOptionalValue(ConfigConstants.ADMIN_DEFAULT_PASSWORD, String.class)
                .orElseGet(this::createNewRandomToken);
        String adminEmail = config.getValue(ConfigConstants.ADMIN_DEFAULT_EMAIL, String.class);
        this.logDefaultAdminPassword(adminPassword);
        String hashedPassword = passwordHash.generate(adminPassword.toCharArray());

        User adminUser = new User();
        adminUser.setActive(true);
        adminUser.setAdmin(true);
        adminUser.setName(AuthenticatorConstants.DEFAULT_ADMIN_USER_NAME);
        adminUser.setPassword(hashedPassword);
        adminUser.setPasswordExpired(false);
        adminUser.setEmail(adminEmail);
        adminUser.setEmailVerified(true);
        try {
            User managedAdminUser = userUpdateService.createUserNoChecks(adminUser);
        } catch (NameAlreadyExistsException | EmailAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
    }

    private void logDefaultAdminPassword(String plainPassword) {
        LOGGER.info("\n" +
                "=========== Your admin password ===============\n" +
                "\n" +
                plainPassword + "\n" +
                "\n" +
                "===============================================\n");
    }

    private String createNewRandomToken() {
        return RandomUtils.generatePasswordString();
    }
}
