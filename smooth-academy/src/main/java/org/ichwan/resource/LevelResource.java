package org.ichwan.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.ichwan.entity.Level;
import org.ichwan.service.LevelService;

import java.util.List;

@ApplicationScoped
@Path("/levels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LevelResource {

    @Inject
    LevelService levelService;

    @GET
    public List<Level> listAllLevels() {
        return levelService.listAll();
    }

    @GET
    @Path("/{id}")
    public Level getLevel(@PathParam("id") Integer id) {
        return levelService.findById(id);
    }
}
