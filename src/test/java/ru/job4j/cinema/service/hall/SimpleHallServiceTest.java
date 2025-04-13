package ru.job4j.cinema.service.hall;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.hall.HallRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class SimpleHallServiceTest {
    private static SimpleHallService service;
    private static HallRepository hallRepository;
    private static Hall hall;

    @BeforeAll
    static void init() {
        hallRepository = mock(HallRepository.class);
        service = new SimpleHallService(hallRepository);
        hall = new Hall(1, "name", 10, 10, "description");
    }

    @Test
    void whenFindAllThenGetListOfHalls() {
        var expected = List.of(hall);
        doReturn(expected).when(hallRepository).findAll();

        var got = service.findAll();

        assertThat(got).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindByIdThenGetHall() {
        doReturn(Optional.of(hall)).when(hallRepository).findById(anyInt());

        var got = service.findById(1);

        assertThat(got).isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(hall);
    }
}