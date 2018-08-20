package com.charlyghislain.authenticator.example.app;

import com.charlyghislain.authenticator.management.api.domain.WsApplicationUser;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/example")
@RolesAllowed(ApplicationRoles.EXAMPLE_ROLE)
@RequestScoped
public class ExampleResourceController {

    @Inject
    private Instance<JsonWebToken> jsonWebToken;
    @Inject
    @Claim("uid")
    private Instance<Long> uid;
    @Inject
    private AuthenticatorManagementClient managementClient;

    @GET
    @Path("/jwt")
    public JsonWebToken getToken() {
        return jsonWebToken.get();
    }

    @GET
    @Path("/user")
    public WsApplicationUser getUser() {
        return this.managementClient.getUser(this.uid.get());
    }
}
