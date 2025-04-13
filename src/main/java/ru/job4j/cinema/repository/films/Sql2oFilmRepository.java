package ru.job4j.cinema.repository.films;

import lombok.AllArgsConstructor;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@Profile("sql2o")
@ThreadSafe
@AllArgsConstructor
public class Sql2oFilmRepository implements FilmRepository {
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

    @Override
    public Optional<Film> save(Film film) {
        try (var connection = sql2o.open()) {
            var sql = """
                      INSERT INTO films (name, description, "year", genre_id, minimal_age, duration_in_minutes, file_id)
                      VALUES (:name, :description, :year, :genreId, :minimalAge, :durationInMinutes, :fileId)
                      """;
            try (var query = connection.createQuery(sql, true)) {
                int generatedId = query.addParameter("name", film.getName())
                        .addParameter("description", film.getDescription())
                        .addParameter("year", film.getYear())
                        .addParameter("genreId", film.getGenreId())
                        .addParameter("minimalAge", film.getMinimalAge())
                        .addParameter("durationInMinutes", film.getDurationInMinutes())
                        .addParameter("fileId", film.getFileId())
                        .executeUpdate()
                        .getKey(Integer.class);
                film.setId(generatedId);
                return Optional.of(film);
            }
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("DELETE FROM films WHERE id = :id")) {
                query.addParameter("id", id);
                int affectedRows = query.executeUpdate().getResult();
                return affectedRows > 0;
            }
        }
    }
}
