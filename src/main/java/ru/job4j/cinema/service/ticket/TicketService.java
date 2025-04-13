package ru.job4j.cinema.service.ticket;

import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.Ticket;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface TicketService {
    Collection<TicketDto> findByUserId(int userId);

    Collection<Ticket> findByFilmSessionId(int filmSessionId);

    Optional<Ticket> findById(int id);

    Optional<Ticket> save(Ticket ticket);

    Map<Integer, List<Integer>> findBusyPlacesByFilmSessionId(int filmSessionId);
}
