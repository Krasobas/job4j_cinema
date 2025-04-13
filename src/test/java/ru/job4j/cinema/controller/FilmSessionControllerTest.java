package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.service.session.FilmSessionService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class FilmSessionControllerTest {
    private static FilmSessionController controller;
    private static FilmSessionService sessionService;

    @BeforeAll
    static void init() {
        sessionService = mock(FilmSessionService.class);
        controller = new FilmSessionController(sessionService);
    }

    @Test
    void whenGetAllThenGetListAndRedirectToList() {
        var expected = "sessions/list";
        var list = List.of(new FilmSession());
        var model = new ConcurrentModel();

        doReturn(list).when(sessionService).findAll();

        var got = controller.getAll(model);

        assertThat(got).isEqualTo(expected);
        assertThat(model.getAttribute("filmSessions")).usingRecursiveComparison().isEqualTo(list);
    }
}