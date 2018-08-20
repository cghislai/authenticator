package com.charlyghislain.authenticator.ejb.service;


import com.charlyghislain.authenticator.domain.domain.Application;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.UserApplication;
import com.charlyghislain.authenticator.domain.domain.exception.AdminCannotLockHerselfOutException;
import com.charlyghislain.authenticator.domain.domain.exception.AuthenticatorRuntimeException;
import com.charlyghislain.authenticator.domain.domain.exception.EmailAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.exception.NameAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.exception.UnauthorizedOperationException;
import com.charlyghislain.authenticator.domain.domain.filter.UserApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.filter.UserFilter;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.domain.domain.validation.ValidEmail;
import com.charlyghislain.authenticator.domain.domain.validation.ValidIdentifierName;
import com.charlyghislain.authenticator.domain.domain.validation.ValidPassword;
import com.charlyghislain.authenticator.ejb.util.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Stateless
public class UserUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(UserUpdateService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UserQueryService userQueryService;
    @Inject
    private CallerQueryService callerQueryService;
    @Inject
    private PasswordHash passwordHash;
    @Inject
    private ApplicationEventService applicationEventService;

    //    @DenyAll
    public User createUserNoChecks(@Valid User user) throws NameAlreadyExistsException, EmailAlreadyExistsException {
        String name = user.getName();
        String email = user.getEmail();

        checkDuplicateName(name);
        checkDuplicateEmail(email);

        return saveUser(user);
    }

    @RolesAllowed({AuthenticatorConstants.ROLE_ADMIN})
    public User createUser(@Valid User newUser) throws NameAlreadyExistsException, EmailAlreadyExistsException {
        String name = newUser.getName();
        String email = newUser.getEmail();
        boolean admin = newUser.isAdmin();
        boolean active = newUser.isActive();

        checkDuplicateName(name);
        checkDuplicateEmail(email);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setActive(active);
        user.setAdmin(admin);
        user.setEmailVerified(false);
        user.setPasswordExpired(true);

        return saveUser(user);
    }

    @RolesAllowed({AuthenticatorConstants.ROLE_ADMIN})
    public User updateUser(@NotNull User existingUser, @Valid User userUpdate) throws NameAlreadyExistsException, EmailAlreadyExistsException, AdminCannotLockHerselfOutException {
        String name = userUpdate.getName();
        String email = userUpdate.getEmail();
        boolean admin = userUpdate.isAdmin();
        boolean active = userUpdate.isActive();


        checkDuplicateName(existingUser, name);
        checkDuplicateEmail(existingUser, email);
        checkActiveStateChange(active, existingUser);
        checkAdminStateChange(admin, existingUser);

        boolean emailChanged = !existingUser.getEmail().equals(email);

        existingUser.setName(name);
        existingUser.setEmail(email);
        existingUser.setActive(active);
        existingUser.setAdmin(admin);
        if (emailChanged) {
            existingUser.setEmailVerified(false);
        }
        return saveUser(existingUser);
    }

    @RolesAllowed({AuthenticatorConstants.ROLE_APPLICATION, AuthenticatorConstants.ROLE_ADMIN})
    public UserApplication createApplicationUser(@NotNull Application application, @Valid User newUser) throws NameAlreadyExistsException, EmailAlreadyExistsException {
        String name = newUser.getName();
        String email = newUser.getEmail();

        checkDuplicateName(name);
        checkDuplicateEmail(email);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setActive(true);
        user.setAdmin(false);
        user.setEmailVerified(false);
        user.setPasswordExpired(true);

        User savedUser = saveUser(user);

        UserApplication userApplication = new UserApplication();
        userApplication.setUser(savedUser);
        userApplication.setApplication(application);
        userApplication.setActive(false);
        UserApplication managedUserApplication = saveUserApplication(userApplication);

        applicationEventService.onUserAdded(managedUserApplication);

        return managedUserApplication;
    }

    @RolesAllowed({AuthenticatorConstants.ROLE_ADMIN, AuthenticatorConstants.ROLE_APPLICATION})
    public UserApplication updateApplicationUser(@NotNull UserApplication userApplication, @Valid User userUpdate) {
        boolean active = userUpdate.isActive();

        userApplication.setActive(active);

        return saveUserApplication(userApplication);
    }


    @RolesAllowed({AuthenticatorConstants.ROLE_ADMIN, AuthenticatorConstants.ROLE_APPLICATION})
    public void forgetApplicationUser(@NotNull UserApplication userApplication) {
        Application application = userApplication.getApplication();
        User user = userApplication.getUser();
        Long userId = user.getId();

        removeUserApplication(userApplication);

        applicationEventService.notifyUserRemoved(application, userId);

        // TODO: remove user as well?

        boolean hasNoMoreApplications = checkHasNoMoreApplications(user);
        if (hasNoMoreApplications) {
            removeUser(user);
        }
    }

    @RolesAllowed(AuthenticatorConstants.ROLE_USER)
    public UserApplication linkUserToApplication(@NotNull User user, @NotNull Application application) throws UnauthorizedOperationException {
        checkIsCallerUser(user);
        checkIsActive(user);
        checkDoestHaveApplication(user, application);

        UserApplication userApplication = new UserApplication();
        userApplication.setUser(user);
        userApplication.setApplication(application);
        UserApplication savedUserApplication = saveUserApplication(userApplication);

        applicationEventService.onUserAdded(savedUserApplication);
        return savedUserApplication;
    }

    @RolesAllowed(AuthenticatorConstants.ROLE_USER)
    public void unlinkUserToApplication(@NotNull UserApplication userApplication) throws UnauthorizedOperationException {
        User user = userApplication.getUser();
        Application application = userApplication.getApplication();
        Long userId = user.getId();

        checkIsActive(user);
        checkIsCallerUser(user);

        removeUserApplication(userApplication);

        applicationEventService.notifyUserRemoved(application, userId);

        boolean hasNoMoreApplications = checkHasNoMoreApplications(user);
        if (hasNoMoreApplications) {
            removeUser(user);
        }
    }


    @RolesAllowed(AuthenticatorConstants.ROLE_USER)
    public User updateUserName(@NotNull User user, @ValidIdentifierName String name) throws UnauthorizedOperationException, NameAlreadyExistsException {
        checkIsCallerUser(user);
        checkIsActive(user);
        checkDuplicateName(user, name);

        user.setName(name);
        return saveUser(user);
    }

    @RolesAllowed(AuthenticatorConstants.ROLE_USER)
    public User updateUserEmail(@NotNull User user, @ValidEmail String email) throws UnauthorizedOperationException, EmailAlreadyExistsException {
        checkIsCallerUser(user);
        checkIsActive(user);
        checkDuplicateEmail(user, email);

        boolean emailChanged = !user.getEmail().equals(email);

        user.setEmail(email);
        if (emailChanged) {
            user.setEmailVerified(false);
        }
        return saveUser(user);
    }

    @RolesAllowed(AuthenticatorConstants.ROLE_USER)
    public User updateUserPassword(@NotNull User user, @ValidPassword String plainPassword) throws UnauthorizedOperationException {
        checkIsCallerUser(user);
        checkIsActive(user);

        String hashedPassword = passwordHash.generate(plainPassword.toCharArray());

        user.setPassword(hashedPassword);
        user.setPasswordExpired(false);
        return saveUser(user);
    }

    @RolesAllowed(AuthenticatorConstants.ROLE_ADMIN)
    public User setUserPassword(@NotNull User user, @ValidPassword String plainPassword) {
        String hashedPassword = passwordHash.generate(plainPassword.toCharArray());

        user.setPassword(hashedPassword);
        user.setPasswordExpired(false);
        return saveUser(user);
    }

    private void checkAdminStateChange(boolean admin, User user) throws AdminCannotLockHerselfOutException {
        if (admin == user.isAdmin()) {
            return;
        }
        // Prevent switching own state
        boolean isLockingHerselfOut = isCallerUser(user);
        if (isLockingHerselfOut) {
            throw new AdminCannotLockHerselfOutException();
        }
    }

    private void checkActiveStateChange(boolean active, User user) throws AdminCannotLockHerselfOutException {
        if (active == user.isActive()) {
            return;
        }
        // Prevent switching own state
        boolean isLockingHerselfOut = isCallerUser(user);
        if (isLockingHerselfOut) {
            throw new AdminCannotLockHerselfOutException();
        }
    }

    private void checkDuplicateName(User existingUser, String newName) throws NameAlreadyExistsException {
        if (existingUser.getName().equals(newName)) {
            return;
        }
        UserFilter filter = new UserFilter();
        filter.setName(newName);
        boolean hasDuplicateName = userQueryService.findAllUsers(filter)
                .stream()
                .anyMatch(user -> !user.equals(existingUser));
        if (hasDuplicateName) {
            throw new NameAlreadyExistsException();
        }
    }

    private void checkDuplicateName(String name) throws NameAlreadyExistsException {
        UserFilter filter = new UserFilter();
        filter.setName(name);
        Optional<User> duplicate = userQueryService.findUser(filter);
        if (duplicate.isPresent()) {
            throw new NameAlreadyExistsException();
        }
    }

    private void checkDuplicateEmail(User existingUser, String newEmail) throws EmailAlreadyExistsException {
        if (existingUser.getEmail().equals(newEmail)) {
            return;
        }
        UserFilter filter = new UserFilter();
        filter.setEmail(newEmail);
        boolean hasDuplicateName = userQueryService.findAllUsers(filter)
                .stream()
                .anyMatch(user -> !user.equals(existingUser));
        if (hasDuplicateName) {
            throw new EmailAlreadyExistsException();
        }
    }

    private void checkDuplicateEmail(String newEmail) throws EmailAlreadyExistsException {
        UserFilter filter = new UserFilter();
        filter.setEmail(newEmail);
        boolean hasDuplicate = userQueryService.findUser(filter).isPresent();
        if (hasDuplicate) {
            throw new EmailAlreadyExistsException();
        }
    }

    private void checkUserHasPassword(User user) {
        String password = user.getPassword();
        if (password != null && !password.isEmpty()) {
            return;
        }

        LOG.info("Generating random password for user [{}], id [{}[", user.getName(), user.getId());
        String passwordString = RandomUtils.generatePasswordString();
        String hashedPassword = passwordHash.generate(passwordString.toCharArray());
        user.setPassword(hashedPassword);
        user.setPasswordExpired(true);
    }


    private void checkIsCallerUser(User user) throws UnauthorizedOperationException {
        if (isCallerUser(user)) {
            return;
        }
        throw new UnauthorizedOperationException();
    }


    private void checkDoestHaveApplication(User user, Application application) throws UnauthorizedOperationException {
        boolean hasApplication = userQueryService.findUserApplication(user, application).isPresent();
        if (hasApplication) {
            throw new UnauthorizedOperationException();
        }
    }

    private void checkIsActive(User user) throws UnauthorizedOperationException {
        boolean active = user.isActive();
        if (!active) {
            throw new UnauthorizedOperationException();
        }
    }

    private boolean checkHasNoMoreApplications(User user) {
        UserApplicationFilter userApplicationFilter = new UserApplicationFilter();
        userApplicationFilter.getUserFilter().setId(user.getId());
        return !userQueryService.findUserApplication(userApplicationFilter)
                .isPresent();
    }

    private boolean isCallerUser(User user) {
        return callerQueryService.findCallerUser()
                .filter(user::equals)
                .isPresent();
    }


    private User saveUser(User user) {
        checkUserHasPassword(user);
        User managedUser = entityManager.merge(user);
        return managedUser;
    }

    private void removeUser(User user) {
        try {
            User managedUser = entityManager.merge(user);
            entityManager.remove(managedUser);
        } catch (Exception e) {
            throw new AuthenticatorRuntimeException("Could not remove user", e);
        }
    }


    private UserApplication saveUserApplication(UserApplication userApplication) {
        UserApplication managedUserApplication = entityManager.merge(userApplication);
        return managedUserApplication;
    }

    private void removeUserApplication(UserApplication userApplication) {
        try {
            UserApplication managedApplication = entityManager.merge(userApplication);
            entityManager.remove(managedApplication);
        } catch (Exception e) {
            throw new AuthenticatorRuntimeException("Could not remove user application", e);
        }
    }
}
