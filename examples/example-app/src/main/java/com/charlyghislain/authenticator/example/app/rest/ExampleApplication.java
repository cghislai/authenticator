package com.charlyghislain.authenticator.example.app.rest;

import com.charlyghislain.authenticator.example.app.ApplicationRoles;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.eclipse.microprofile.auth.LoginConfig;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("")
@LoginConfig(authMethod = "MP-JWT")
@DeclareRoles({ApplicationRoles.EXAMPLE_ROLE, ApplicationRoles.AUTHENTICATOR_APP_ROLE})
public class ExampleApplication extends Application {

    @NonNull
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(ExampleResourceController.class);
        classes.add(UserEventResourceController.class);
        classes.add(AuthorizationResourceController.class);
        return classes;
    }
}
