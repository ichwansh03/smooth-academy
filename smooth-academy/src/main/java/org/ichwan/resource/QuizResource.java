package org.ichwan.resource;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.ichwan.entity.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
@Path("/quiz-results")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QuizResource implements PanacheRepositoryBase<QuizResult, UUID> {

    @Inject
    UserResource userResource;

    @Inject
    LevelResource levelResource;

    @POST
    @Transactional
    public Response submitResult(Map<String, Object> body) {
        UUID userId = UUID.fromString(body.get("userId").toString());
        Integer levelId = Integer.valueOf(body.get("levelId").toString());
        String mode = body.get("mode").toString();
        Integer totalQuestions = Integer.valueOf(body.get("totalQuestions").toString());
        Integer correctCount = Integer.valueOf(body.get("correctCount").toString());

        User user = userResource.findById(userId);
        if (user == null) throw new WebApplicationException(404);

        Level level = levelResource.findById(levelId);
        if (level == null) throw new WebApplicationException(404);

        BigDecimal percentage = BigDecimal.valueOf(correctCount.doubleValue() / totalQuestions * 100)
                .setScale(2, java.math.RoundingMode.HALF_UP);

        int starsEarned;
        if (percentage.compareTo(BigDecimal.valueOf(90)) >= 0) starsEarned = 3;
        else if (percentage.compareTo(BigDecimal.valueOf(70)) >= 0) starsEarned = 2;
        else if (percentage.compareTo(BigDecimal.valueOf(50)) >= 0) starsEarned = 1;
        else starsEarned = 0;

        QuizResult result = new QuizResult();
        result.setUser(user);
        result.setLevel(level);
        result.setMode(mode);
        result.setTotalQuestions(totalQuestions);
        result.setCorrectCount(correctCount);
        result.setPercentage(percentage);
        result.setStarsEarned(starsEarned);
        persist(result);

        return Response.created(URI.create("/api/quiz-results/" + result.getId()))
                .entity(result)
                .build();
    }

    @GET
    @Path("/user/{userId}")
    public List<QuizResult> getUserResults(@PathParam("userId") UUID userId) {
        return list("user_id = ?1 ORDER BY created_at DESC", userId);
    }

    @GET
    @Path("/{id}")
    public QuizResult getResult(@PathParam("id") UUID id) {
        return findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException(404));
    }
}
