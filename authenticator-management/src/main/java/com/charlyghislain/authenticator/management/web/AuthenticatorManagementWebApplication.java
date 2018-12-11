package com.charlyghislain.authenticator.management.web;

import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.management.api.error.AuthenticatorManagementWebException;
import com.charlyghislain.authenticator.management.web.provider.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/management")
@DeclareRoles({AuthenticatorConstants.ROLE_APP_MANAGEMENT})
@RolesAllowed(AuthenticatorConstants.ROLE_APP_MANAGEMENT)
public class AuthenticatorManagementWebApplication extends Application {

    @NonNull
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(ConnectivityResourceController.class);
        classes.add(UserResourceController.class);

        classes.add(AuthenticatorManagementValidationExceptionMapper.class);
        classes.add(AuthenticatorManagementWebExceptionMapper.class);
        classes.add(CrossOriginResourceSharingRequestFilter.class);
        classes.add(CrossOriginResourceSharingResponseFilter.class);
        classes.add(ThrowableExceptionMapper.class);
        classes.add(WebApplicationExceptionMapper.class);

        return classes;
    }
}
