# Authenticator

This service signs JWT tokens to be used by applications relying on the
[JWT-auth 1.0](https://github.com/eclipse/microprofile-jwt-auth/blob/1.0/spec/src/main/asciidoc/microprofile-jwt-auth-spec.asciidoc)
microoprofile spec.

## Getting started

You may deploy the `authenticator-web` artifact or provide the different modules in an ear.
You need to configure your application container to reference a jndi resource `jdbc/authenticator` which
will be used to connect to the database.

### Configuration
A [configuration file](https://raw.githubusercontent.com/cghislai/authenticator/master/authenticator-ejb/src/main/resources/META-INF/microprofile-config.properties)
defines the configuration keys used and their default value.

If you do not provide an admin password at startup, one will be generated and printed in the logs.

### Creating applications

Once the service is running, you must register an application and generate an application secret.
Use the [admin api](https://raw.githubusercontent.com/cghislai/authenticator/master/authenticator-admin-api/src/main/java/com/charlyghislain/authenticator/admin/api/AdminApplicationResource.java)
with your admin credentials to do so.

### Implementing application-api

Applications implement the [application-api](https://github.com/cghislai/authenticator/tree/master/authenticator-application-api/src/main/java/com/charlyghislain/authenticator/application/api)
and accept token signed by this service.
- The authorization resource contains a `listUserRoles` returning s a list of roles that will be provided in the token as well
as an healthcheck endpoint to ensure the jwt tokens are handled correctly.
- The health resource should be already implemented by your container providing the microprofile-health api.
- The user event resource contains 3 endpoints that will be called asynchronously. 

The JWT-auth 1.0 spec does not specify how to configure the public key and issuer to verify the token.
It depends on your container, see the [javaee-sample](https://github.com/javaee-samples/microprofile1.2-samples/tree/master/jwt-auth) for
more info.

### Example application
Building the project with the `example` profile active produces a payaramicro executable jar deploying `authenticator-web`
and another one deploying an example application. the authenticator service will be deployed on port 8443,
the example application on port 8444. The authenticator service will be preconfigured for a second application
deployed on port 8445 - to make use of this one, you have to generate a secret and specify the port and secret, for instance
by passing system properties: `-Dpayaramicro.sslPort=8445 -DappSecret=<generated app secret>`.

The example application exposes a /test/ servlet with a login/register form and displaying rest responses
and internal events.  

## Architecture

The service handles applications, keys and users.

Applications implement the authenticator-application-api and make use of the authenticator-management-api.
Applications are linked to one active signing key. User tokens requested for an application scope
are signed using the application key. Additionally, this service will use the application key to 
authenticate itself when notifying the application of user events.
Applications authenticate themselves using an application secret. This is a long-lived JWT token signed
with a specific key. Applications management is performed using the authenticator-admin-api.

Keys may be scoped to an application or to this service. Three key scopes are handled:
* Keys intended to sign user token for this service audience
* Keys intended to sign application secrets for this service audience
* Keys intended to sign token for an application audience
Although 1 key for each scope is probably sufficient, keys may be rotated and deactivated.
Key management is performed using the authenticator-admin-api.


Users are expected to create accounts on applications. Users with an existing account may use
a rest resource to consult and update their data. User email verification is delegated to applications
which have the permission.


