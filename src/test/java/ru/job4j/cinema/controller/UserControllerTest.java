package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.user.UserService;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserControllerTest {
    private static UserController controller;
    private static UserService userService;

    @BeforeAll
    static void init() {
        userService = mock(UserService.class);
        controller = new UserController(userService);
    }

    @Test
    void whenGetLoginPageThenOk() {
        var expected = "users/login";
        var got = controller.getLoginPage();
        assertThat(got).isEqualTo(expected);
    }

    @Test
    void whenLoginThenRedirectToSessions() {
        var user = new User(1, "fullName", "email", "pwd");
        var model = new ConcurrentModel();
        var request = new MockHttpServletRequest();
        var emailCaptor = ArgumentCaptor.forClass(String.class);
        var pwdCaptor = ArgumentCaptor.forClass(String.class);
        doReturn(Optional.of(user)).when(userService).findByEmailAndPassword(emailCaptor.capture(), pwdCaptor.capture());

        var got = controller.login(user, model, request);

        assertThat(got).isEqualTo("redirect:/sessions");
        assertThat(emailCaptor.getValue()).isEqualTo(user.getEmail());
        assertThat(pwdCaptor.getValue()).isEqualTo(user.getPassword());
        assertThat(Objects.requireNonNull(request.getSession()).getAttribute("user")).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void whenLoginAndNotFoundThenRedirectToLogin() {
        var user = new User(1, "fullName", "email", "pwd");
        var model = new ConcurrentModel();
        var request = new MockHttpServletRequest();
        var emailCaptor = ArgumentCaptor.forClass(String.class);
        var pwdCaptor = ArgumentCaptor.forClass(String.class);
        doReturn(Optional.empty()).when(userService).findByEmailAndPassword(emailCaptor.capture(), pwdCaptor.capture());

        var got = controller.login(user, model, request);

        assertThat(got).isEqualTo("users/login");
        assertThat(emailCaptor.getValue()).isEqualTo(user.getEmail());
        assertThat(pwdCaptor.getValue()).isEqualTo(user.getPassword());
        assertThat(model.getAttribute("error")).isEqualTo("Account not found, please enter a valid email or password");
    }

    @Test
    void whenGetRegistrationPageThenOk() {
        var expected = "users/register";
        var got = controller.getRegistrationPage();
        assertThat(got).isEqualTo(expected);
    }

    @Test
    void whenRegisterThenRedirectToLogin() {
        var user = new User(1, "fullName", "email", "pwd");
        var model = new ConcurrentModel();
        var userCaptor = ArgumentCaptor.forClass(User.class);
        doReturn(Optional.of(user)).when(userService).save(userCaptor.capture());

        var got = controller.register(user, model);

        assertThat(got).isEqualTo("redirect:/users/login");
        assertThat(userCaptor.getValue()).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void whenRegisterAndUserExistsThenRedirectBackToRegistration() {
        var user = new User(1, "fullName", "email", "pwd");
        var model = new ConcurrentModel();
        var userCaptor = ArgumentCaptor.forClass(User.class);
        doReturn(Optional.empty()).when(userService).save(userCaptor.capture());

        var got = controller.register(user, model);

        assertThat(got).isEqualTo("/users/register");
        assertThat(userCaptor.getValue()).usingRecursiveComparison().isEqualTo(user);
        assertThat(model.getAttribute("error")).isEqualTo("User with this email already exists");
    }

    @Test
    void whenLogoutThenSessionIsInvalidatedAndRedirectToRoot() {
        var expected = "redirect:/";
        var session = mock(HttpSession.class);
        doNothing().when(session).invalidate();

        var got = controller.logout(session);
        assertThat(got).isEqualTo(expected);
        verify(session).invalidate();
    }
}