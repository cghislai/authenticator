package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.exception.*;
import com.charlyghislain.authenticator.domain.domain.filter.KeyFilter;
import com.charlyghislain.authenticator.domain.domain.util.ResultList;
import com.charlyghislain.authenticator.ejb.configuration.ConfigConstants;
import com.charlyghislain.authenticator.ejb.util.AuthenticatorManagedError;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;

@Stateless
//@RolesAllowed(AuthenticatorConstants.ROLE_ADMIN)
@AuthenticatorManagedError
public class RsaKeyPairUpdateService {


    private static final Logger LOG = LoggerFactory.getLogger(RsaKeyPairUpdateService.class);
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private RsaKeyPairQueryService rsaKeyPairQueryService;
    @Inject
    private RsaKeyPairConverterService rsaKeyPairConverterService;

    @Inject
    private SigningKKeyPairsProvider signingKKeyPairsProvider;

    @Inject
    @ConfigProperty(name = ConfigConstants.UNSAFE_FEATURES_ENABLED, defaultValue = "false")
    private boolean unsafeFeaturesEnabled;
    @Inject
    @ConfigProperty(name = ConfigConstants.UNSAFE_DETERMINISTIC_KEYS, defaultValue = "false")
    private boolean unsafeDeterministicKeys;
    @Inject
    @ConfigProperty(name = ConfigConstants.AUTHENTICATOR_NAME)
    private String authenticatorName;


    public RsaKeyPair createNewKey(@NonNull RsaKeyPair newKey) throws NameAlreadyExistsException {
        String name = newKey.getName();
        boolean forApplicationSecrets = newKey.isForApplicationSecrets();
        Optional<Application> keyApplication = newKey.getApplication();
        boolean active = newKey.isActive();
        boolean signingKey = newKey.isSigningKey();

        checkDuplicateName(name);

        RsaKeyPair createdKey = generateKeyPair(newKey);
        createdKey.setActive(active);
        createdKey.setName(name);
        createdKey.setForApplicationSecrets(forApplicationSecrets);
        createdKey.setApplication(keyApplication.orElse(null));
        createdKey.setCreationTime(LocalDateTime.now());

        RsaKeyPair managedKeyPair = saveRsaKeyPair(createdKey);
        if (signingKey && managedKeyPair.isActive()) {
            return this.setSigningKey(managedKeyPair);
        }
        return managedKeyPair;
    }

    public RsaKeyPair updateKey(@NonNull @NotNull RsaKeyPair existingKey, @NonNull RsaKeyPair keyUpdate) throws NameAlreadyExistsException, KeyScopeChangedException, KeyIsLastActiveInScopeException, CannotDeactivateSigningKey {
        String name = keyUpdate.getName();
        boolean forApplicationSecrets = keyUpdate.isForApplicationSecrets();
        Optional<Application> keyApplication = keyUpdate.getApplication();
        boolean active = keyUpdate.isActive();
        boolean signingKey = keyUpdate.isSigningKey();

        checkDuplicateName(existingKey, name);
        checkKeyScopeChanges(existingKey, forApplicationSecrets, keyApplication);
        checkDeactivation(existingKey, existingKey.isActive(), active, keyApplication, forApplicationSecrets);

        existingKey.setActive(active);
        existingKey.setName(name);
        existingKey.setForApplicationSecrets(forApplicationSecrets);
        existingKey.setApplication(keyApplication.orElse(null));
        RsaKeyPair managedKeyPair = saveRsaKeyPair(existingKey);

        if (signingKey && managedKeyPair.isActive()) {
            return this.setSigningKey(managedKeyPair);
        }
        return managedKeyPair;
    }

    public void removeKey(@NonNull @NotNull RsaKeyPair rsaKeyPair) throws InvalidKeyScopeException {
        Application application = rsaKeyPair.getApplication()
                .orElseThrow(() -> new InvalidKeyScopeException("Only application keys can be removed"));
        signingKKeyPairsProvider.clearApplicationKey(application);
        
        @NotNull RsaKeyPair managedKey = entityManager.merge(rsaKeyPair);
        entityManager.remove(managedKey);
    }

    public RsaKeyPair setSigningKey(@NonNull RsaKeyPair keyPair) {
        KeyFilter keyFilter = new KeyFilter();
        keyFilter.setSigningKey(true);
        keyFilter.setForApplicationSecrets(keyPair.isForApplicationSecrets());
        keyPair.getApplication().ifPresent(keyFilter::setApplication);
        ResultList<RsaKeyPair> previousSigningKeys = rsaKeyPairQueryService.findAllRsaKeyPairs(keyFilter);

        keyPair.setSigningKey(true);
        RsaKeyPair managedSigningKey = saveRsaKeyPair(keyPair);

        previousSigningKeys
                .stream()
                .filter(key -> !key.equals(managedSigningKey))
                .forEach(this::unsetSigningKey);
        return managedSigningKey;
    }

    private void unsetSigningKey(RsaKeyPair rsaKeyPair) {
        rsaKeyPair.setSigningKey(false);
        saveRsaKeyPair(rsaKeyPair);
    }

    private void checkKeyScopeChanges(RsaKeyPair existingKey, boolean forApplicationSecrets,
                                      @NonNull Optional<Application> applicationScope) throws KeyScopeChangedException {
        if (forApplicationSecrets != existingKey.isForApplicationSecrets()) {
            throw new KeyScopeChangedException();
        }
        if (applicationScope.isPresent() != existingKey.getApplication().isPresent()) {
            throw new KeyScopeChangedException();
        }
        Boolean isSameScope = applicationScope
                .map(application -> application.equals(existingKey.getApplication().orElse(null)))
                .orElse(true); // Both empty
        if (!isSameScope) {
            throw new KeyScopeChangedException();
        }
    }

