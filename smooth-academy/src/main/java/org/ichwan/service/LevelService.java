package org.ichwan.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.ichwan.entity.Level;
import org.ichwan.repository.LevelRepository;

import java.util.List;

@ApplicationScoped
public class LevelService {

    @Inject
    LevelRepository levelRepository;

    public List<Level> listAll() {
        return levelRepository.list("ORDER BY sort_order");
    }

    public Level findById(Integer id) {
        return levelRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException(404));
    }
}
