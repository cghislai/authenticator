package com.charlyghislain.authenticator.ejb.configuration;

import com.charlyghislain.authenticator.ejb.service.DefaultResourcesService;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class AuthApplicationInitialization {

    @EJB
    private LiquibaseChangelogRunner liquibaseChangelogRunner;
    @EJB
    private DefaultResourcesService defaultResourcesService;

    @PostConstruct
    public void init() {
        liquibaseChangelogRunner.runChangeLogs();
        defaultResourcesService.createDefaultResources();

    }

}
