package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.exception.NoSigningKeyException;
import com.charlyghislain.authenticator.domain.domain.filter.ApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.filter.KeyFilter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

@ApplicationScoped
public class SigningKKeyPairsProvider {

    @Inject
    private RsaKeyPairQueryService rsaKeyPairQueryService;
    @Inject
    private ApplicationQueryService applicationQueryService;

    private final Map<Application, RsaKeyPair> applicationSigningKeys = new ConcurrentSkipListMap<>(
            Comparator.comparing(Application::getId)
    );

    private RsaKeyPair authenticatorSigningKey;
    private RsaKeyPair authenticatorSigningKeyForApplicationSecrets;

    @PostConstruct
    public void init() {
        loadActiveKeys();
    }

    public RsaKeyPair getApplicationSigningKey(Application application) throws NoSigningKeyException {
        RsaKeyPair applicationPair = applicationSigningKeys.computeIfAbsent(application, this::findApplicationSigningKeyPairNullable);
        return Optional.ofNullable(applicationPair)
                .orElseThrow(NoSigningKeyException::new);
    }

    public RsaKeyPair getAuthenticatorSigningKey() throws NoSigningKeyException {
        if (authenticatorSigningKey == null) {
            this.loadAuthenticatorKey();
        }
        return Optional.ofNullable(authenticatorSigningKey)
                .orElseThrow(NoSigningKeyException::new);
    }

    public RsaKeyPair getActiveAuthenticatorKeyForApplicationSecrets() throws NoSigningKeyException {
        if (authenticatorSigningKeyForApplicationSecrets == null) {
            this.loadAuthenticatorKeyForApplicationSecrets();
        }
        return Optional.ofNullable(authenticatorSigningKeyForApplicationSecrets)
                .orElseThrow(NoSigningKeyException::new);
    }

    public void loadActiveKeys() {
        loadAuthenticatorKey();
        loadAuthenticatorKeyForApplicationSecrets();
        loadApplicationsKeys();
    }

    private void loadAuthenticatorKeyForApplicationSecrets() {
        Optional<RsaKeyPair> authenticatorKeyForApplicationSecrets = findAuthenticatorSingingKeyForApplicationSecrets();
        this.authenticatorSigningKeyForApplicationSecrets = authenticatorKeyForApplicationSecrets.orElse(null);
    }

    private void loadAuthenticatorKey() {
        Optional<RsaKeyPair> providerKey = findAuthenticatorSigningKey();
        this.authenticatorSigningKey = providerKey.orElse(null);
    }

    private void loadApplicationsKeys() {
        ApplicationFilter ApplicationFilter = new ApplicationFilter();
        ApplicationFilter.setActive(true);
        applicationQueryService.findAllApplications(ApplicationFilter)
                .forEach(this::loadApplicationKey);
    }

    private void loadApplicationKey(Application application) {
        Optional<RsaKeyPair> applicationPrivateKey = findApplicationSigningKey(application);
        if (applicationPrivateKey.isPresent()) {
            applicationSigningKeys.put(application, applicationPrivateKey.get());
        } else {
            applicationSigningKeys.remove(application);
        }
    }

    private RsaKeyPair findApplicationSigningKeyPairNullable(Application application) {
        return findApplicationSigningKey(application).orElse(null);
    }

    private Optional<RsaKeyPair> findApplicationSigningKey(Application application) {
        KeyFilter keyPairFilter = new KeyFilter();
        keyPairFilter.setActive(true);
        keyPairFilter.setForApplicationSecrets(false);
        keyPairFilter.setForApplication(true);
        keyPairFilter.setApplication(application);
        keyPairFilter.setSigningKey(true);
        return rsaKeyPairQueryService.findRsaKeyPair(keyPairFilter);
    }

    private Optional<RsaKeyPair> findAuthenticatorSigningKey() {
        KeyFilter keyPairFilter = new KeyFilter();
        keyPairFilter.setActive(true);
        keyPairFilter.setForApplicationSecrets(false);
        keyPairFilter.setForApplication(false);
        keyPairFilter.setSigningKey(true);
        return rsaKeyPairQueryService.findRsaKeyPair(keyPairFilter);
    }

    private Optional<RsaKeyPair> findAuthenticatorSingingKeyForApplicationSecrets() {
        KeyFilter keyPairFilter = new KeyFilter();
        keyPairFilter.setActive(true);
        keyPairFilter.setForApplicationSecrets(true);
        keyPairFilter.setForApplication(false);
        keyPairFilter.setSigningKey(true);
        return rsaKeyPairQueryService.findRsaKeyPair(keyPairFilter);
    }
}
