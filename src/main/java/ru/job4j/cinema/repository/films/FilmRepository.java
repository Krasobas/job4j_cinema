package ru.job4j.cinema.repository.films;

import ru.job4j.cinema.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmRepository {
    Collection<Film> findAll();

    Optional<Film> findById(int id);
}
