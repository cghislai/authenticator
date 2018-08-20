package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.exception.AuthenticatorRuntimeException;
import com.charlyghislain.authenticator.domain.domain.exception.NameAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.filter.ApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.util.CharacterSequences;
import com.charlyghislain.authenticator.ejb.util.RandomUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Random;

@Stateless
public class ApplicationUpdateService {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private ApplicationQueryService applicationQueryService;
    @Inject
    private RsaKeyPairUpdateService rsaKeyPairUpdateService;

    public Application createApplication(@Valid Application newApplication) throws NameAlreadyExistsException {
        Application application = new Application();
        updateExistingApplication(application, newApplication);

        Application managedApplication = saveApplication(application);

        RsaKeyPair rsaKeyPair = new RsaKeyPair();
        rsaKeyPair.setApplication(managedApplication);
        rsaKeyPair.setActive(true);
        rsaKeyPair.setSigningKey(true);

        String random = RandomUtils.generateString(new Random(), CharacterSequences.ALPHANUMERIC, 12);
        String keyName = managedApplication.getName() + "-" + random;
        keyName = keyName.substring(0, Math.min(254, keyName.length()));
        rsaKeyPair.setName(keyName);

        try {
            rsaKeyPairUpdateService.createNewKey(rsaKeyPair);
        } catch (NameAlreadyExistsException e) {
            throw new AuthenticatorRuntimeException("A key name " + keyName + " already exists");
        }
        return managedApplication;
    }

    public Application updateApplication(@NotNull Application existingApplication,
                                         @Valid Application newApplication) throws NameAlreadyExistsException {
        updateExistingApplication(existingApplication, newApplication);
        return saveApplication(existingApplication);
    }


    private void updateExistingApplication(Application existingApplication, Application applicationUpdate) throws NameAlreadyExistsException {
        String name = applicationUpdate.getName();
        String endpointUrl = applicationUpdate.getEndpointUrl();
        boolean active = applicationUpdate.isActive();

        checkDuplicateName(existingApplication, name);

        existingApplication.setActive(active);
        existingApplication.setName(name);
        existingApplication.setEndpointUrl(endpointUrl);
    }

    private void checkDuplicateName(Application existingApplication, String newName) throws NameAlreadyExistsException {
        ApplicationFilter filter = new ApplicationFilter();
        filter.setName(newName);
        boolean hasDuplicateName = applicationQueryService.findAllApplications(filter)
                .stream()
                .anyMatch(application -> !application.equals(existingApplication));
        if (hasDuplicateName) {
            throw new NameAlreadyExistsException();
        }
    }

    private Application saveApplication(Application application) {
        Application managedApplication = entityManager.merge(application);
        return managedApplication;
    }


}
