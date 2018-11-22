# Authenticator
[![Maven Central](https://img.shields.io/maven-central/v/com.charlyghislain.authenticator/authenticator.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.charlyghislain.authenticator%22%20AND%20a:%22authenticator%22)


This service signs JWT tokens to be used by applications relying on the
[JWT-auth 1.1](https://github.com/eclipse/microprofile-jwt-auth/blob/1.1/spec/src/main/asciidoc/microprofile-jwt-auth-spec.asciidoc)
microoprofile spec.

## Architecture

The goal of this service is to allow application developers to rely on a trusted third party for user authentication.
This trust is ensured by multiple means:
- The TLS certificates exposed by this service during the SSL handshake guarantees an application is talking to a
genuine authenticator service.
- Cryptographic signature of the JWT tokens exchanged with each API call allow each party to check the signature validity. 


The project is split into several modules:
- At the core, `authenticator-ejb` provides the services to alter the database state, sign and verify jwt, and trigger
calls to applications. `authenticator-domain` contains the POJOs exchanged during these internal calls, as well as the
database-bound entities and some shared stuff.
- A first web resource module, `authenticator`, and its api, `autenticator-api` exposes web services targetting 
end-user (applications frontends). It contains endpoints to sign an user in, out, get the logged user information, ...
- Another web resources module, `authenticator-admin`, and its api, `authenticator-admin-api` expose web resources
accessible for the administrator user. They can be used to manage users, applications and keys.
- Another web resource module, `authenticator-management` and its api `authenticator-management-api` expose web
resources targetting applications (backends). It allows applications registered with this service to create/register
new users, validate their email, request new passwords, ...;
- An application API, `authenticator-application-api` define rest resources an application must implement to integrates
with this service. It is barely a couple of REST resources called on various events, like an user has been created,
or removed, or an email address has been validated. The `authenticator-application-client` module contains the HTTP
client used to contact applications using this API.


## Getting started

You may deploy the `authenticator-web` artifact, which is a war bundling the modules decribed above, or provide the
different modules in an ear. You need to configure your application container to reference a jndi resource 
`jdbc/authenticator` which will be used to connect to the database. Take a look at the `example/microbundle` project to build
a payara-micro microbundle deploying the service using an h2 database.

### Configuration
A [configuration file](https://raw.githubusercontent.com/cghislai/authenticator/master/authenticator-ejb/src/main/resources/META-INF/microprofile-config.properties)
defines the configuration keys used and their default value.
You can provide configuration values using the microprofile-config mechanisms.

If you do not provide an admin password at startup, one will be generated and printed in the logs.

### Creating applications

Once the service is running, you must register an application and generate an application secret.
Use the [admin api](https://raw.githubusercontent.com/cghislai/authenticator/master/authenticator-admin-api/src/main/java/com/charlyghislain/authenticator/admin/api/AdminApplicationResource.java)
with your admin credentials to do so.
You can find some example requests in the test resources of the `example/microbundle` module.

### Implementing application-api

Applications implement the [application-api](https://github.com/cghislai/authenticator/tree/master/authenticator-application-api/src/main/java/com/charlyghislain/authenticator/application/api)
and accept token signed by this service.
- The authorization resource contains a `listUserRoles` returning s a list of roles that will be provided in the token as well
as an healthcheck endpoint to ensure the jwt tokens are handled correctly.
- The health resource should be already implemented by your container providing the microprofile-health api, although you may need to provide
it under the correct context path.
- The user event resource contains 3 endpoints that will be called asynchronously. 

Applications configure their authentication mechanism to accept JWT signed using their key by this service issuer.
Refer to the JWT 1.1 spec or the examples of this project to figure out how to configure the JWT authentication.
You will need to provide the trusted public key as well as the accepted token issuer, at least.
The application public key can be provided as an url: `/app/${appName}/publicKey`

### Example application
Building the project with the `example` profile active produces a payaramicro executable jar deploying `authenticator-web`
and another one deploying an example application. the authenticator service will be deployed on port 8443,
the example application on port 8444. The authenticator service will be preconfigured for a second application
deployed on port 8445 - to make use of this one, you have to generate a secret and specify the port and secret, for instance
by passing system properties: `-Dpayaramicro.sslPort=8445 -DappSecret=<generated app secret>`.

The example application exposes a /test/ servlet with a login/register form and displaying rest responses
and internal events.  

## Application domain

The service handles applications, (token signing) keys and users. They can be managed using the admin api.

Applications must be registered manually, and a secret token must be generated. The application is then deployed at 
the registered url, using its application secret to contact this service and trusting JWT tokens signed with its
signing key. Healthcheck endpoints are implemented on both side to ensure communication between the service works as
expected. Take care before using them as a docker healthcheck for instance: if both this service and the application
are waiting on each other to become healthy to consider itself healthy, none of them may ever be reachable.

Signing keys are stored in the database as well. They are bound to a single scope. Key scopes include:
* Keys intended to sign user token for this service audience (an user can request her information by means of the authenticator-api)
* Keys intended to sign application secrets for this service audience (application secrets are long-lived jwt tokens)
* Keys intended to sign token for an application audience (an user requesting a token for application X will be signed with application X key)
Although 1 key for each scope is probably sufficient, keys may be rotated and deactivated.

Users are expected to create accounts on applications. Users with an existing account may use
a rest resource to consult and update their data. User email verification is delegated to applications
which have the permission.


## License
MIT, do whatever you want

## Links
- An angular frontend for the admin api: https://github.com/cghislai/authenticator-admin-front
