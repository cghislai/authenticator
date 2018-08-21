package com.charlyghislain.authenticator.web;


import com.charlyghislain.authenticator.api.PasswordResource;
import com.charlyghislain.authenticator.api.domain.WsPasswordReset;
import com.charlyghislain.authenticator.api.error.AuthenticatorWebError;
import com.charlyghislain.authenticator.api.error.AuthenticatorWebException;
import com.charlyghislain.authenticator.domain.domain.User;
import com.charlyghislain.authenticator.domain.domain.exception.UnauthorizedOperationException;
import com.charlyghislain.authenticator.ejb.service.UserQueryService;
import com.charlyghislain.authenticator.ejb.service.UserUpdateService;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;

@PermitAll
public class PasswordResourceController implements PasswordResource {

    @Inject
    private UserUpdateService userUpdateService;
    @Inject
    private UserQueryService userQueryService;

    @Override
    public void resetPassword(WsPasswordReset wsPasswordReset) {
        Long userId = wsPasswordReset.getUserId();
        String resetToken = wsPasswordReset.getResetToken();
        String password = wsPasswordReset.getPassword();

        User user = userQueryService.findUserById(userId)
                .orElseThrow(this::newUnauthorizedException);
        try {
            userUpdateService.resetUserPassword(user, password, resetToken);
        } catch (UnauthorizedOperationException e) {
            throw newUnauthorizedException();
        }
    }

    private AuthenticatorWebException newUnauthorizedException() {
        return new AuthenticatorWebException(AuthenticatorWebError.UNAUTHORIZED_OPERATION);
    }

}
