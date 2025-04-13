package ru.job4j.cinema.repository.genre;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Genre;

import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oGenreRepositoryTest {
    private static Sql2oGenreRepository repository;

    @BeforeAll
    static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oGenreRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        repository = new Sql2oGenreRepository(sql2o);
    }

    @AfterEach
    void clear() {
        var sessions = repository.findAll();
        for (var session : sessions) {
            repository.deleteById(session.getId());
        }
    }

    @Test
    void whenSaveAndFindAllThenContains() {
        var saved = repository.save(new Genre(0, "name"));
        var gotList = repository.findAll();

        assertThat(saved).isPresent();
        assertThat(gotList).contains(saved.get());
    }

    @Test
    void whenSaveThenFindById() {
        var saved = repository.save(new Genre(0, "name"));
        var got = repository.findById(saved.get().getId());
        assertThat(got).isPresent().get().usingRecursiveComparison().isEqualTo(saved.get());
    }

    @Test
    void whenDeleteByIdThenNotFoundById() {
        var saved = repository.save(new Genre(0, "name"));
        repository.deleteById(saved.get().getId());
        var got = repository.findById(saved.get().getId());
        assertThat(got).isEmpty();
    }
}