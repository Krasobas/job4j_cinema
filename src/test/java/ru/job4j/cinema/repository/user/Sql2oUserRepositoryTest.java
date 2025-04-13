package ru.job4j.cinema.repository.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.file.Sql2oFileRepository;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository repository;

    @BeforeAll
    static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        repository = new Sql2oUserRepository(sql2o);
    }

    @Test
    void whenSaveThenFindByEmailAndPasswordOk() {
        var saved = repository.save(new User(1, "fullName", "email", "password"));

        var got = repository.findByEmailAndPassword("email", "password");

        assertThat(got).isPresent();

        assertThat(got.get()).usingRecursiveComparison().isEqualTo(saved.get());
        assertThat(repository.deleteById(saved.get().getId())).isTrue();
    }
}