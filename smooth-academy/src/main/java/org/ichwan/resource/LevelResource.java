package org.ichwan.resource;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.ichwan.entity.Level;

import java.util.List;

@ApplicationScoped
@Path("/levels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LevelResource implements PanacheRepositoryBase<Level, Integer> {

    @GET
    public List<Level> listAllLevels() {
        return list("ORDER BY sort_order");
    }

    @GET
    @Path("/{id}")
    public Level getLevel(@PathParam("id") Integer id) {
        return findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException(404));
    }
}
