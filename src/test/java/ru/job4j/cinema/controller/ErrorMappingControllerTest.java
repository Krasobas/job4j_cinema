package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ErrorMappingControllerTest {
    private static ErrorMappingController controller;

    @BeforeAll
    static void init() {
        controller = new ErrorMappingController();
    }

    @Test
    void whenHandleErrorThenAddErrorMessageAndRedirectToError() {
        var expected = "errors/404";

        var model = new ConcurrentModel();

        var got = controller.handleError(model);

        assertThat(got).isEqualTo(expected);
        assertThat(model.getAttribute("error")).isEqualTo("We are sorry, something went wrong :( ");
    }
}