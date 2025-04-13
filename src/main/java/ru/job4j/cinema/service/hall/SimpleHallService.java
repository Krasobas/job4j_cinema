package ru.job4j.cinema.service.hall;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.hall.HallRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@ThreadSafe
@AllArgsConstructor
public class SimpleHallService implements HallService {
    @GuardedBy("this")
    private final HallRepository hallRepository;

    @Override
    public Collection<Hall> findAll() {
        return hallRepository.findAll();
    }

    @Override
    public Optional<Hall> findById(int id) {
        return hallRepository.findById(id);
    }
}
