package ru.job4j.cinema.service.film;

import lombok.AllArgsConstructor;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.films.FilmRepository;
import ru.job4j.cinema.service.genre.GenreService;

import java.util.Collection;
import java.util.Optional;

@Service
@ThreadSafe
@AllArgsConstructor
public class SimpleFilmService implements FilmService {
    @GuardedBy("this")
    private final FilmRepository filmRepository;
    @GuardedBy("this")
    private final GenreService genreService;

    @Override
    public Collection<Film> findAll() {
        return filmRepository.findAll();
    }

    @Override
    public Optional<FilmDto> findById(int id) {
        return filmRepository.findById(id)
                .map(this::getFilmDto);
    }

    private FilmDto getFilmDto(Film film) {
        var genre = genreService.findById(film.getGenreId());
        return new FilmDto(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getYear(),
                genre.orElseGet(() -> new Genre(0, "Unknown")),
                film.getMinimalAge(),
                humanizeDuration(film.getDurationInMinutes()),
                film.getFileId()
        );
    }

    private static String humanizeDuration(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        if (hours > 0) {
            return String.format("%dh %d min", hours, minutes);
        } else {
            return String.format("%d min", minutes);
        }
    }
}
