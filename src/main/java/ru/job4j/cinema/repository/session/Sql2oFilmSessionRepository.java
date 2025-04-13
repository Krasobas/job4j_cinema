package ru.job4j.cinema.repository.session;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.model.FilmSession;

import java.util.Collection;
import java.util.Optional;

@Repository
@Profile("sql2o")
@Log4j2
@ThreadSafe
@AllArgsConstructor
public class Sql2oFilmSessionRepository implements FilmSessionRepository {
    @GuardedBy("this")
    private final Sql2o sql2o;

    @Override
    public Collection<FilmSession> findAll() {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("SELECT * FROM film_sessions")) {
                return query.setColumnMappings(FilmSession.COLUMN_MAPPING)
                        .executeAndFetch(FilmSession.class);
            }
        }
    }

    @Override
    public Optional<FilmSession> findById(int id) {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("SELECT * FROM film_sessions WHERE id = :id")) {
                var filmSession = query.addParameter("id", id)
                        .setColumnMappings(FilmSession.COLUMN_MAPPING)
                        .executeAndFetchFirst(FilmSession.class);
                return Optional.ofNullable(filmSession);
            }
        }
    }

    @Override
    public Optional<FilmSession> save(FilmSession filmSession) {
        try (var connection = sql2o.open()) {
            var sql = """
                      INSERT INTO film_sessions(film_id, halls_id, start_time, end_time, price)
                      VALUES(:filmId, :hallId, :startTime, :endTime, :price)
                      """;
            try (var query = connection.createQuery(sql, true)) {
                int generatedId = query
                        .addParameter("filmId", filmSession.getFilmId())
                        .addParameter("hallId", filmSession.getHallId())
                        .addParameter("startTime", filmSession.getStartTime())
                        .addParameter("endTime", filmSession.getEndTime())
                        .addParameter("price", filmSession.getPrice())
                        .executeUpdate()
                        .getKey(Integer.class);
                filmSession.setId(generatedId);
                return Optional.of(filmSession);
            } catch (Sql2oException e) {
                log.error(e);
            }
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("DELETE FROM film_sessions WHERE id = :id")) {
                query.addParameter("id", id);
                int affectedRows = query.executeUpdate().getResult();
                return affectedRows > 0;
            }
        }
    }
}
