package ru.job4j.cinema.service.session;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.session.FilmSessionRepository;
import ru.job4j.cinema.service.film.FilmService;
import ru.job4j.cinema.service.hall.HallService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class SimpleFilmSessionServiceTest {
    private static SimpleFilmSessionService service;
    private static FilmSessionRepository filmSessionRepository;
    private static FilmService filmService;
    private static HallService hallService;
    private static FilmDto filmDto;
    private static Hall hall;
    private static FilmSession filmSession;

    @BeforeAll
    static void init() {
        filmSessionRepository = mock(FilmSessionRepository.class);
        filmService = mock(FilmService.class);
        hallService = mock(HallService.class);
        service = new SimpleFilmSessionService(filmSessionRepository, filmService, hallService);
        filmDto = new FilmDto(1, "name", "description", 2025, new Genre(1, "genre"), 18, "1h 6 min", 1);
        hall = new Hall(1, "name", 10, 10, "descripption");
        var time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        filmSession = new FilmSession(0, 1, 1, time, time, 100);
    }

    @BeforeEach
    void mockGetFilmSessionDto() {
        doReturn(Optional.of(filmDto)).when(filmService).findById(anyInt());
        doReturn(Optional.of(hall)).when(hallService).findById(anyInt());
    }

    @Test
    void whenFindAllThenOk() {
        var expected = new FilmSessionDto(
                filmSession.getId(),
                filmDto,
                hall,
                filmSession.getStartTime(),
                filmSession.getEndTime(),
                filmSession.getPrice()
        );
        doReturn(List.of(filmSession)).when(filmSessionRepository).findAll();

        var got = service.findAll();

        assertThat(got).usingRecursiveComparison().isEqualTo(List.of(expected));
    }

    @Test
    void whenFindByIdThenOk() {
        var expected = new FilmSessionDto(
                filmSession.getId(),
                filmDto,
                hall,
                filmSession.getStartTime(),
                filmSession.getEndTime(),
                filmSession.getPrice()
        );
        doReturn(Optional.of(filmSession)).when(filmSessionRepository).findById(anyInt());

        var got = service.findById(1);

        assertThat(got).isPresent()
                .get()
                .usingRecursiveComparison().isEqualTo(expected);
    }
}