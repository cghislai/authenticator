package com.charlyghislain.authenticator.management.api.error;

public enum AuthenticatorManagementWebError {

    UNEXPECTED_ERROR(500),
    APPLICATION_NOT_FOUND(404),
    SORTS_DESERIALIZATION_EXCEPTION(400),
    UNAUTHENTICATED_ERROR(401),
    EMAIL_ALREADY_EXISTS(406),
    NAME_ALREADY_EXISTS(406),
    USER_NOT_FOUND(404),
    UNAUTHORIZED_OPERATION(403);

    int httpStatus;

    AuthenticatorManagementWebError(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