    private void checkDeactivation(@NonNull RsaKeyPair existingKey, boolean wasActive, boolean active,
                                   @NonNull Optional<Application> applicationScope, boolean forApplicationSecrets) throws KeyIsLastActiveInScopeException, CannotDeactivateSigningKey {
        if (active || !wasActive) {
            return;
        }
        if (existingKey.isSigningKey()) {
            throw new CannotDeactivateSigningKey();
        }
        checkLastActiveKeyForApplication(existingKey, applicationScope);
        if (forApplicationSecrets) {
            checkLastActiveKeyForApplicationSecrets(existingKey);
        }
    }

    private void checkLastActiveKeyForApplication(@NonNull RsaKeyPair existingKey, Optional<Application> applicationScope) throws KeyIsLastActiveInScopeException {
        // Cannot deactivate the last key for a application scope
        KeyFilter keyPairFilter = new KeyFilter();
        keyPairFilter.setActive(true);
        keyPairFilter.setForApplicationSecrets(false);
        if (applicationScope.isPresent()) {
            keyPairFilter.setForApplication(true);
            keyPairFilter.setApplication(applicationScope.get());
        } else {
            keyPairFilter.setForApplication(false);
        }
        boolean hasOtherKeyForApplicationScope = rsaKeyPairQueryService.findAllRsaKeyPairs(keyPairFilter)
                .stream()
                .anyMatch(key -> !existingKey.equals(key));
        if (!hasOtherKeyForApplicationScope) {
            throw new KeyIsLastActiveInScopeException();
        }
    }

    private void checkLastActiveKeyForApplicationSecrets(@NonNull RsaKeyPair existingKey) throws KeyIsLastActiveInScopeException {
        // Cannot deactivate the last key to sign application secrets
        KeyFilter keyPairFilter = new KeyFilter();
        keyPairFilter.setActive(true);
        keyPairFilter.setForApplicationSecrets(true);
        boolean hasOtherKeyForApplicationSecrets = rsaKeyPairQueryService.findAllRsaKeyPairs(keyPairFilter)
                .stream()
                .anyMatch(key -> !existingKey.equals(key));
        if (!hasOtherKeyForApplicationSecrets) {
            throw new KeyIsLastActiveInScopeException();
        }
    }

    private void checkDuplicateName(@NonNull RsaKeyPair existingKey, String name) throws NameAlreadyExistsException {
        KeyFilter keyPairFilter = new KeyFilter();
        keyPairFilter.setName(name);
        boolean hasDuplicateName = rsaKeyPairQueryService.findAllRsaKeyPairs(keyPairFilter)
                .stream()
                .anyMatch(key -> !existingKey.equals(key));
        if (hasDuplicateName) {
            throw new NameAlreadyExistsException();
        }
    }

    private void checkDuplicateName(String name) throws NameAlreadyExistsException {
        KeyFilter keyPairFilter = new KeyFilter();
        keyPairFilter.setName(name);
        boolean hasDuplicateName = rsaKeyPairQueryService.findRsaKeyPair(keyPairFilter).isPresent();
        if (hasDuplicateName) {
            throw new NameAlreadyExistsException();
        }
    }


    private RsaKeyPair generateKeyPair(@NonNull RsaKeyPair keyPair) {
        if (unsafeFeaturesEnabled && unsafeDeterministicKeys) {
            if (keyPair.getApplication().isPresent()) {
                return this.generateUnsafeApplicationKeyPair(keyPair.getApplication().get());
            }
            if (keyPair.isForApplicationSecrets()) {
                return this.generateUnsafeApplicationSecretsKeyPair();
            } else {
                return this.generateUnsafeAuthenticatorKeyPair();
            }
        }
        return rsaKeyPairConverterService.generateNewKeyPair();
    }


    private RsaKeyPair generateUnsafeAuthenticatorKeyPair() {
        String seedString = authenticatorName;
        LOG.warn("Generating authenticator key pair using {} as a seed", seedString);
        return generateUnsafeKeyFromSeed(seedString);
    }

    private RsaKeyPair generateUnsafeApplicationSecretsKeyPair() {
        String seedString = authenticatorName + "-app-secrets";
        LOG.warn("Generating key pair for application secrets using {} as a seed", seedString);
        return generateUnsafeKeyFromSeed(seedString);
    }


    private RsaKeyPair generateUnsafeApplicationKeyPair(Application application) {
        String seedString = authenticatorName + "-app";
        LOG.warn("Generating key pair for application {} using {} name as a seed", application.getName(), seedString);
        return generateUnsafeKeyFromSeed(seedString);
    }

    private RsaKeyPair generateUnsafeKeyFromSeed(String seedString) {
        try {
            byte[] keySeed = seedString.getBytes("UTF-8");
            RsaKeyPair rsaKeyPair = rsaKeyPairConverterService.generateNewKeyPair(keySeed);
            String publicKeyPem = rsaKeyPairConverterService.encodePublicKeyToPem(rsaKeyPair);
            LOG.info("Generated public key:");
            LOG.info(publicKeyPem);
            return rsaKeyPair;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private RsaKeyPair saveRsaKeyPair(@Valid RsaKeyPair rsaKeyPair) {
        RsaKeyPair managedRsaKeyPair = entityManager.merge(rsaKeyPair);

        signingKKeyPairsProvider.loadActiveKeys();
        return managedRsaKeyPair;
    }
}
