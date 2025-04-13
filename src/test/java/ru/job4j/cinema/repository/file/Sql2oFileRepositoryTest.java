package ru.job4j.cinema.repository.file;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.File;

import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oFileRepositoryTest {
    private static Sql2oFileRepository repository;

    @BeforeAll
    static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oFileRepository.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        repository = new Sql2oFileRepository(sql2o);
    }

    @Test
    void whenAddThenFindByIdAndDeleteOk() {
        var file = new File(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        var saved = repository.save(file);
        var got = repository.findById(saved.getId());
        assertThat(got)
                .isPresent()
                .contains(file);
        repository.deleteById(saved.getId());
        got = repository.findById(saved.getId());
        assertThat(got).isEmpty();
    }
}