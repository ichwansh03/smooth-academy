package org.ichwan.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.ichwan.entity.User;
import org.ichwan.service.UserService;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @POST
    @Path("/register")
    public Response register(Map<String, String> body) {
        User user = userService.register(
                body.get("email"),
                body.get("password"),
                body.get("displayName"));
        return Response.created(URI.create("/api/users/" + user.getId()))
                .entity(user)
                .build();
    }

    @POST
    @Path("/login")
    public Response login(Map<String, String> body) {
        try {
            User user = userService.login(
                    body.get("email"),
                    body.get("password"));
            return Response.ok(user).build();
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 401) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Map.of("error", "Email atau password salah"))
                        .build();
            }
            throw e;
        }
    }

    @GET
    @Path("/by-email/{email}")
    public User getUserByEmail(@PathParam("email") String email) {
        return userService.findByEmail(email);
    }

    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") UUID id) {
        return userService.findById(id);
    }
}
