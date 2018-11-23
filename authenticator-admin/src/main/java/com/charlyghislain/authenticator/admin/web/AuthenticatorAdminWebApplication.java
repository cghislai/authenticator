package com.charlyghislain.authenticator.admin.web;

import com.charlyghislain.authenticator.admin.web.provider.AuthenticatorAdminWebExceptionMapper;
import com.charlyghislain.authenticator.admin.web.provider.CrossOriginResourceSharingRequestFilter;
import com.charlyghislain.authenticator.admin.web.provider.CrossOriginResourceSharingResponseFilter;
import com.charlyghislain.authenticator.admin.web.provider.ThrowableExceptionMapper;
import com.charlyghislain.authenticator.admin.web.provider.WebApplicationExceptionMapper;
import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/admin")
@DeclareRoles(AuthenticatorConstants.ROLE_ADMIN)
@RolesAllowed(AuthenticatorConstants.ROLE_ADMIN)
public class AuthenticatorAdminWebApplication extends Application {

    @NonNull
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(AdminApplicationResourceController.class);
        classes.add(AdminUserResourceController.class);
        classes.add(AdminKeyResourceController.class);

        classes.add(AuthenticatorAdminWebExceptionMapper.class);
        classes.add(CrossOriginResourceSharingRequestFilter.class);
        classes.add(CrossOriginResourceSharingResponseFilter.class);
        classes.add(ThrowableExceptionMapper.class);
        classes.add(WebApplicationExceptionMapper.class);

        return classes;
    }
}
