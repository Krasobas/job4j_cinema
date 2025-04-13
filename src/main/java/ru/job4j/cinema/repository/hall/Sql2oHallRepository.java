package ru.job4j.cinema.repository.hall;

import lombok.AllArgsConstructor;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Hall;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@ThreadSafe
@AllArgsConstructor
public class Sql2oHallRepository implements HallRepository {
    @GuardedBy("this")
    private final Sql2o sql2o;

    @Override
    public Collection<Hall> findAll() {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("SELECT * FROM halls")) {
                return query.setColumnMappings(Hall.COLUMN_MAPPING)
                        .executeAndFetch(Hall.class);
            }
        }
    }

    @Override
    public Optional<Hall> findById(int id) {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("SELECT * FROM halls WHERE id = :id")) {
                var hall = query.addParameter("id", id)
                        .setColumnMappings(Hall.COLUMN_MAPPING)
                        .executeAndFetchFirst(Hall.class);
                return Optional.ofNullable(hall);
            }
        }
    }

    @Override
    public Optional<Hall> save(Hall hall) {
        try (var connection = sql2o.open()) {
            var sql = """
                      INSERT INTO halls (name, row_count, place_count, description)
                      VALUES (:name, :rowCount, :placeCount, :description)
                      """;
            try (var query = connection.createQuery(sql, true)) {
                int generatedId = query.addParameter("name", hall.getName())
                        .addParameter("rowCount", hall.getRowCount())
                        .addParameter("placeCount", hall.getPlaceCount())
                        .addParameter("description", hall.getDescription())
                        .executeUpdate()
                        .getKey(Integer.class);
                hall.setId(generatedId);
                return Optional.of(hall);
            }
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("DELETE FROM halls WHERE id = :id")) {
                 int affectedRows = query.addParameter("id", id)
                        .executeUpdate()
                        .getResult();
                 return affectedRows > 0;
            }
        }
    }
}
