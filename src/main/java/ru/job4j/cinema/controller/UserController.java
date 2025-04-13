package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.user.UserService;

@Controller
@RequestMapping("/users")
@ThreadSafe
@AllArgsConstructor
public class UserController {
    @GuardedBy("this")
    private final UserService userService;

    @GetMapping("/login")
    public String getLoginPage() {
        return "users/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") User user, Model model, HttpServletRequest request) {
        var found = userService.findByEmailAndPassword(user.getEmail(), user.getPassword());
        if (found.isEmpty()) {
            model.addAttribute("error", "Account not found, please enter a valid email or password");
            return "users/login";
        }
        var session = request.getSession();
        session.setAttribute("user", found.get());
        return "redirect:/sessions";
    }

    @GetMapping("/register")
    public String getRegistrationPage() {
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        var result = userService.save(user);
        if (result.isEmpty()) {
            model.addAttribute("error", "User with this email already exists");
            return "errors/404";
        }
        return "redirect:/users/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}
