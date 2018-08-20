package com.charlyghislain.authenticator.ejb.configuration;


import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;

@DatabaseIdentityStoreDefinition(
        dataSourceLookup = "jdbc/authenticator",
        callerQuery = "SELECT password from auth_user where active = true and name = ?",
        groupsQuery = "SELECT role from user_roles where name = ?"
)
public class DatabaseIdentityStoreConfiguration {
}
