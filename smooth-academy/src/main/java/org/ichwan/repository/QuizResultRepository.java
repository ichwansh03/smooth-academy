package org.ichwan.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.ichwan.entity.QuizResult;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class QuizResultRepository implements PanacheRepositoryBase<QuizResult, UUID> {

    public List<QuizResult> findByUserId(UUID userId) {
        return list("user_id = ?1 ORDER BY created_at DESC", userId);
    }
}
