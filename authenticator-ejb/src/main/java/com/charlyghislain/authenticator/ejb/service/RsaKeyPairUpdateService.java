package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.exception.CannotDeactivateSigningKey;
import com.charlyghislain.authenticator.domain.domain.exception.KeyIsLastActiveInScopeException;
import com.charlyghislain.authenticator.domain.domain.exception.KeyScopeChangedException;
import com.charlyghislain.authenticator.domain.domain.exception.NameAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.filter.KeyFilter;
import com.charlyghislain.authenticator.domain.domain.util.ResultList;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Stateless
//@RolesAllowed(AuthenticatorConstants.ROLE_ADMIN)
public class RsaKeyPairUpdateService {


    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private RsaKeyPairQueryService rsaKeyPairQueryService;
    @Inject
    private RsaKeyPairConverterService rsaKeyPairConverterService;

    @Inject
    private SigningKKeyPairsProvider signingKKeyPairsProvider;

    public RsaKeyPair createNewKey(RsaKeyPair newKey) throws NameAlreadyExistsException {
        String name = newKey.getName();
        boolean forApplicationSecrets = newKey.isForApplicationSecrets();
        Optional<Application> keyApplication = newKey.getApplication();
        boolean active = newKey.isActive();
        boolean signingKey = newKey.isSigningKey();

        checkDuplicateName(name);

        RsaKeyPair createdKey = rsaKeyPairConverterService.generateNewKeyPair();
        createdKey.setActive(active);
        createdKey.setName(name);
        createdKey.setForApplicationSecrets(forApplicationSecrets);
        createdKey.setApplication(keyApplication.orElse(null));

        RsaKeyPair managedKeyPair = saveRsaKeyPair(createdKey);
        if (signingKey && managedKeyPair.isActive()) {
            return this.setSigningKey(managedKeyPair);
        }
        return managedKeyPair;
    }

    public RsaKeyPair updateKey(@NotNull RsaKeyPair existingKey, @Valid RsaKeyPair keyUpdate) throws NameAlreadyExistsException, KeyScopeChangedException, KeyIsLastActiveInScopeException, CannotDeactivateSigningKey {
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

    public RsaKeyPair setSigningKey(RsaKeyPair keyPair) {
        KeyFilter keyFilter = new KeyFilter();
        keyFilter.setSigningKey(false);
        keyFilter.setForApplication(keyPair.isForApplicationSecrets());
        keyFilter.setApplication(keyPair.getApplication().orElse(null));
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
                                      Optional<Application> applicationScope) throws KeyScopeChangedException {
        if (forApplicationSecrets != existingKey.isForApplicationSecrets()) {
            throw new KeyScopeChangedException();
        }
        if (applicationScope.isPresent() != existingKey.getApplication().isPresent()) {
            throw new KeyScopeChangedException();
        }
        Boolean isSameScope = applicationScope
                .map(application -> application.equals(existingKey.getApplication().orElse(null)))
                .orElse(false);
        if (!isSameScope) {
            throw new KeyScopeChangedException();
        }
    }

    private void checkDeactivation(RsaKeyPair existingKey, boolean wasActive, boolean active,
                                   Optional<Application> applicationScope, boolean forApplicationSecrets) throws KeyIsLastActiveInScopeException, CannotDeactivateSigningKey {
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

    private void checkLastActiveKeyForApplication(RsaKeyPair existingKey, Optional<Application> applicationScope) throws KeyIsLastActiveInScopeException {
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

    private void checkLastActiveKeyForApplicationSecrets(RsaKeyPair existingKey) throws KeyIsLastActiveInScopeException {
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

    private void checkDuplicateName(RsaKeyPair existingKey, String name) throws NameAlreadyExistsException {
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

    private RsaKeyPair saveRsaKeyPair(RsaKeyPair rsaKeyPair) {
        RsaKeyPair managedRsaKeyPair = entityManager.merge(rsaKeyPair);

        signingKKeyPairsProvider.loadActiveKeys();
        return managedRsaKeyPair;
    }
}
