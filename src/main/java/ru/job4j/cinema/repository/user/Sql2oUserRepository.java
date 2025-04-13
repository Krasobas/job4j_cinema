package ru.job4j.cinema.repository.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.model.User;

import java.util.Optional;

@Repository
@ThreadSafe
@Log4j2
@AllArgsConstructor
public class Sql2oUserRepository implements UserRepository {
    @GuardedBy("this")
    private final Sql2o sql2o;

    @Override
    public Optional<User> save(User user) {
        try (var connection = sql2o.open()) {
            var sql = """
                      INSERT INTO users (full_name, email, password)
                      VALUES (:fullName, :email, :password)
                      """;
            try (var query = connection.createQuery(sql, true)) {
                int generatedId = query.addParameter("fullName", user.getFullName())
                        .addParameter("email", user.getEmail())
                        .addParameter("password", user.getPassword())
                        .executeUpdate()
                        .getKey(Integer.class);
                user.setId(generatedId);
                return Optional.of(user);
            } catch (Sql2oException e) {
                log.error(e);
            }
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("SELECT * FROM users WHERE email = :email AND password = :password")) {
                 var user = query.addParameter("email", email)
                        .addParameter("password", password)
                        .setColumnMappings(User.COLUMN_MAPPING)
                        .executeAndFetchFirst(User.class);
                 return Optional.ofNullable(user);
            }
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (var connection = sql2o.open()) {
            try (var query = connection.createQuery("DELETE FROM users WHERE id = :id")) {
                int affectedRows = query.addParameter("id", id)
                        .executeUpdate()
                        .getResult();
                return affectedRows > 0;
            }
        }
    }
}
