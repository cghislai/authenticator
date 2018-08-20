package com.charlyghislain.authenticator.ejb.configuration;

public class ConfigConstants {

    public static final String AUTHENTICATOR_NAME
            = "com.charlyghislain.authenticator.name";
    public static final String AUTHENTICATOR_URL
            = "com.charlyghislain.authenticator.url";

    public static final String TOKEN_ISSUER
            = "com.charlyghislain.authenticator.token.issuer";
    public static final String TOKEN_VALIDITY_MINUTES
            = "com.charlyghislain.authenticator.token.validityMinutes";
    public static final String TOKEN_VALIDATION_LEEWAY_SECONDS
            = "com.charlyghislain.authenticator.token.validationLeewaySeconds";

    public static final String APPLICATION_SECRET_TOKEN_VALIDITY_DAYS
            = "com.charlyghislain.authenticator.application.secret.token.validationDays";

    public static final String ADMIN_DEFAULT_PASSWORD
            = "com.charlyghislain.authenticator.admin.defaultPassword";
    public static final String ADMIN_DEFAULT_EMAIL
            = "com.charlyghislain.authenticator.admin.defaultEmail";

    public static final String CORS_ALLOWED_ORIGINS
            = "com.charlyghislain.authenticator.cors.allowedOrigins";

}
