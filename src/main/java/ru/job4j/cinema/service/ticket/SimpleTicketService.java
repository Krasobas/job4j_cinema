package ru.job4j.cinema.service.ticket;

import lombok.AllArgsConstructor;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.ticket.TicketRepository;
import ru.job4j.cinema.service.session.FilmSessionService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@ThreadSafe
@AllArgsConstructor
public class SimpleTicketService implements TicketService {
    @GuardedBy("this")
    private final TicketRepository ticketRepository;
    @GuardedBy("this")
    private final FilmSessionService filmSessionService;

    @Override
    public Collection<TicketDto> findByUserId(int userId) {
        return ticketRepository.findByUserId(userId)
                .stream()
                .map(this::getTicketDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public Collection<Ticket> findByFilmSessionId(int filmSessionId) {
        return ticketRepository.findByFilmSessionId(filmSessionId);
    }

    @Override
    public Optional<Ticket> findById(int id) {
        return ticketRepository.findById(id);
    }

    @Override
    public Optional<Ticket> save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Map<Integer, List<Integer>> findBusyPlacesByFilmSessionId(int filmSessionId) {
        return findByFilmSessionId(filmSessionId).stream()
                .collect(
                        Collectors.groupingBy(
                                Ticket::getRowNumber,
                                Collectors.mapping(Ticket::getPlaceNumber, Collectors.toList())
                        )
                );
    }

    private Optional<TicketDto> getTicketDto(Ticket ticket) {
        var filmSessionOpt = filmSessionService.findById(ticket.getSessionId());
        if (filmSessionOpt.isEmpty()) {
            return Optional.empty();
        }
        var filmSession = filmSessionOpt.get();
        var ticketDto = TicketDto.builder()
                .id(ticket.getId())
                .filmName(filmSession.getFilm().getName())
                .fileId(filmSession.getFilm().getFileId())
                .hall(filmSession.getHall())
                .startTime(filmSession.getStartTime())
                .endTime(filmSession.getEndTime())
                .rowNumber(ticket.getRowNumber())
                .placeNumber(ticket.getPlaceNumber())
                .userId(ticket.getUserId())
                .sessionId(ticket.getSessionId())
                .build();
        return Optional.of(ticketDto);

    }
}
