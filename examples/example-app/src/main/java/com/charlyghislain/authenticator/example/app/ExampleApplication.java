package com.charlyghislain.authenticator.example.app;

import org.eclipse.microprofile.auth.LoginConfig;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("")
@LoginConfig(authMethod = "MP-JWT")
@DeclareRoles({ApplicationRoles.EXAMPLE_ROLE, ApplicationRoles.AUTHNETICATOR_APP_ROLE})
public class ExampleApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(ExampleResourceController.class);
        classes.add(UserEventResourceController.class);
        classes.add(AuthorizationResourceController.class);
        return classes;
    }
}
