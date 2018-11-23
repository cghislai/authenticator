package com.charlyghislain.authenticator.ejb.service;

import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.exception.EmailAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.exception.NameAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.filter.KeyFilter;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.ejb.configuration.ConfigConstants;
import com.charlyghislain.authenticator.ejb.util.RandomUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.security.enterprise.identitystore.PasswordHash;
import java.time.LocalDateTime;
import java.util.List;
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
    private ApplicationUpdateService applicationUpdateService;
    @Inject
    private PasswordHash passwordHash;

    @Inject
    @ConfigProperty(name = ConfigConstants.CREATE_DEFAULT_APPLICATION, defaultValue = "false")
    private boolean createDefaultApplication;
    @Inject
    @ConfigProperty(name = ConfigConstants.DEFAULT_APPLICATION_NAMES)
    private List<String> defaultApplicationNames;
    @Inject
    @ConfigProperty(name = ConfigConstants.DEFAULT_APPLICATION_ENDPOINT_URLS)
    private List<String> defaultApplicationEndpointUrls;

    public void createDefaultResources() {
        createDefaultKeyPair();
        createDefaultKeyPairForApplicationSecrets();
        createAdminUser();
        createDefaultApplication();
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
        rsaKeyPair.setCreationTime(LocalDateTime.now());
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
        rsaKeyPair.setCreationTime(LocalDateTime.now());
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
                .orElseGet(this::createAndLogRandomAdminPassword);
        String adminEmail = config.getValue(ConfigConstants.ADMIN_DEFAULT_EMAIL, String.class);
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
        } catch (@NonNull NameAlreadyExistsException | EmailAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
    }

    private void createDefaultApplication() {
        if (!createDefaultApplication) {
            return;
        }
        if (defaultApplicationNames.isEmpty() || defaultApplicationEndpointUrls.isEmpty()
                || defaultApplicationNames.size() != defaultApplicationEndpointUrls.size()) {
            throw new RuntimeException("Configuration error: must provide default application names and endpoint urls when creating default application");
        }
        int count = defaultApplicationNames.size();
        for (int i = 0; i < count; i++) {
            String name = defaultApplicationNames.get(i);
            String endpointUrl = defaultApplicationEndpointUrls.get(i);

            Application application = new Application();
            application.setActive(true);
            application.setName(name);
            application.setEndpointUrl(endpointUrl);
            application.setCreationTime(LocalDateTime.now());
            try {
                Application managedApplication = applicationUpdateService.createApplication(application);
                LOGGER.info("Created default application {}", name);
            } catch (NameAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void logAdminPassword(String plainPassword) {
        LOGGER.info("\n" +
                "=========== Your admin password ===============\n" +
                "\n" +
                plainPassword + "\n" +
                "\n" +
                "===============================================\n");
    }

    @NonNull
    private String createAndLogRandomAdminPassword() {
        String randomPassword = RandomUtils.generatePasswordString();
        logAdminPassword(randomPassword);
        return randomPassword;
    }
}
