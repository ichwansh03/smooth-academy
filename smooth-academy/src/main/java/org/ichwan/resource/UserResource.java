package org.ichwan.resource;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.ichwan.entity.User;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource implements PanacheRepositoryBase<User, UUID> {

    @POST
    @Path("/register")
    @Transactional
    public Response register(Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String displayName = body.get("displayName");

        if (email == null || password == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        if (find("email", email).firstResult() != null) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        User user = new User();
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setPasswordHash(hashPassword(password));
        persist(user);

        return Response.created(URI.create("/api/users/" + user.getId()))
                .entity(user)
                .build();
    }

    @GET
    @Path("/by-email/{email}")
    public User getUserByEmail(@PathParam("email") String email) {
        User user = find("email", email).firstResult();
        if (user == null) {
            throw new WebApplicationException(404);
        }
        return user;
    }

    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") UUID id) {
        return findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException(404));
    }

    private String hashPassword(String password) {
        return password;
    }
}
