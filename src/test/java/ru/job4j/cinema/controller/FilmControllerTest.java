package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.service.film.FilmService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class FilmControllerTest {

    private static FilmController controller;
    private static FilmService filmService;

    @BeforeAll
    static void init() {
        filmService = mock(FilmService.class);
        controller = new FilmController(filmService);
    }

    @Test
    void whenGetAllThenGetListAndRedirectToList() {
        var expectedView = "films/list";
        var expected = List.of(new Film());
        var model = new ConcurrentModel();
        doReturn(expected).when(filmService).findAll();

        var gotView = controller.getAll(model);

        assertThat(gotView).isEqualTo(expectedView);
        assertThat(model.getAttribute("films")).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenGetFilmThenRedirectToOne() {
        var expected = "films/one";
        var film = new Film();
        var model = new ConcurrentModel();
        var idCaptor = ArgumentCaptor.forClass(Integer.class);
        doReturn(Optional.of(film)).when(filmService).findById(idCaptor.capture());

        var got = controller.getFilm(1, model);

        assertThat(got).isEqualTo(expected);
        assertThat(idCaptor.getValue()).isEqualTo(1);
        assertThat(model.getAttribute("film")).usingRecursiveComparison().isEqualTo(film);
    }

    @Test
    void whenGetFilmAndNotFoundThenRedirectToError() {
        var expected = "errors/404";
        var model = new ConcurrentModel();
        var idCaptor = ArgumentCaptor.forClass(Integer.class);
        doReturn(Optional.empty()).when(filmService).findById(idCaptor.capture());

        var got = controller.getFilm(1, model);

        assertThat(got).isEqualTo(expected);
        assertThat(idCaptor.getValue()).isEqualTo(1);
        assertThat(model.getAttribute("error")).isEqualTo("Film not found");
    }
}