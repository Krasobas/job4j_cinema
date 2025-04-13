package ru.job4j.cinema.repository.films;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.file.Sql2oFileRepository;
import ru.job4j.cinema.repository.genre.Sql2oGenreRepository;
import ru.job4j.cinema.repository.hall.Sql2oHallRepository;

import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.util.AssertionErrors.fail;

class Sql2oFilmRepositoryTest {
    private static Sql2oFilmRepository repository;
    private static Sql2oFileRepository sql2oFileRepository;
    private static Sql2oGenreRepository sql2oGenreRepository;
    private static File file;
    private static Genre genre;

    @BeforeAll
    static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oFilmRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        repository = new Sql2oFilmRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);
        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);

        file = new File(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        sql2oFileRepository.save(file);
        genre = sql2oGenreRepository.save(new Genre(0, "name")).get();
    }

    @AfterAll
    static void deleteDependencies() {
        sql2oFileRepository.deleteById(file.getId());
        sql2oGenreRepository.deleteById(genre.getId());
    }

    @AfterEach
    void clear() {
        var films = repository.findAll();
        for (var film : films) {
            repository.deleteById(film.getId());
        }
    }


    @Test
    void whenSaveAndFindAllThenContains() {
        var saved = repository.save(new Film(
                0, "name", "description", 2025, genre.getId(), 10, 60, file.getId()
        ));
        if (saved.isEmpty()) {
            fail("session not added");
        }
        var gotList = repository.findAll();

        assertThat(gotList).contains(saved.get());
    }

    @Test
    void whenSaveThenFindById() {
        var saved = repository.save(new Film(
                0, "name", "description", 2025, genre.getId(), 10, 60, file.getId()
        ));
        if (saved.isEmpty()) {
            fail("session not added");
        }
        var got = repository.findById(saved.get().getId());

        assertThat(got).isPresent().get().usingRecursiveComparison().isEqualTo(saved.get());
    }

    @Test
    void whenSaveManyThenGetAll() {
        var first = repository.save(new Film(
                0, "name", "description", 2025, genre.getId(), 10, 60, file.getId()
        ));
        var second = repository.save(new Film(
                0, "name2", "description2", 2025, genre.getId(), 10, 60, file.getId()
        ));
        var third = repository.save(new Film(
                0, "name3", "description3", 2025, genre.getId(), 10, 60, file.getId()
        ));

        var gotList = repository.findAll();
        var added = Stream.of(first, second, third).filter(Optional::isPresent).map(Optional::get).toList();
        assertThat(added).hasSize(3);
        assertThat(gotList).containsAll(added);
    }
}