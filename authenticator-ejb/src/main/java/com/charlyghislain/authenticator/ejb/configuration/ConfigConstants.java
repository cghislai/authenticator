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
            = "com.charlyghislain.authenticator.application.secret.token.validityDays";

    public static final String EMAIL_VERIFICATION_TOKEN_VALIDITY_DAYS
            = "com.charlyghislain.authenticator.emailVerificationToken.validityDays";

    public static final String PASSWORD_RESET_TOKEN_VALIDITY_DAYS
            = "com.charlyghislain.authenticator.passwordResetToken.validityDays";

    public static final String ADMIN_DEFAULT_PASSWORD
            = "com.charlyghislain.authenticator.admin.defaultPassword";
    public static final String ADMIN_DEFAULT_EMAIL
            = "com.charlyghislain.authenticator.admin.defaultEmail";

    public static final String CREATE_DEFAULT_APPLICATION
            = "com.charlyghislain.authenticator.createDefaultApplication";
    public static final String DEFAULT_APPLICATION_NAMES
            = "com.charlyghislain.authenticator.defaultApplicationNames";
    public static final String DEFAULT_APPLICATION_ENDPOINT_URLS
            = "com.charlyghislain.authenticator.defaultApplicationEndpointUrls";

    public static final String UNSAFE_FEATURES_ENABLED
            = "com.charlyghislain.authenticator.unsafeFeaturesEnabled";
    public static final String UNSAFE_DETERMINISTIC_KEYS
            = "com.charlyghislain.authenticator.unsafe.deterministicKeysGeneration";

    public static final String CORS_ALLOWED_ORIGINS
            = "com.charlyghislain.authenticator.cors.allowedOrigins";

    public static final String ERRORS_SEND_MAIL
            = "com.charlyghislain.authenticator.error.sendMail";
    public static final String ERROR_MAIL_FROM
            = "com.charlyghislain.authenticator.error.mail.from";
    public static final String ERROR_MAIL_TO
            = "com.charlyghislain.authenticator.error.mail.to";

}
