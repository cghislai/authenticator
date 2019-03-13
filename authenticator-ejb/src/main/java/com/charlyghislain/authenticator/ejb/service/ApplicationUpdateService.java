package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.RsaKeyPair;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.exception.AuthenticatorRuntimeException;
import com.charlyghislain.authenticator.domain.domain.exception.ExistingActiveApplicationUsersException;
import com.charlyghislain.authenticator.domain.domain.exception.InvalidKeyScopeException;
import com.charlyghislain.authenticator.domain.domain.exception.NameAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.filter.ApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.filter.KeyFilter;
import com.charlyghislain.authenticator.domain.domain.filter.UserApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.util.CharacterSequences;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import com.charlyghislain.authenticator.domain.domain.util.ResultList;
import com.charlyghislain.authenticator.ejb.util.AuthenticatorManagedError;
import com.charlyghislain.authenticator.ejb.util.RandomUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Random;

@Stateless
@AuthenticatorManagedError
public class ApplicationUpdateService {

    private final static Logger LOG = LoggerFactory.getLogger(ApplicationUpdateService.class);
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private ApplicationQueryService applicationQueryService;
    @Inject
    private RsaKeyPairUpdateService rsaKeyPairUpdateService;
    @Inject
    private UserQueryService userQueryService;
    @Inject
    private UserUpdateService userUpdateService;
    @Inject
    private RsaKeyPairQueryService keyPairQueryService;
    @Inject
    private RsaKeyPairUpdateService keyPairUpdateService;


    public Application createApplication(@NonNull @Valid Application newApplication) throws NameAlreadyExistsException {
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


    public Application updateApplication(@NonNull @NotNull Application existingApplication,
                                         @NonNull @Valid Application newApplication) throws NameAlreadyExistsException {
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

    public void removeApplication(@NonNull @NotNull Application existingApplication) throws ExistingActiveApplicationUsersException {
        // Only allow removing applications without any active users
        ResultList<UserApplication> userApplications = findActiveUserApplications(existingApplication);
        if (userApplications.hasResults()) {
            throw new ExistingActiveApplicationUsersException();
        }
        // Gather linked resources
        ResultList<UserApplication> allUserApplications = findAllUserApplications(existingApplication);
        ResultList<RsaKeyPair> allApplicationKeys = findAllApplicationKeys(existingApplication);

        LOG.info("Removing application {} as well as the {} users and the {} keys linked to it",
                existingApplication.getName(), allUserApplications.getTotalCount(), allApplicationKeys.getTotalCount());
        allUserApplications.forEach(userUpdateService::forgetApplicationUser);
        allApplicationKeys.forEach(this::removeKeyPair);
        deleteApplication(existingApplication);
    }

    private void removeKeyPair(RsaKeyPair rsaKeyPair) {
        try {
            keyPairUpdateService.removeKey(rsaKeyPair);
        } catch (InvalidKeyScopeException e) {
            throw new IllegalStateException(e);
        }
    }

    @NonNull
    private ResultList<UserApplication> findActiveUserApplications(@NonNull @NotNull Application existingApplication) {
        UserApplicationFilter activeApplicationUsers = new UserApplicationFilter();
        activeApplicationUsers.setActive(true);
        activeApplicationUsers.setApplication(existingApplication);
        return userQueryService.findUserApplications(activeApplicationUsers, new Pagination<>(0));
    }

    @NonNull
    private ResultList<UserApplication> findAllUserApplications(@NonNull @NotNull Application existingApplication) {
        UserApplicationFilter userApplicationFilter = new UserApplicationFilter();
        userApplicationFilter.setApplication(existingApplication);
        return userQueryService.findAllUserApplications(userApplicationFilter);
    }

    @NonNull
    private ResultList<RsaKeyPair> findAllApplicationKeys(@NonNull @NotNull Application existingApplication) {
        KeyFilter keyFilter = new KeyFilter();
        keyFilter.setApplication(existingApplication);
        return keyPairQueryService.findAllRsaKeyPairs(keyFilter);
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


    private void createNewApplicationKeyPair(@NonNull Application managedApplication) {
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


    private void deleteApplication(Application application) {
        Application managedApplication = entityManager.merge(application);
        entityManager.remove(managedApplication);
    }


}
