package com.charlyghislain.authenticator.domain.domain.util;

public class AuthenticatorConstants {

    public static final String AUTHENTICATOR_ROLES_PREFIX = "authenticator.";
    public static final String ROLE_ADMIN = "authenticator.ADMIN";
    public static final String ROLE_USER = "authenticator.USER";
    public static final String ROLE_ACTIVE = "authenticator.ACTIVE";
    public static final String ROLE_APPLICATION = "authenticator.APP";
    // Appended with the application id
    public static final String ROLE_APPLICATION_RESTRICTION = "authenticator.APP.";
    public static final String ROLE_APP_MANAGEMENT = "authenticator.APP_MANAGEMENT";

    public static final String DEFAULT_KEY_PAIR_NAME = "default";
    public static final String DEFAULT_KEY_PAIR_FOR_SECRETS_NAME = "app-secrets-default";
    public static final String DEFAULT_ADMIN_USER_NAME = "admin";

    public static final String MP_JWT_USER_PRINCIPAL_CLAIM_NAME = "upn";
    public static final String MP_JWT_GROUPS_CLAIM_NAME = "groups";
    public static final String USER_ID_CLAIM_NAME = "uid";
    public static final String APPLICATION_ID_CLAIM_NAME = "aid";

    public static final String AUTHENTICAOTOR_PRINCIPAL_NAME = "com.charlyghislain.authenticator";
    public static final String APPLICATION_PRINCIPAL_NAME_PREFIX = "com.charlyghislain.authenticator.app.";


}
