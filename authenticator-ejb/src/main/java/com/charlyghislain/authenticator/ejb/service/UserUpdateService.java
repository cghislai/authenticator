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

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
    @Inject
    private EmailVerificationUpdateService emailVerificationUpdateService;
    @Inject
    private PasswordResetTokenUpdateService passwordResetTokenUpdateService;

    //    @DenyAll
    public User createUserNoChecks(User user) throws NameAlreadyExistsException, EmailAlreadyExistsException {
        String name = user.getName();
        String email = user.getEmail();

        checkDuplicateName(name);
        checkDuplicateEmail(email);

        user.setCreationTime(LocalDateTime.now());
        return saveUser(user);
    }

    @RolesAllowed({AuthenticatorConstants.ROLE_ADMIN})
    public User createUser(User newUser) throws NameAlreadyExistsException, EmailAlreadyExistsException {
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
        user.setCreationTime(LocalDateTime.now());

        return saveUser(user);
    }

    @RolesAllowed({AuthenticatorConstants.ROLE_ADMIN})
    public User updateUser(@NotNull User existingUser, User userUpdate) throws NameAlreadyExistsException, EmailAlreadyExistsException, AdminCannotLockHerselfOutException {
        String name = userUpdate.getName();
        String email = userUpdate.getEmail();
        boolean admin = userUpdate.isAdmin();
        boolean active = userUpdate.isActive();
        boolean emailVerified = userUpdate.isEmailVerified();


        checkDuplicateName(existingUser, name);
        checkDuplicateEmail(existingUser, email);
        checkActiveStateChange(active, existingUser);
        checkAdminStateChange(admin, existingUser);

        boolean emailChanged = !existingUser.getEmail().equals(email);
        boolean emailVerifiedChanged = existingUser.isEmailVerified() != emailVerified;

        existingUser.setName(name);
        existingUser.setEmail(email);
        existingUser.setActive(active);
        existingUser.setAdmin(admin);
        existingUser.setEmailVerified(emailVerified);
        if (emailChanged) {
            existingUser.setEmailVerified(false || emailVerified);
            emailVerificationUpdateService.removeAllUserTokens(existingUser);
        }
        if (emailVerifiedChanged && emailVerified) {
            this.applicationEventService.notifiyEmailVerified(existingUser);
        }
        return saveUser(existingUser);
    }

    @RolesAllowed({AuthenticatorConstants.ROLE_APPLICATION, AuthenticatorConstants.ROLE_ADMIN})
    public UserApplication createApplicationUser(@NotNull Application application, User newUser) throws NameAlreadyExistsException, EmailAlreadyExistsException {
        String name = newUser.getName();
        String email = newUser.getEmail();
        boolean active = newUser.isActive();

        checkDuplicateName(name);
        checkDuplicateEmail(email);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setActive(true);
        user.setAdmin(false);
        user.setEmailVerified(false);
        user.setPasswordExpired(true);
        user.setCreationTime(LocalDateTime.now());

        User managedUser = saveUser(user);

        UserApplication userApplication = new UserApplication();
        userApplication.setUser(managedUser);
        userApplication.setApplication(application);
        userApplication.setActive(active);
        userApplication.setCreationTime(LocalDateTime.now());
        UserApplication managedUserApplication = saveUserApplication(userApplication);

        managedUser.getUserApplications().add(managedUserApplication);
        applicationEventService.notifyUserAdded(managedUserApplication);

        return managedUserApplication;
    }

    @RolesAllowed({AuthenticatorConstants.ROLE_ADMIN})
    public UserApplication setUserApplicationActive(@NotNull UserApplication existinUserApplication, boolean active) {

        existinUserApplication.setActive(active);

        return saveUserApplication(existinUserApplication);
    }

    @RolesAllowed({AuthenticatorConstants.ROLE_APPLICATION})
    public UserApplication updateApplicationUser(@NotNull UserApplication userApplication, User userUpdate) {
        boolean active = userUpdate.isActive();

        userApplication.setActive(active);

        return saveUserApplication(userApplication);
    }


    @RolesAllowed({AuthenticatorConstants.ROLE_ADMIN, AuthenticatorConstants.ROLE_APPLICATION})
    public void forgetApplicationUser(@NotNull UserApplication userApplication) {
        Application application = userApplication.getApplication();
        User user = userApplication.getUser();
        Long userId = user.getId();

        User managedUser = removeUserApplication(userApplication);

        applicationEventService.notifyUserRemoved(application, userId);

        boolean hasNoMoreApplications = checkHasNoMoreApplications(managedUser);
        if (hasNoMoreApplications && !managedUser.isAdmin()) {
            removeUser(managedUser);
        }
    }

    @RolesAllowed(AuthenticatorConstants.ROLE_USER)
    public UserApplication linkUserToApplicationOnTokenRequest(@NotNull User user, @NotNull Application application) throws UnauthorizedOperationException {
        checkIsCallerUser(user);
        checkIsActive(user);
        checkDoesNotHaveApplication(user, application);
        checkUsersAreAddedOnTokenRequest(application);

        boolean addedUsersAreActive = application.isAddedUsersAreActive();

        UserApplication userApplication = new UserApplication();
        userApplication.setUser(user);
        userApplication.setApplication(application);
        userApplication.setCreationTime(LocalDateTime.now());
        userApplication.setActive(addedUsersAreActive);
        UserApplication managedUserApplication = saveUserApplication(userApplication);

        user.getUserApplications().add(managedUserApplication);
        User managedUser = saveUser(user);
        managedUserApplication.setUser(managedUser);

        applicationEventService.notifyUserAdded(managedUserApplication);
        return managedUserApplication;
    }

    @RolesAllowed(AuthenticatorConstants.ROLE_USER)
    public void unlinkUserToApplication(@NotNull UserApplication userApplication) throws UnauthorizedOperationException {
        User user = userApplication.getUser();
        Application application = userApplication.getApplication();
        Long userId = user.getId();

        checkIsActive(user);
        checkIsCallerUser(user);

        User managedUser = removeUserApplication(userApplication);

        applicationEventService.notifyUserRemoved(application, userId);

        boolean hasNoMoreApplications = checkHasNoMoreApplications(managedUser);
        if (hasNoMoreApplications && !user.isAdmin()) {
            removeUser(managedUser);
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
            emailVerificationUpdateService.removeAllUserTokens(user);
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

    @PermitAll
    public User resetUserPassword(@NotNull User user, @ValidPassword String plainPassword, @NotNull String resetToken) throws UnauthorizedOperationException {
        checkIsActive(user);
        validatePasswordResetToken(user, resetToken);

        String hashedPassword = passwordHash.generate(plainPassword.toCharArray());
        boolean emailWasVerified = user.isEmailVerified();

        user.setPassword(hashedPassword);
        user.setPasswordExpired(false);
        user.setEmailVerified(true);
        User managedUser = saveUser(user);
        passwordResetTokenUpdateService.removeAllUserTokens(managedUser);

        if (!emailWasVerified) {
            applicationEventService.notifiyEmailVerified(managedUser);
        }
        return managedUser;
    }


    @RolesAllowed(AuthenticatorConstants.ROLE_APPLICATION)
    public UserApplication updateApplicationUserPassword(@NotNull UserApplication userApplication, @ValidPassword String plainPassword) throws UnauthorizedOperationException {
        Application application = userApplication.getApplication();
        checkApplicationCanResetPasswords(application);

        User user = userApplication.getUser();
        String hashedPassword = passwordHash.generate(plainPassword.toCharArray());

        user.setPassword(hashedPassword);
        user.setPasswordExpired(false);

        User managedUser = saveUser(user);
        passwordResetTokenUpdateService.removeAllUserTokens(managedUser);

        userApplication.setUser(managedUser);
        UserApplication managedUserApplication = saveUserApplication(userApplication);
        managedUser.getUserApplications().add(managedUserApplication);

        return managedUserApplication;
    }


    @RolesAllowed(AuthenticatorConstants.ROLE_ADMIN)
    public User setUserPassword(@NotNull User user, @ValidPassword String plainPassword) {
        String hashedPassword = passwordHash.generate(plainPassword.toCharArray());

        user.setPassword(hashedPassword);
        user.setPasswordExpired(false);
        User managedUser = saveUser(user);

        passwordResetTokenUpdateService.removeAllUserTokens(managedUser);
        return managedUser;
    }


    private void checkApplicationCanResetPasswords(Application application) throws UnauthorizedOperationException {
        boolean canResetUserPassword = application.isCanResetUserPassword();
        if (!canResetUserPassword) {
            throw new UnauthorizedOperationException();
        }
    }

    private void checkUsersAreAddedOnTokenRequest(Application application) throws UnauthorizedOperationException {
        boolean existingUsersAreAddedOnTokenRequest = application.isExistingUsersAreAddedOnTokenRequest();
        if (!existingUsersAreAddedOnTokenRequest) {
            throw new UnauthorizedOperationException();
        }
    }

    private void validatePasswordResetToken(User user, String resetToken) throws UnauthorizedOperationException {
        boolean valid = passwordResetTokenUpdateService.validatePasswordResetToken(user, resetToken);
        if (!valid) {
            throw new UnauthorizedOperationException();
        }
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


    private void checkDoesNotHaveApplication(User user, Application application) throws UnauthorizedOperationException {
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


    private User saveUser(@Valid User user) {
        checkUserHasPassword(user);
        User managedUser = entityManager.merge(user);
        return managedUser;
    }

    private void removeUser(User user) {
        try {
            User managedUser = entityManager.merge(user);
            this.emailVerificationUpdateService.removeAllUserTokens(managedUser);
            this.passwordResetTokenUpdateService.removeAllUserTokens(managedUser);
            entityManager.remove(managedUser);
        } catch (Exception e) {
            throw new AuthenticatorRuntimeException("Could not remove user", e);
        }
    }


    private UserApplication saveUserApplication(@Valid UserApplication userApplication) {
        UserApplication managedUserApplication = entityManager.merge(userApplication);
        return managedUserApplication;
    }

    private User removeUserApplication(UserApplication userApplication) {
        try {
            UserApplication managedApplication = entityManager.merge(userApplication);
            User managedUser = managedApplication.getUser();

            this.emailVerificationUpdateService.removeAllUserApplicationTokens(managedApplication);
            entityManager.remove(managedApplication);

            managedUser.getUserApplications().remove(managedApplication);
            return managedUser;
        } catch (Exception e) {
            throw new AuthenticatorRuntimeException("Could not remove user application", e);
        }
    }
}
