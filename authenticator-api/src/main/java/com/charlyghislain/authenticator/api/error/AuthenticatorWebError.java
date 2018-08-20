package com.charlyghislain.authenticator.api.error;

public enum AuthenticatorWebError {

    UNEXPECTED_ERROR(500),
    APPLICATION_NOT_FOUND(404),
    UNAUTHENTICATED_ERROR(401),
    EMAIL_ALREADY_EXISTS(406),
    NAME_ALREADY_EXISTS(406),
    UNAUTHORIZED_OPERATION(403);

    int httpStatus;

    AuthenticatorWebError(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
