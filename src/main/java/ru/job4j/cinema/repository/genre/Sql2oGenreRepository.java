package ru.job4j.cinema.repository.genre;

import lombok.AllArgsConstructor;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Repository
@Profile("sql2o")
@ThreadSafe
@AllArgsConstructor
public class Sql2oGenreRepository implements GenreRepository {
    @GuardedBy("this")
    private final Sql2o sql2o;

    @Override
    public Collection<Genre> findAll() {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("SELECT * FROM genres")) {
                return query.executeAndFetch(Genre.class);
            }
        }
    }

    @Override
    public Optional<Genre> findById(int id) {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("SELECT * FROM genres WHERE id = :id")) {
                var genre = query.addParameter("id", id).executeAndFetchFirst(Genre.class);
                return Optional.ofNullable(genre);
            }
        }
    }
}
