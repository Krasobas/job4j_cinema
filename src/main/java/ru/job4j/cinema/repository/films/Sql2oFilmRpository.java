package ru.job4j.cinema.repository.films;

import lombok.AllArgsConstructor;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Film;

import java.util.Collection;
import java.util.Optional;

@Repository
@Profile("sql2o")
@ThreadSafe
@AllArgsConstructor
public class Sql2oFilmRpository implements FilmRepository {
    @GuardedBy("this")
    private final Sql2o sql2o;

    @Override
    public Collection<Film> findAll() {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("SELECT * FROM films")) {
                return query.setColumnMappings(Film.COLUMN_MAPPING)
                        .executeAndFetch(Film.class);
            }
        }
    }

    @Override
    public Optional<Film> findById(int id) {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("SELECT * FROM films WHERE id = :id")) {
                var film = query.addParameter("id", id)
                        .setColumnMappings(Film.COLUMN_MAPPING)
                        .executeAndFetchFirst(Film.class);
                return Optional.ofNullable(film);
            }
        }
    }
}
