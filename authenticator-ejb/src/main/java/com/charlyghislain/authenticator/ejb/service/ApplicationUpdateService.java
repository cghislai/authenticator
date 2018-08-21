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
import java.time.LocalDateTime;
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
        String name = newApplication.getName();
        String endpointUrl = newApplication.getEndpointUrl();
        boolean active = newApplication.isActive();
        boolean addedUsersAreActive = newApplication.isAddedUsersAreActive();
        boolean canResetUserPassword = newApplication.isCanResetUserPassword();
        boolean canVerifyUserEmail = newApplication.isCanVerifyUserEmail();
        boolean existingUsersAreAddedOnTokenRequest = newApplication.isExistingUsersAreAddedOnTokenRequest();

        checkDuplicateName(name);

        Application application = new Application();
        application.setActive(active);
        application.setName(name);
        application.setEndpointUrl(endpointUrl);
        application.setCreationTime(LocalDateTime.now());
        application.setAddedUsersAreActive(addedUsersAreActive);
        application.setCanResetUserPassword(canResetUserPassword);
        application.setCanVerifyUserEmail(canVerifyUserEmail);
        application.setExistingUsersAreAddedOnTokenRequest(existingUsersAreAddedOnTokenRequest);

        Application managedApplication = saveApplication(application);

        createNewApplicationKeyPair(managedApplication);
        return managedApplication;
    }


    public Application updateApplication(@NotNull Application existingApplication,
                                         @Valid Application newApplication) throws NameAlreadyExistsException {
        String name = newApplication.getName();
        String endpointUrl = newApplication.getEndpointUrl();
        boolean active = newApplication.isActive();
        boolean addedUsersAreActive = newApplication.isAddedUsersAreActive();
        boolean canResetUserPassword = newApplication.isCanResetUserPassword();
        boolean canVerifyUserEmail = newApplication.isCanVerifyUserEmail();
        boolean existingUsersAreAddedOnTokenRequest = newApplication.isExistingUsersAreAddedOnTokenRequest();

        checkDuplicateName(existingApplication, name);

        existingApplication.setActive(active);
        existingApplication.setName(name);
        existingApplication.setEndpointUrl(endpointUrl);
        existingApplication.setAddedUsersAreActive(addedUsersAreActive);
        existingApplication.setCanResetUserPassword(canResetUserPassword);
        existingApplication.setCanVerifyUserEmail(canVerifyUserEmail);
        existingApplication.setExistingUsersAreAddedOnTokenRequest(existingUsersAreAddedOnTokenRequest);

        return saveApplication(existingApplication);
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

    private void checkDuplicateName(String name) throws NameAlreadyExistsException {
        boolean hasDuplicate = applicationQueryService.findActiveApplicationByName(name).isPresent();
        if (hasDuplicate) {
            throw new NameAlreadyExistsException();
        }
    }


    private void createNewApplicationKeyPair(Application managedApplication) {
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
    }

    private Application saveApplication(Application application) {
        Application managedApplication = entityManager.merge(application);
        return managedApplication;
    }


}
