package com.charlyghislain.authenticator.management.web;

import com.charlyghislain.authenticator.domain.domain.*;
import com.charlyghislain.authenticator.domain.domain.exception.EmailAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.exception.ValidationException;
import com.charlyghislain.authenticator.domain.domain.exception.NameAlreadyExistsException;
import com.charlyghislain.authenticator.domain.domain.exception.UnauthorizedOperationException;
import com.charlyghislain.authenticator.domain.domain.filter.UserApplicationFilter;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.domain.domain.util.Pagination;
import com.charlyghislain.authenticator.domain.domain.util.ResultList;
import com.charlyghislain.authenticator.ejb.service.EmailVerificationUpdateService;
import com.charlyghislain.authenticator.ejb.service.PasswordResetTokenUpdateService;
import com.charlyghislain.authenticator.ejb.service.UserQueryService;
import com.charlyghislain.authenticator.ejb.service.UserUpdateService;
import com.charlyghislain.authenticator.management.api.UserResource;
import com.charlyghislain.authenticator.management.api.domain.*;
import com.charlyghislain.authenticator.management.api.error.AuthenticatorManagementValidationException;
import com.charlyghislain.authenticator.management.api.error.AuthenticatorManagementWebError;
import com.charlyghislain.authenticator.management.api.error.AuthenticatorManagementWebException;
import com.charlyghislain.authenticator.management.api.domain.WsViolationError;
import com.charlyghislain.authenticator.management.web.converter.*;
import com.charlyghislain.authenticator.management.web.provider.CallerManagedApplication;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RolesAllowed({AuthenticatorConstants.ROLE_APP_MANAGEMENT})
public class UserResourceController implements UserResource {

    @Inject
    private UserUpdateService userUpdateService;
    @Inject
    private UserQueryService userQueryService;
    @Inject
    private WsApplicationUserConverter wsApplicationUserConverter;
    @Inject
    private UserApplicationFilterConverter userApplicationFilterConverter;
    @Inject
    private UserConverter userConverter;
    @Inject
    private EmailVerificationUpdateService emailVerificationUpdateService;
    @Inject
    private WsEmailVerificationTokenConverter wsEmailVerificationTokenConverter;
    @Inject
    private PasswordResetTokenUpdateService passwordResetTokenUpdateService;
    @Inject
    private WsPasswordResetTokenConverter wsPasswordResetTokenConverter;
    @Inject
    private WsValidationErrorConverter wsValidationErrorConverter;

    @Inject
    @CallerManagedApplication
    private Application callerManagedApplication;

    @NonNull
    @Override
    public WsApplicationUser createUser(@NonNull WsApplicationUserWithPassword wsApplicationUser) {
        User user = userConverter.toUser(wsApplicationUser);
        String password = wsApplicationUser.getPassword();
        boolean passwordValid = userUpdateService.checkPasswordValidity(password);
        if (!passwordValid) {
            // FIXME: use bean validation / wsvalidationerror returned from auth backend
            WsViolationError wsViolationError = new WsViolationError("password", "com.charlyghislain.authenticator.domain.domain.validation.ValidPassword.message");
            Set<WsViolationError> errorSet = Collections.singleton(wsViolationError);
            throw new AuthenticatorManagementValidationException(AuthenticatorManagementWebError.INVALID_PASSWORD.name(), errorSet);
        }
        try {
            UserApplication newUserApplication = userUpdateService.createApplicationUser(callerManagedApplication, user, password);
            return wsApplicationUserConverter.toWsUserApplication(newUserApplication);
        } catch (EmailAlreadyExistsException e) {
            throw new AuthenticatorManagementWebException(AuthenticatorManagementWebError.EMAIL_ALREADY_EXISTS);
        } catch (NameAlreadyExistsException e) {
            throw new AuthenticatorManagementWebException(AuthenticatorManagementWebError.NAME_ALREADY_EXISTS);
        } catch (ValidationException e) {
            Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
            Set<WsViolationError> validationErrors = constraintViolations.stream()
                    .map(wsValidationErrorConverter::toWsValidationError)
                    .collect(Collectors.toSet());
            throw new AuthenticatorManagementValidationException(AuthenticatorManagementWebError.VALIDATION_ERROR.name(), validationErrors);
        }
    }

