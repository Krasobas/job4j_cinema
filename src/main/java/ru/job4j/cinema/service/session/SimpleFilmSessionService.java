package ru.job4j.cinema.service.session;

import lombok.AllArgsConstructor;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.session.FilmSessionRepository;
import ru.job4j.cinema.service.film.FilmService;
import ru.job4j.cinema.service.hall.HallService;
import ru.job4j.cinema.service.ticket.TicketService;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@ThreadSafe
@AllArgsConstructor
public class SimpleFilmSessionService implements FilmSessionService {
    @GuardedBy("this")
    private final FilmSessionRepository filmSessionRepository;
    @GuardedBy("this")
    private final FilmService filmService;
    @GuardedBy("this")
    private final HallService hallService;

    @Override
    public Collection<FilmSessionDto> findAll() {
        return  filmSessionRepository.findAll()
                .stream()
                .map(this::getFilmSessionDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public Optional<FilmSessionDto> findById(int id) {
        return filmSessionRepository.findById(id)
                .map(this::getFilmSessionDto)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private Optional<FilmSessionDto> getFilmSessionDto(FilmSession filmSession) {
        var filmDto = filmService.findById(filmSession.getFilmId());
        if (filmDto.isEmpty()) {
            return Optional.empty();
        }
        var hall = hallService.findById(filmSession.getHallId());
        if (hall.isEmpty()) {
            return Optional.empty();
        }
        var filmSessionDto = new FilmSessionDto(
                filmSession.getId(),
                filmDto.get(),
                hall.get(),
                filmSession.getStartTime(),
                filmSession.getEndTime(),
                filmSession.getPrice()
        );
        return Optional.of(filmSessionDto);
    }
}
