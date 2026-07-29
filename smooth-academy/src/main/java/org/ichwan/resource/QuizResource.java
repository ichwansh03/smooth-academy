package org.ichwan.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.ichwan.entity.QuizResult;
import org.ichwan.service.QuizService;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
@Path("/quiz-results")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QuizResource {

    @Inject
    QuizService quizService;

    @POST
    public Response submitResult(Map<String, Object> body) {
        QuizResult result = quizService.submitResult(
                UUID.fromString(body.get("userId").toString()),
                Integer.valueOf(body.get("levelId").toString()),
                body.get("mode").toString(),
                Integer.valueOf(body.get("totalQuestions").toString()),
                Integer.valueOf(body.get("correctCount").toString()));
        return Response.created(URI.create("/api/quiz-results/" + result.getId()))
                .entity(result)
                .build();
    }

    @GET
    @Path("/user/{userId}")
    public List<QuizResult> getUserResults(@PathParam("userId") UUID userId) {
        return quizService.getUserResults(userId);
    }

    @GET
    @Path("/{id}")
    public QuizResult getResult(@PathParam("id") UUID id) {
        return quizService.findById(id);
    }
}
