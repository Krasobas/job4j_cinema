package ru.job4j.cinema.repository.hall;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Hall;

import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class Sql2oHallRepositoryTest {
    private static Sql2oHallRepository repository;

    @BeforeAll
    static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oHallRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        repository = new Sql2oHallRepository(sql2o);
    }

    @AfterEach
    void clear() {
        var sessions = repository.findAll();
        for (var session : sessions) {
            repository.deleteById(session.getId());
        }
    }

    @Test
    void whenSaveThenFindById() {
        var saved = repository.save(new Hall(0, "name", 10, 10, "description"));

        assertThat(saved).isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(repository.findById(saved.get().getId()).get());
    }

    @Test
    void whenSaveManyAndGetAllThenContainsAll() {
        var first = repository.save(new Hall(0, "name", 10, 10, "description"));
        var second = repository.save(new Hall(0, "name2", 10, 10, "description"));
        var third = repository.save(new Hall(0, "name3", 10, 10, "description"));

        var saved = Stream.of(first, second, third).filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        var gotList = repository.findAll();

        assertThat(gotList).hasSize(3)
                .containsAll(saved);
    }

    @Test
    void whenDeleteThenNotFound() {
        var saved = repository.save(new Hall(0, "name", 10, 10, "description"));

        repository.deleteById(saved.get().getId());
        var found = repository.findById(saved.get().getId());

        assertThat(found).isEmpty();
    }
}