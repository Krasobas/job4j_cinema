package ru.job4j.cinema.repository.session;

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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.fail;


class Sql2oFilmSessionRepositoryTest {
    private static Sql2oFilmSessionRepository repository;
    private static Sql2oFileRepository sql2oFileRepository;
    private static Sql2oGenreRepository sql2oGenreRepository;
    private static Sql2oFilmRepository sql2oFilmRepository;
    private static Sql2oHallRepository sql2oHallRepository;
    private static File file;
    private static Genre genre;
    private static Film film;
    private static Hall hall;

    @BeforeAll
    static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oFilmSessionRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        repository = new Sql2oFilmSessionRepository(sql2o);
        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
        sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
        sql2oHallRepository = new Sql2oHallRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);

        file = sql2oFileRepository.save(new File(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        genre = sql2oGenreRepository.save(new Genre(0, "name")).get();
        film = sql2oFilmRepository.save(new Film(
                0, "name", "description", 2025, genre.getId(), 10, 60, file.getId()
        )).get();
        hall = sql2oHallRepository.save(new Hall(0, "name", 10, 10, "description")).get();
    }

    @AfterAll
    static void deleteDependencies() {
        sql2oHallRepository.deleteById(hall.getId());
        sql2oFilmRepository.deleteById(film.getId());
        sql2oGenreRepository.deleteById(genre.getId());
        sql2oFileRepository.deleteById(file.getId());
    }

    @AfterEach
    void clear() {
        var sessions = repository.findAll();
        for (var session : sessions) {
            repository.deleteById(session.getId());
        }
    }

    @Test
    void whenSaveAndFindAllThenContainsFilmSession() {
        var now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        var filmSession = repository.save(new FilmSession(
                0, film.getId(), hall.getId(), now, now, 100
        ));
        if (filmSession.isEmpty()) {
            fail("session not added");
        }
        var filmSessions = repository.findAll();

        assertThat(filmSessions).contains(filmSession.get());
    }

    @Test
    void whenSaveManyThenGetAll() {
        var now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        var first = repository.save(new FilmSession(
                0, film.getId(), hall.getId(), now, now, 100
        ));
        var second = repository.save(new FilmSession(
                0, film.getId(), hall.getId(), now, now, 50
        ));
        var third = repository.save(new FilmSession(
                0, film.getId(), hall.getId(), now, now, 10
        ));

        var filmSessions = repository.findAll();
        var added = Stream.of(first, second, third).filter(Optional::isPresent).map(Optional::get).toList();
        assertThat(added).hasSize(3);
        assertThat(filmSessions).containsAll(added);
    }

    @Test
    void whenSaveThenFindById() {
        var now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        var saved = repository.save(new FilmSession(
                0, film.getId(), hall.getId(), now, now, 100
        ));
        if (saved.isEmpty()) {
            fail("session not added");
        }
        var got = repository.findById(saved.get().getId()).get();

        assertThat(got).usingRecursiveComparison().isEqualTo(saved.get());
    }

}