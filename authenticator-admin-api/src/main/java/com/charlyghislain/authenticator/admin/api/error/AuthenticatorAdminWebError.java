package com.charlyghislain.authenticator.admin.api.error;

public enum AuthenticatorAdminWebError {

    UNEXPECTED_ERROR(500),
    NO_SIGNING_KEY(406),
    NAME_ALREADY_EXISTS(406),
    LOCKING_ADMIN_OUT(406),
    EMAIL_ALREADY_EXISTS(406),
    LAST_ACTIVE_KEY_IN_SCOPE(406),
    KEY_SCOPE_CHANGED(400),
    SIGNING_KEY_DEACTIVATION(400),
    APPLICAITON_NOT_FOUND(404),
    KEY_NOT_FOUND(404),
    USER_NOT_FOUND(404),
    SORTS_DESERIALIZATION_EXCEPTION(400);

    int httpStatus;

    AuthenticatorAdminWebError(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
