package ru.job4j.cinema.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.session.FilmSessionService;
import ru.job4j.cinema.service.ticket.TicketService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketControllerTest {
    private static TicketController controller;
    private static TicketService ticketService;
    private static FilmSessionService sessionService;

    @BeforeAll
    static void init() {
        ticketService = mock(TicketService.class);
        sessionService = mock(FilmSessionService.class);
        controller = new TicketController(ticketService, sessionService);
    }

    @AfterEach
    void resetMocks() {
        reset(ticketService, sessionService);
    }

    @Test
    void whenBuyThenRedirectToUserTickets() {
        var userId = 1;
        var expected = "redirect:/tickets/1";
        var ticket = new Ticket(1, 1, 1, 1, 1);
        var model = new ConcurrentModel();
        var ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        doReturn(Optional.of(ticket)).when(ticketService).save(ticketCaptor.capture());

        var got = controller.buy(ticket, model);

        assertThat(got).isEqualTo(expected);
        assertThat(ticketCaptor.getValue()).isEqualTo(ticket);
    }

    @Test
    void whenBuyAndNotSuccessThenRedirectToError() {
        var userId = 1;
        var expected = "errors/purchaseFailure";
        var ticket = new Ticket(1, 1, 1, 1, 1);
        var model = new ConcurrentModel();
        var ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        doReturn(Optional.empty()).when(ticketService).save(ticketCaptor.capture());

        var got = controller.buy(ticket, model);

        assertThat(got).isEqualTo(expected);
        assertThat(ticketCaptor.getValue()).isEqualTo(ticket);
        assertThat(model.getAttribute("error")).isEqualTo("Couldn't book the selected seat. It’s probably taken.");
    }

    @Test
    void whenGetPurchasePageThenRedirectToPurchaseWithFilmSessionAndBusyPalaces() {
        var expected = "tickets/purchase";
        var sessionId = 1;
        var time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        var model = new ConcurrentModel();
        var filmSession = new FilmSession(1, 1, 1, time, time, 100);
        var busyPlaces = Map.of(1, List.of(1, 2, 3));
        var sessionIdCaptor = ArgumentCaptor.forClass(Integer.class);
        var sessionIdCaptor2 = ArgumentCaptor.forClass(Integer.class);
        doReturn(Optional.of(filmSession)).when(sessionService).findById(sessionIdCaptor.capture());
        doReturn(busyPlaces).when(ticketService).findBusyPlacesByFilmSessionId(sessionIdCaptor2.capture());
        var got = controller.getPurchasePage(sessionId, model);

        assertThat(got).isEqualTo(expected);
        assertThat(sessionIdCaptor.getValue()).isEqualTo(sessionId);
        assertThat(sessionIdCaptor2.getValue()).isEqualTo(sessionId);
        assertThat(model.getAttribute("filmSession")).usingRecursiveComparison().isEqualTo(filmSession);
        assertThat(model.getAttribute("busyPlaces")).usingRecursiveComparison().isEqualTo(busyPlaces);
    }

    @Test
    void whenGetPurchasePageAndNotFoundThenRedirectToError() {
        var expected = "errors/404";
        var sessionId = 1;
        var model = new ConcurrentModel();
        var sessionIdCaptor = ArgumentCaptor.forClass(Integer.class);
        doReturn(Optional.empty()).when(sessionService).findById(sessionIdCaptor.capture());
        doReturn(Collections.emptyMap()).when(ticketService).findBusyPlacesByFilmSessionId(anyInt());

        var got = controller.getPurchasePage(sessionId, model);

        verify(ticketService, never()).findBusyPlacesByFilmSessionId(anyInt());
        assertThat(got).isEqualTo(expected);
        assertThat(sessionIdCaptor.getValue()).isEqualTo(sessionId);
        assertThat(model.getAttribute("error")).isEqualTo("Showtime not found");
    }

    @Test
    void whenListTicketsThenRedirectToList() {
        var expected = "tickets/list";
        var list = List.of(new Ticket());
        var userId = 1;
        var userIdCaptor = ArgumentCaptor.forClass(Integer.class);
        var model = new ConcurrentModel();
        doReturn(list).when(ticketService).findByUserId(userIdCaptor.capture());

        var got = controller.listTickets(userId, model);

        assertThat(got).isEqualTo(expected);
        assertThat(userIdCaptor.getValue()).isEqualTo(userId);
        assertThat(model.getAttribute("tickets")).usingRecursiveComparison().isEqualTo(list);
    }
}