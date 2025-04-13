package ru.job4j.cinema.service.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.user.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class SimpleUserServiceTest {
    private static SimpleUserService service;
    private static UserRepository userRepository;
    private static User user;

    @BeforeAll
    static void init() {
        userRepository = mock(UserRepository.class);
        service = new SimpleUserService(userRepository);
        user = new User(1, "fullName", "email", "pwd");
    }

    @Test
    void whenSaveThenGetUser() {
        doReturn(Optional.of(user)).when(userRepository).save(any(User.class));

        var got = service.save(user);
        assertThat(got).isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    void findByEmailAndPassword() {
        doReturn(Optional.of(user)).when(userRepository).findByEmailAndPassword(anyString(), anyString());

        var got = service.findByEmailAndPassword("email", "pwd");

        assertThat(got).isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }
}