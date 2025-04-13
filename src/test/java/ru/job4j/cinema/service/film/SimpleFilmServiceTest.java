package ru.job4j.cinema.service.film;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.films.FilmRepository;
import ru.job4j.cinema.service.genre.GenreService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class SimpleFilmServiceTest {
    private static SimpleFilmService service;
    private static FilmRepository filmRepository;
    private static GenreService genreService;

    @BeforeAll
    static void init() {
        filmRepository = mock(FilmRepository.class);
        genreService = mock(GenreService.class);
        service = new SimpleFilmService(filmRepository, genreService);
    }

    @Test
    void whenFindAllThenOk() {
        var expected = List.of(new Film());
        doReturn(expected).when(filmRepository).findAll();

        var got = service.findAll();

        assertThat(got).isEqualTo(expected);
    }

    @Test
    void whenFindByIdThenOk() {
        var film = new Film();
        film.setDurationInMinutes(65);
        var genre = Optional.of(new Genre(0, "name"));
        var expected = new FilmDto(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getYear(),
                genre.get(),
                film.getMinimalAge(),
                "1h 5 min",
                film.getFileId()
        );

        doReturn(genre).when(genreService).findById(anyInt());
        doReturn(Optional.of(film)).when(filmRepository).findById(anyInt());

        var got = service.findById(0);

        assertThat(got).isPresent()
                .get()
                .usingRecursiveComparison().isEqualTo(expected);
    }
}