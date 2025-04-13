package ru.job4j.cinema.service.genre;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.genre.GenreRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class SimpleGenreServiceTest {
    private static SimpleGenreService service;
    private static GenreRepository genreRepository;

    @BeforeAll
    static void init() {
        genreRepository = mock(GenreRepository.class);
        service = new SimpleGenreService(genreRepository);
    }

    @Test
    void whnFindAllThenOk() {
        var expected = List.of(new Genre(0, "first"), new Genre(1, "second"));

        doReturn(expected).when(genreRepository).findAll();

        var got = service.findAll();

        assertThat(got).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindByIdThenOk() {
        var expected = new Genre(0, "name");

        doReturn(Optional.of(expected)).when(genreRepository).findById(0);

        var got = service.findById(0);

        assertThat(got).isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}