package org.ichwan.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.ichwan.entity.Level;

@ApplicationScoped
public class LevelRepository implements PanacheRepositoryBase<Level, Integer> {
}
