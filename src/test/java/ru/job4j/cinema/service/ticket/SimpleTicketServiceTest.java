package ru.job4j.cinema.service.ticket;

import net.jcip.annotations.GuardedBy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.ticket.TicketRepository;
import ru.job4j.cinema.service.session.FilmSessionService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class SimpleTicketServiceTest {

    private static SimpleTicketService service;
    private static TicketRepository ticketRepository;
    private static FilmSessionService filmSessionService;
    private static Ticket ticket;
    private static FilmSessionDto filmSessionDto;

    @BeforeAll
    static void init() {
        ticketRepository = mock(TicketRepository.class);
        filmSessionService = mock(FilmSessionService.class);
        service = new SimpleTicketService(ticketRepository, filmSessionService);
        ticket = new Ticket(1, 1, 10, 10, 1);
        var time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        filmSessionDto = new FilmSessionDto(1, new FilmDto(), new Hall(), time, time, 100);
    }

    private void mockGetTicketDto() {
        doReturn(Optional.of(filmSessionDto)).when(filmSessionService).findById(anyInt());
    }

    @Test
    void whenFindByUserIdThenGetListOfTicketDto() {
        mockGetTicketDto();
        doReturn(List.of(ticket)).when(ticketRepository).findByUserId(anyInt());
        var expected = TicketDto.builder()
                .id(ticket.getId())
                .filmName(filmSessionDto.getFilm().getName())
                .fileId(filmSessionDto.getFilm().getFileId())
                .hall(filmSessionDto.getHall())
                .startTime(filmSessionDto.getStartTime())
                .endTime(filmSessionDto.getEndTime())
                .rowNumber(ticket.getRowNumber())
                .placeNumber(ticket.getPlaceNumber())
                .userId(ticket.getUserId())
                .sessionId(ticket.getSessionId())
                .build();

        var got = service.findByUserId(1);

        assertThat(got).usingRecursiveComparison().isEqualTo(List.of(expected));
    }

    @Test
    void whenFindByFilmSessionIdThenGetListOfTickets() {
        var expected = List.of(ticket);
        doReturn(expected).when(ticketRepository).findByFilmSessionId(anyInt());

        var got = service.findByFilmSessionId(1);

        assertThat(got).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindByIdThenGetTicket() {
        doReturn(Optional.of(ticket)).when(ticketRepository).findById(anyInt());
        var got = service.findById(1);

        assertThat(got).isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(ticket);
    }

    @Test
    void whenSaveThenGetTicket() {
        doReturn(Optional.of(ticket)).when(ticketRepository).save(any(Ticket.class));

        var got = service.save(ticket);

        assertThat(got).isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(ticket);
    }

    @Test
    void findBusyPlacesByFilmSessionId() {
        doReturn(List.of(ticket)).when(ticketRepository).findByFilmSessionId(anyInt());

        var expected = Map.of(ticket.getRowNumber(), List.of(ticket.getPlaceNumber()));

        var got = service.findBusyPlacesByFilmSessionId(1);

        assertThat(got).usingRecursiveComparison().isEqualTo(expected);
    }
}