    @NonNull
    @Override
    public WsResultList<WsApplicationUser> listUsers(@NonNull WsUserApplicationFilter filter, @NonNull WsPagination wsPagination) {
        UserApplicationFilter userApplicationFilter = userApplicationFilterConverter.toUserApplicationFilter(filter);
        Pagination<UserApplication> pagination = userApplicationFilterConverter.toPagination(wsPagination);
        userApplicationFilter.setApplication(callerManagedApplication);

        ResultList<UserApplication> userApplicationResultList = userQueryService.findUserApplications(userApplicationFilter, pagination);
        List<WsApplicationUser> results = userApplicationResultList.map(wsApplicationUserConverter::toWsUserApplication).getResults();
        return new WsResultList<>(results, userApplicationResultList.getTotalCount());
    }

    @NonNull
    @Override
    public WsApplicationUser getUser(Long userId) {
        UserApplication userApplication = getUserApplication(userId);
        return wsApplicationUserConverter.toWsUserApplication(userApplication);
    }

    @NonNull
    @Override
    public WsApplicationUser updateUser(Long userId, @NonNull WsApplicationUser wsApplicationUser) {
        UserApplication userApplication = getUserApplication(userId);
        User user = userConverter.toUser(wsApplicationUser);
        UserApplication updatedUserApplication = userUpdateService.updateApplicationUser(userApplication, user);
        return wsApplicationUserConverter.toWsUserApplication(updatedUserApplication);
    }

    @NonNull
    @Override
    public WsApplicationUser updateUserPassword(Long userId, @NonNull String password) {
        UserApplication userApplication = getUserApplication(userId);
        try {
            UserApplication updatedUserApplication = userUpdateService.updateApplicationUserPassword(userApplication, password);
            return wsApplicationUserConverter.toWsUserApplication(updatedUserApplication);
        } catch (UnauthorizedOperationException e) {
            throw new AuthenticatorManagementWebException(AuthenticatorManagementWebError.UNAUTHORIZED_OPERATION);
        }
    }

    @NonNull
    @Override
    public WsPasswordResetToken createNewPasswordResetToken(Long userId) {
        UserApplication userApplication = getUserApplication(userId);
        User user = userApplication.getUser();

        PasswordResetToken newResetToken = passwordResetTokenUpdateService.createNewResetToken(user);
        return wsPasswordResetTokenConverter.toWsPasswordResetToken(newResetToken);
    }

    @Override
    public void resetUserPassword(Long userId, @NonNull WsPasswordReset wsPasswordReset) {
        UserApplication userApplication = getUserApplication(userId);
        User user = userApplication.getUser();
        String resetToken = wsPasswordReset.getResetToken();
        String password = wsPasswordReset.getPassword();

        try {
            userUpdateService.resetUserPassword(user, password, resetToken);
        } catch (UnauthorizedOperationException e) {
            throw new AuthenticatorManagementWebException(AuthenticatorManagementWebError.UNAUTHORIZED_OPERATION);
        }
    }

    @Override
    public void forgetUser(Long userId) {
        UserApplication userApplication = getUserApplication(userId);
        userUpdateService.forgetApplicationUser(userApplication);
    }

    @NonNull
    @Override
    public WsEmailVerificationToken getEmailVerificationToken(Long userId) {
        UserApplication userApplication = getUserApplication(userId);
        EmailVerificationToken verificationToken = emailVerificationUpdateService.createNewVerificationToken(userApplication);

        return wsEmailVerificationTokenConverter.toWsEmailVerificationToken(verificationToken);
    }

    @Override
    public void checkEmailVerification(Long userId, String verificationToken) {
        UserApplication userApplication = getUserApplication(userId);
        try {
            emailVerificationUpdateService.validateUserEmail(userApplication, verificationToken);
        } catch (UnauthorizedOperationException e) {
            throw new AuthenticatorManagementWebException(AuthenticatorManagementWebError.UNAUTHORIZED_OPERATION);
        }
    }

    @NotNull
    private UserApplication getUserApplication(Long userId) {
        UserApplicationFilter userApplicationFilter = new UserApplicationFilter();
        userApplicationFilter.setApplication(callerManagedApplication);
        userApplicationFilter.getUserFilter().setId(userId);

        return userQueryService.findUserApplication(userApplicationFilter)
                .orElseThrow(() -> new AuthenticatorManagementWebException(AuthenticatorManagementWebError.USER_NOT_FOUND));
    }
}
