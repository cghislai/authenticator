package com.charlyghislain.authenticator.web;

import com.charlyghislain.authenticator.domain.domain.util.AuthenticatorConstants;
import com.charlyghislain.authenticator.web.provider.AuthenticatorWebExceptionMapper;
import com.charlyghislain.authenticator.web.provider.CrossOriginResourceSharingRequestFilter;
import com.charlyghislain.authenticator.web.provider.CrossOriginResourceSharingResponseFilter;
import com.charlyghislain.authenticator.web.provider.ThrowableExceptionMapper;
import com.charlyghislain.authenticator.web.provider.WebApplicationExceptionMapper;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("")
@DeclareRoles(AuthenticatorConstants.ROLE_USER)
public class AuthenticatorWebApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(InfoResourceController.class);
        classes.add(LoggedUserResourceController.class);
        classes.add(TokenResourceController.class);
        classes.add(PasswordResourceController.class);

        classes.add(AuthenticatorWebExceptionMapper.class);
        classes.add(CrossOriginResourceSharingRequestFilter.class);
        classes.add(CrossOriginResourceSharingResponseFilter.class);
        classes.add(ThrowableExceptionMapper.class);
        classes.add(WebApplicationExceptionMapper.class);

        return classes;
    }
}
