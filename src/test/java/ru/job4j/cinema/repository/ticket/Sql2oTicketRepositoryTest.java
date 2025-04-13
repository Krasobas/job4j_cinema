package ru.job4j.cinema.repository.ticket;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.*;
import ru.job4j.cinema.repository.file.Sql2oFileRepository;
import ru.job4j.cinema.repository.films.Sql2oFilmRepository;
import ru.job4j.cinema.repository.genre.Sql2oGenreRepository;
import ru.job4j.cinema.repository.hall.Sql2oHallRepository;
import ru.job4j.cinema.repository.session.Sql2oFilmSessionRepository;
import ru.job4j.cinema.repository.user.Sql2oUserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class Sql2oTicketRepositoryTest {
    private static Sql2oTicketRepository repository;
    private static Sql2oFileRepository sql2oFileRepository;
    private static Sql2oGenreRepository sql2oGenreRepository;
    private static Sql2oFilmRepository sql2oFilmRepository;
    private static Sql2oHallRepository sql2oHallRepository;
    private static Sql2oFilmSessionRepository sql2oFilmSessionRepository;
    private static Sql2oUserRepository sql2oUserRepository;
    private static File file;
    private static Genre genre;
    private static Film film;
    private static Hall hall;
    private static LocalDateTime time;
    private static FilmSession filmSession;
    private static User user;

    @BeforeAll
    static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oTicketRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        repository = new Sql2oTicketRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);
        sql2oFilmSessionRepository = new Sql2oFilmSessionRepository(sql2o);
        sql2oUserRepository = new Sql2oUserRepository(sql2o);
        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
        sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
        sql2oHallRepository = new Sql2oHallRepository(sql2o);

        file = sql2oFileRepository.save(new File(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        genre = sql2oGenreRepository.save(new Genre(0, "name")).get();
        film = sql2oFilmRepository.save(new Film(
                0, "name", "description", 2025, genre.getId(), 10, 60, file.getId()
        )).get();
        hall = sql2oHallRepository.save(new Hall(0, "name", 10, 10, "description")).get();
        time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        filmSession = sql2oFilmSessionRepository.save(new FilmSession(
                0, film.getId(), hall.getId(), time, time, 100
        )).get();
        user = sql2oUserRepository.save(new User(0, "fullName", "email", "password")).get();
    }

    @AfterAll
    static void deleteDependencies() {
        sql2oFilmSessionRepository.deleteById(filmSession.getId());
        sql2oHallRepository.deleteById(hall.getId());
        sql2oUserRepository.deleteById(user.getId());
        sql2oFilmRepository.deleteById(film.getId());
        sql2oGenreRepository.deleteById(genre.getId());
        sql2oFileRepository.deleteById(file.getId());

    }

    @AfterEach
    void clear() {
        var tickets = repository.findAll();
        for (var ticket : tickets) {
            repository.deleteById(ticket.getId());
        }
    }

    @Test
    void whenSaveThenFindById() {
        var saved = repository.save(new Ticket(
                0, filmSession.getId(), 10, 10, user.getId()
        ));

        var found = repository.findById(saved.get().getId());

        assertThat(found.get()).usingRecursiveComparison().isEqualTo(saved.get());
    }

    @Test
    void whenSaveThenFindByUserId() {
        var first = repository.save(new Ticket(
                0, filmSession.getId(), 10, 10, user.getId()
        ));

        var second = repository.save(new Ticket(
                0, filmSession.getId(), 10, 11, user.getId()
        ));

        var expected = List.of(first.get(), second.get());

        var found = repository.findByUserId(user.getId());

        assertThat(found).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenSaveThenFindByFilmSessionId() {
        var first = repository.save(new Ticket(
                0, filmSession.getId(), 10, 10, user.getId()
        ));

        var second = repository.save(new Ticket(
                0, filmSession.getId(), 10, 11, user.getId()
        ));

        var expected = List.of(first.get(), second.get());

        var found = repository.findByFilmSessionId(filmSession.getId());

        assertThat(found).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenSaveThenFindAll() {
        var first = repository.save(new Ticket(
                0, filmSession.getId(), 10, 10, user.getId()
        ));

        var second = repository.save(new Ticket(
                0, filmSession.getId(), 10, 11, user.getId()
        ));

        var expected = List.of(first.get(), second.get());

        var found = repository.findAll();

        assertThat(found).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenDeleteThenNotFound() {
        var saved = repository.save(new Ticket(
                0, filmSession.getId(), 10, 10, user.getId()
        ));

        repository.deleteById(saved.get().getId());

        var found = repository.findById(saved.get().getId());

        assertThat(found).isEmpty();
    }

    @Test
    void whenNotExistsAndDeleteThenFalse() {
        var got = repository.deleteById(111);
        assertThat(got).isFalse();
    }


}