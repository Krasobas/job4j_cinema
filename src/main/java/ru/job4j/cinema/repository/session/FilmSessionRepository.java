package ru.job4j.cinema.repository.session;


import ru.job4j.cinema.model.FilmSession;

import java.util.Collection;
import java.util.Optional;

public interface FilmSessionRepository {
    Collection<FilmSession> findAll();

    Optional<FilmSession> findById(int id);

    Optional<FilmSession> save(FilmSession session);

    boolean deleteById(int id);
}
