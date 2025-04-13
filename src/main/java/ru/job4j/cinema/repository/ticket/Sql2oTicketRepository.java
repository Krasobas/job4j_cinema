package ru.job4j.cinema.repository.ticket;

import lombok.AllArgsConstructor;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Ticket;

import java.util.Collection;
import java.util.Optional;

@Repository
@ThreadSafe
@AllArgsConstructor
public class Sql2oTicketRepository implements TicketRepository {
    @GuardedBy("this")
    private final Sql2o sql2o;

    @Override
    public Collection<Ticket> findAll() {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("SELECT * FROM tickets")) {
                return query
                        .setColumnMappings(Ticket.COLUMN_MAPPING)
                        .executeAndFetch(Ticket.class);
            }
        }
    }

    @Override
    public Collection<Ticket> findByUserId(int userId) {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("SELECT * FROM tickets WHERE user_id = :userId")) {
                return query.addParameter("userId", userId)
                        .setColumnMappings(Ticket.COLUMN_MAPPING)
                        .executeAndFetch(Ticket.class);
            }
        }
    }

    @Override
    public Collection<Ticket> findByFilmSessionId(int filmSessionId) {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("SELECT * FROM tickets WHERE session_id = :filmSessionId")) {
                return query.addParameter("filmSessionId", filmSessionId)
                        .setColumnMappings(Ticket.COLUMN_MAPPING)
                        .executeAndFetch(Ticket.class);
            }
        }
    }

    @Override
    public Optional<Ticket> findById(int id) {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("SELECT * FROM tickets WHERE id = :id")) {
                var ticket = query.addParameter("id", id)
                        .setColumnMappings(Ticket.COLUMN_MAPPING)
                        .executeAndFetchFirst(Ticket.class);
                return Optional.ofNullable(ticket);
            }
        }
    }

    @Override
    public Optional<Ticket> save(Ticket ticket) {
        try (var connection = sql2o.open()) {
            var sql = """
                      INSERT INTO tickets(user_id, row_number, place_number, session_id)
                      VALUES(:userId, :rowNumber, :placeNumber, :sessionId)
                      """;
            try (var query = connection.createQuery(sql, true)) {
                int generatedId = query.addParameter("userId", ticket.getUserId())
                        .addParameter("rowNumber", ticket.getRowNumber())
                        .addParameter("placeNumber", ticket.getPlaceNumber())
                        .addParameter("sessionId", ticket.getSessionId())
                        .executeUpdate()
                        .getKey(Integer.class);
                ticket.setId(generatedId);
                return Optional.of(ticket);
            }
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("DELETE FROM tickets WHERE id = :id")) {
                int affectedRows = query.addParameter("id", id)
                        .executeUpdate()
                        .getResult();
                return affectedRows > 0;
            }
        }
    }
}
