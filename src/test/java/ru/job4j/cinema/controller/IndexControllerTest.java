package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class IndexControllerTest {
    private static IndexController controller;

    @BeforeAll
    static void init() {
        controller = new IndexController();
    }

    @Test
    void whenGetIndexThenOk() {
        var expected = "index";
        var got = controller.getIndex();

        assertThat(got).isEqualTo(expected);
    }
}