package com.charlyghislain.authenticator.ejb.configuration;


import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;

@DatabaseIdentityStoreDefinition(
        dataSourceLookup = "jdbc/authenticator",
        callerQuery = "SELECT password from user_password where name = ?",
        groupsQuery = "SELECT role from user_roles where name = ?"
)
public class DatabaseIdentityStoreConfiguration {
}
