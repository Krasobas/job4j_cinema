package ru.job4j.cinema.service.hall;

import ru.job4j.cinema.model.Hall;

import java.util.Collection;
import java.util.Optional;

public interface HallService {
    Collection<Hall> findAll();

    Optional<Hall> findById(int id);
}
